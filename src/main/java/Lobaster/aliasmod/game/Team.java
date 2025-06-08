package Lobaster.aliasmod.game;

import net.minecraft.server.network.ServerPlayerEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Team {
    private final int teamId;
    private int score;
    private final List<UUID> playerUuids;

    public Team(int teamId) {
        this.teamId = teamId;
        this.score = 0;
        this.playerUuids = new ArrayList<>();
    }

    public void addPlayer(ServerPlayerEntity player) {
        if (!playerUuids.contains(player.getUuid())) {
            this.playerUuids.add(player.getUuid());
        }
    }

    public void removePlayer(UUID playerUuid) {
        this.playerUuids.remove(playerUuid);
    }

    public void addScore(int points) { this.score += points; }
    public int getScore() { return score; }
    public List<UUID> getPlayerUuids() { return playerUuids; }
    public int getTeamId() { return teamId; }
}
