package Lobaster.aliasmod.client.gui;

import Lobaster.aliasmod.client.AliasmodClient;
import Lobaster.aliasmod.networking.payload.PlayerActionC2SPayload;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class GameGui extends LightweightGuiDescription {
    private final WLabel timerLabel;

    public GameGui(String activePlayerName, int team1Score, int team2Score, String wordToShow) {
        WGridPanel root = new WGridPanel();
        setRootPanel(root);
        root.setInsets(Insets.ROOT_PANEL);

        root.add(new WLabel(Text.literal("Пояснює: ").append(Text.literal(activePlayerName).formatted(Formatting.YELLOW))), 0, 0, 8, 1);
        root.add(new WLabel(Text.literal("Рахунок: ").append(Text.literal(team1Score + " - " + team2Score).formatted(Formatting.GREEN))), 0, 1, 8, 1);
        this.timerLabel = new WLabel(Text.literal("Час: ").append(Text.literal("60").formatted(Formatting.RED)));
        root.add(timerLabel, 12, 0);

        WLabel wordLabel = new WLabel(Text.literal(wordToShow).formatted(Formatting.BOLD, Formatting.AQUA));
        wordLabel.setHorizontalAlignment(HorizontalAlignment.CENTER);
        root.add(wordLabel, 0, 4, 18, 2);

        WButton guessedButton = new WButton(Text.literal("Вгадано"));
        guessedButton.setOnClick(() -> ClientPlayNetworking.send(new PlayerActionC2SPayload(AliasmodClient.currentRoomId, PlayerActionC2SPayload.ActionType.GUESSED)));
        root.add(guessedButton, 2, 7, 6, 1);

        WButton skipButton = new WButton(Text.literal("Пропустити"));
        skipButton.setOnClick(() -> ClientPlayNetworking.send(new PlayerActionC2SPayload(AliasmodClient.currentRoomId, PlayerActionC2SPayload.ActionType.SKIPPED)));
        root.add(skipButton, 10, 7, 6, 1);

        boolean isActivePlayer = MinecraftClient.getInstance().player.getName().getString().equals(AliasmodClient.activePlayerName);
        guessedButton.setEnabled(isActivePlayer);
        skipButton.setEnabled(isActivePlayer);

        root.validate(this);
    }

    public void updateTimer(int seconds) { this.timerLabel.setText(Text.literal("Час: ").append(Text.literal(String.valueOf(seconds)).formatted(Formatting.RED))); }
}
