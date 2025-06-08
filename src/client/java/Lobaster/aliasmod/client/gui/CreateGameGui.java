package Lobaster.aliasmod.client.gui;

import Lobaster.aliasmod.client.AliasmodClient;
import Lobaster.aliasmod.networking.payload.CreateRoomC2SPayload;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.data.Axis;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.util.List;

public class CreateGameGui extends LightweightGuiDescription {
    private String selectedTheme = "Не обрано";
    private int teamCount = 2;
    private int playersPerTeamCount = 2;

    public CreateGameGui() {
        WGridPanel root = new WGridPanel();
        setRootPanel(root);
        root.setInsets(Insets.ROOT_PANEL);

        root.add(new WLabel(Text.literal("Створення нової гри")), 0, 0);
        root.add(new WLabel(Text.literal("Оберіть тему:")), 0, 2);
        List<String> themes = AliasmodClient.receivedThemeNames;
        WLabel currentThemeLabel = new WLabel(Text.literal("Обрано: " + selectedTheme));
        root.add(currentThemeLabel, 8, 2, 8, 1);
        WListPanel<String, WButton> themeList = new WListPanel<>(themes, WButton::new, (theme, button) -> {
            button.setLabel(Text.literal(theme));
            button.setOnClick(() -> {
                selectedTheme = theme;
                currentThemeLabel.setText(Text.literal("Обрано: " + selectedTheme));
            });
        });
        root.add(themeList, 0, 3, 7, 4);
        WLabel teamsTitle = new WLabel(Text.literal("Кількість команд:"));
        root.add(teamsTitle, 0, 8);
        WLabel teamsValueLabel = new WLabel(Text.literal(String.valueOf(teamCount)));
        WSlider teamsSlider = new WSlider(2, 4, Axis.HORIZONTAL);
        teamsSlider.setValue(teamCount);
        teamsSlider.setValueChangeListener(value -> { teamCount = value; teamsValueLabel.setText(Text.literal(String.valueOf(teamCount))); });
        root.add(teamsSlider, 0, 9, 5, 1);
        root.add(teamsValueLabel, 6, 9);

        WButton createButton = new WButton(Text.literal("Створити"));
        createButton.setOnClick(() -> {
            if ("Не обрано".equals(selectedTheme)) return;

            MinecraftClient.getInstance().setScreen(new LoadingScreen("Створення кімнати..."));

            ClientPlayNetworking.send(new CreateRoomC2SPayload(selectedTheme, teamCount, playersPerTeamCount));
        });
        root.add(createButton, 0, 14, 4, 1);

        WButton backButton = new WButton(Text.literal("Назад"));
        backButton.setOnClick(() -> MinecraftClient.getInstance().setScreen(new MainMenuScreen()));
        root.add(backButton, 5, 14, 4, 1);

        root.validate(this);
    }
}
