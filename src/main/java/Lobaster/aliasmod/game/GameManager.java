package Lobaster.aliasmod.game;

import Lobaster.aliasmod.Aliasmod;
import Lobaster.aliasmod.networking.payload.*;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class GameManager {
    private static final Map<UUID, GameRoom> activeRooms = new ConcurrentHashMap<>();

    public static void createRoom(ServerPlayerEntity host, String themeName, int teamCount) {
        GameRoom newRoom = new GameRoom(host, themeName, teamCount);
        activeRooms.put(newRoom.getRoomId(), newRoom);
        broadcastLobbyState(newRoom, host.getServer());
    }

    public static void joinRoom(ServerPlayerEntity player, UUID roomId) {
        GameRoom room = activeRooms.get(roomId);
        if (room == null || room.getGameState() != GameRoom.GameState.LOBBY) {
            return;
        }
        Team smallestTeam = room.getTeams().stream().min(Comparator.comparingInt(team -> team.getPlayerUuids().size())).orElse(null);
        if (smallestTeam != null) {
            room.addPlayer(player, smallestTeam.getTeamId());
            broadcastLobbyState(room, player.getServer());
        }
    }

    public static void removePlayerFromRoom(ServerPlayerEntity player, UUID roomId) {
        GameRoom room = activeRooms.get(roomId);
        if (room == null) return;
        room.removePlayer(player);
        if (room.isEmpty()) {
            activeRooms.remove(roomId);
        } else {
            broadcastLobbyState(room, player.getServer());
        }
    }

    public static void resyncPlayer(ServerPlayerEntity player, UUID roomId) {
        GameRoom room = activeRooms.get(roomId);
        if (room == null) return;
        MinecraftServer server = player.getServer();
        if (room.getGameState() == GameRoom.GameState.IN_GAME) {
            broadcastGameRoundState(room, server, player);
        } else if (room.getGameState() == GameRoom.GameState.LOBBY) {
            broadcastLobbyState(room, server, player);
        }
    }

    public static void onPlayerDisconnect(ServerPlayerEntity player) {
        findRoomByPlayer(player.getUuid()).ifPresent(room -> removePlayerFromRoom(player, room.getRoomId()));
    }

    public static void changePlayerTeam(ServerPlayerEntity player, UUID roomId, int teamId) {
        GameRoom room = activeRooms.get(roomId);
        if (room != null) {
            room.movePlayerToTeam(player, teamId);
            broadcastLobbyState(room, player.getServer());
        }
    }

    public static void startGame(ServerPlayerEntity player, UUID roomId, MinecraftServer server) {
        GameRoom room = activeRooms.get(roomId);
        if (room == null || !room.getHostId().equals(player.getUuid()) || !room.canStartGame()) return;
        room.startGame(server);
    }

    public static void tick(MinecraftServer server) {
        activeRooms.values().forEach(room -> room.tick(server));
    }

    public static void handlePlayerAction(ServerPlayerEntity player, UUID roomId, PlayerActionC2SPayload.ActionType action) {
        GameRoom room = activeRooms.get(roomId);
        if (room != null) {
            room.processPlayerAction(player, action, player.getServer());
        }
    }

    public static void sendWordUpdate(GameRoom room, MinecraftServer server) {
        room.setNextWord();
        broadcastGameRoundState(room, server);
    }

    public static void startNextRound(GameRoom room, MinecraftServer server) {
        room.startNewRound();
        sendWordUpdate(room, server);
    }

    public static void broadcastGameRoundState(GameRoom room, MinecraftServer server) {
        room.getAllPlayers(server).forEach(player -> broadcastGameRoundState(room, server, player));
    }

    public static void broadcastGameRoundState(GameRoom room, MinecraftServer server, ServerPlayerEntity targetPlayer) {
        ServerPlayerEntity activePlayer = room.getCurrentPlayer(server);
        if (activePlayer == null) return;
        String word = room.getCurrentWord();
        int team1Score = room.getTeamScore(1);
        int team2Score = room.getTeamScore(2);
        String wordToShow = targetPlayer.equals(activePlayer) ? word : "******";
        var payload = new GameRoundS2CPayload(activePlayer.getName().getString(), team1Score, team2Score, wordToShow);
        ServerPlayNetworking.send(targetPlayer, payload);
    }

    public static void broadcastLobbyState(GameRoom room, MinecraftServer server) {
        room.getAllPlayers(server).forEach(player -> broadcastLobbyState(room, server, player));
    }

    public static void broadcastLobbyState(GameRoom room, MinecraftServer server, ServerPlayerEntity targetPlayer) {
        Map<Integer, List<String>> teamPlayersMap = room.getTeams().stream().collect(Collectors.toMap(Team::getTeamId, team -> team.getPlayerUuids().stream().map(uuid -> server.getPlayerManager().getPlayer(uuid)).filter(Objects::nonNull).map(p -> p.getName().getString()).collect(Collectors.toList())));
        boolean canStart = room.canStartGame();
        var payload = new LobbyStateS2CPayload(room.getRoomId(), room.getHostId(), teamPlayersMap, canStart);
        ServerPlayNetworking.send(targetPlayer, payload);
    }

    public static void broadcastTimerUpdate(GameRoom room, int remainingSeconds, MinecraftServer server) {
        var payload = new TimerTickS2CPayload(remainingSeconds);
        room.getAllPlayers(server).forEach(player -> ServerPlayNetworking.send(player, payload));
    }

    public static void broadcastGameOver(GameRoom room, Team winningTeam, MinecraftServer server) {
        Text winnerText = Text.literal("Команда " + winningTeam.getTeamId()).formatted(Formatting.GOLD);
        var payload = new GameOverS2CPayload(winnerText);
        room.getAllPlayers(server).forEach(player -> ServerPlayNetworking.send(player, payload));
        activeRooms.remove(room.getRoomId());
    }

    public static List<RoomInfo> getRoomInfos(MinecraftServer server) {
        return activeRooms.values().stream().map(room -> room.toRoomInfo(server)).collect(Collectors.toList());
    }

    private static Optional<GameRoom> findRoomByPlayer(UUID playerUuid) {
        return activeRooms.values().stream().filter(r -> r.getAllPlayersUuids().contains(playerUuid)).findFirst();
    }
}
