package Lobaster.aliasmod.game;

import Lobaster.aliasmod.ThemeManager;
import Lobaster.aliasmod.networking.payload.PlayerActionC2SPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import java.util.*;
import java.util.stream.Collectors;

public class GameRoom {
    public enum GameState { LOBBY, IN_GAME, FINISHED }

    private UUID hostId;
    private final UUID roomId;
    private final String themeName;
    private final int maxPlayersPerTeam;
    private GameState gameState;
    private final List<Team> teams;
    private final List<UUID> allPlayers;
    private int currentTeamIndex = 0;
    private int currentPlayerIndexInTeam = 0;
    private List<String> wordsForGame;
    private int roundTimerTicks;
    private String currentWord;
    private static final int TICKS_PER_SECOND = 20;
    private static final int ROUND_DURATION_SECONDS = 60;
    private static final int WIN_SCORE = 100;

    public GameRoom(ServerPlayerEntity host, String themeName, int teamCount) {
        this.roomId = UUID.randomUUID();
        this.hostId = host.getUuid();
        this.themeName = themeName;
        this.gameState = GameState.LOBBY;
        this.teams = new ArrayList<>();
        this.allPlayers = new ArrayList<>();
        this.maxPlayersPerTeam = 8;
        for (int i = 0; i < teamCount; i++) {
            teams.add(new Team(i + 1));
        }
        addPlayer(host, 1);
    }

    public void startGame(MinecraftServer server) {
        if (this.gameState == GameState.IN_GAME) return;
        this.gameState = GameState.IN_GAME;
        this.wordsForGame = new ArrayList<>(ThemeManager.getWordsForTheme(this.themeName));
        Collections.shuffle(this.wordsForGame);
        GameManager.startNextRound(this, server);
    }

    public void tick(MinecraftServer server) {
        if (this.gameState != GameState.IN_GAME || roundTimerTicks <= 0) return;
        roundTimerTicks--;
        if (roundTimerTicks > 0 && roundTimerTicks % TICKS_PER_SECOND == 0) {
            GameManager.broadcastTimerUpdate(this, roundTimerTicks / TICKS_PER_SECOND, server);
        }
        if (roundTimerTicks <= 0) {
            advanceTurn();
            GameManager.startNextRound(this, server);
        }
    }

    public void startNewRound() { this.roundTimerTicks = ROUND_DURATION_SECONDS * TICKS_PER_SECOND; }

    public void processPlayerAction(ServerPlayerEntity player, PlayerActionC2SPayload.ActionType action, MinecraftServer server) {
        ServerPlayerEntity currentPlayer = getCurrentPlayer(server);
        if (currentPlayer == null || !player.getUuid().equals(currentPlayer.getUuid()) || gameState != GameState.IN_GAME) return;
        if (action == PlayerActionC2SPayload.ActionType.GUESSED) {
            Team playerTeam = getPlayerTeam(player.getUuid());
            if (playerTeam != null) {
                playerTeam.addScore(1);
                if (playerTeam.getScore() >= WIN_SCORE) {
                    endGame(server, playerTeam);
                    return;
                }
            }
        } else if (action == PlayerActionC2SPayload.ActionType.SKIPPED) {
            roundTimerTicks -= 10 * TICKS_PER_SECOND;
            if (roundTimerTicks < 0) roundTimerTicks = 0;
            GameManager.broadcastTimerUpdate(this, roundTimerTicks / TICKS_PER_SECOND, server);
        }
        GameManager.sendWordUpdate(this, server);
    }

    private void endGame(MinecraftServer server, Team winningTeam) {
        this.gameState = GameState.FINISHED;
        GameManager.broadcastGameOver(this, winningTeam, server);
    }

    private void advanceTurn() {
        currentTeamIndex = (currentTeamIndex + 1) % teams.size();
        if (currentTeamIndex == 0) {
            currentPlayerIndexInTeam++;
        }
    }

    public ServerPlayerEntity getCurrentPlayer(MinecraftServer server) {
        if (teams.isEmpty()) return null;
        for (int i = 0; i < teams.size(); i++) {
            Team team = teams.get(currentTeamIndex);
            if (!team.getPlayerUuids().isEmpty()) {
                int playerIndex = currentPlayerIndexInTeam % team.getPlayerUuids().size();
                return server.getPlayerManager().getPlayer(team.getPlayerUuids().get(playerIndex));
            }
            currentTeamIndex = (currentTeamIndex + 1) % teams.size();
        }
        return null;
    }

    public void setNextWord() {
        this.currentWord = wordsForGame.isEmpty() ? "СЛОВА ЗАКІНЧИЛИСЬ" : wordsForGame.remove(0);
    }

    public String getCurrentWord() { return this.currentWord != null ? this.currentWord : ""; }

    public void addPlayer(ServerPlayerEntity player, int teamId) {
        if (allPlayers.contains(player.getUuid())) {
            movePlayerToTeam(player, teamId);
        } else {
            teams.stream().filter(team -> team.getTeamId() == teamId).findFirst().ifPresent(team -> {
                team.addPlayer(player);
                allPlayers.add(player.getUuid());
            });
        }
    }

    public void removePlayer(ServerPlayerEntity player) {
        allPlayers.remove(player.getUuid());
        teams.forEach(team -> team.removePlayer(player.getUuid()));
        if (hostId.equals(player.getUuid()) && !allPlayers.isEmpty()) {
            hostId = allPlayers.get(0);
        }
    }

    public void movePlayerToTeam(ServerPlayerEntity player, int newTeamId) {
        teams.forEach(team -> team.removePlayer(player.getUuid()));
        teams.stream().filter(team -> team.getTeamId() == newTeamId).findFirst().ifPresent(team -> team.addPlayer(player));
    }

    public Team getPlayerTeam(UUID playerUuid) { return teams.stream().filter(team -> team.getPlayerUuids().contains(playerUuid)).findFirst().orElse(null); }
    public int getTeamScore(int teamId) { return teams.stream().filter(t -> t.getTeamId() == teamId).findFirst().map(Team::getScore).orElse(0); }
    public List<ServerPlayerEntity> getAllPlayers(MinecraftServer server) { return allPlayers.stream().map(uuid -> server.getPlayerManager().getPlayer(uuid)).filter(Objects::nonNull).collect(Collectors.toList()); }

    public RoomInfo toRoomInfo(MinecraftServer server) {
        ServerPlayerEntity host = server.getPlayerManager().getPlayer(hostId);
        String hostName = (host != null) ? host.getName().getString() : "N/A";
        return new RoomInfo(roomId, hostName, allPlayers.size(), teams.size() * maxPlayersPerTeam, this.gameState);
    }

    public boolean canStartGame() { return teams.stream().filter(team -> !team.getPlayerUuids().isEmpty()).count() >= 2; }
    public boolean isEmpty() { return allPlayers.isEmpty(); }
    public UUID getRoomId() { return roomId; }
    public UUID getHostId() { return hostId; }
    public List<Team> getTeams() { return teams; }
    public List<UUID> getAllPlayersUuids() { return allPlayers; }
    public GameState getGameState() { return gameState; }
}