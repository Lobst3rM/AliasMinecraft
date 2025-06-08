package Lobaster.aliasmod.client.gui;

import Lobaster.aliasmod.client.AliasmodClient;
import Lobaster.aliasmod.networking.payload.ChangeTeamC2SPayload;
import Lobaster.aliasmod.networking.payload.LeaveRoomC2SPayload;
import Lobaster.aliasmod.networking.payload.StartGameC2SPayload;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class LobbyGui extends LightweightGuiDescription {
    public LobbyGui(UUID roomId) {
        WGridPanel root = new WGridPanel();
        setRootPanel(root);
        root.setInsets(Insets.ROOT_PANEL);

        UUID selfUuid = MinecraftClient.getInstance().player.getUuid();
        root.add(new WLabel(Text.literal("Лобі кімнати")), 0, 0, 6, 1);

        WGridPanel teamsPanel = new WGridPanel();
        Map<Integer, List<String>> lobbyState = AliasmodClient.currentLobbyState;

        if (lobbyState != null && !lobbyState.isEmpty()) {
            int yPos = 0;
            for (Map.Entry<Integer, List<String>> entry : lobbyState.entrySet()) {
                int teamId = entry.getKey();
                teamsPanel.add(new WLabel(Text.literal("Команда " + teamId).formatted(Formatting.BOLD)), 0, yPos);
                WButton joinButton = new WButton(Text.literal("Приєднатись"));
                joinButton.setOnClick(() -> ClientPlayNetworking.send(new ChangeTeamC2SPayload(roomId, teamId)));
                teamsPanel.add(joinButton, 8, yPos++, 5, 1);

                if (entry.getValue().isEmpty()) {
                    teamsPanel.add(new WLabel(Text.literal(" (порожньо)").formatted(Formatting.GRAY)), 1, yPos++);
                } else {
                    for (String playerName : entry.getValue()) {
                        teamsPanel.add(new WLabel(Text.literal(" - " + playerName)), 1, yPos++);
                    }
                }
                yPos++;
            }
        }

        WScrollPanel scrollPanel = new WScrollPanel(teamsPanel);
        root.add(scrollPanel, 0, 2, 16, 9);

        WButton startGameButton = new WButton(Text.literal("Почати гру"));
        startGameButton.setOnClick(() -> ClientPlayNetworking.send(new StartGameC2SPayload(roomId)));
        if (selfUuid.equals(AliasmodClient.currentRoomHostId)) {
            root.add(startGameButton, 0, 12, 5, 1);
            startGameButton.setEnabled(AliasmodClient.canStartGame);
        }

        WButton leaveButton = new WButton(Text.literal("Вийти"));
        leaveButton.setOnClick(() -> {
            ClientPlayNetworking.send(new LeaveRoomC2SPayload(roomId));
            AliasmodClient.gameStatus = AliasmodClient.ClientGameStatus.NONE;
            AliasmodClient.currentRoomId = null;
            MinecraftClient.getInstance().setScreen(new MainMenuScreen());
        });
        root.add(leaveButton, 6, 12, 5, 1);

        root.validate(this);
    }
}