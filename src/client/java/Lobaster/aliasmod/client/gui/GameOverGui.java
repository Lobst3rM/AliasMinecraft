package Lobaster.aliasmod.client.gui;

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class GameOverGui extends LightweightGuiDescription {
    public GameOverGui(Text winnerText) {
        WGridPanel root = new WGridPanel();
        setRootPanel(root);
        root.setInsets(Insets.ROOT_PANEL);

        WLabel title = new WLabel(Text.literal("Гру закінчено!").formatted(Formatting.BOLD));
        title.setHorizontalAlignment(HorizontalAlignment.CENTER);
        root.add(title, 0, 1, 18, 1);

        WLabel winnerLabel = new WLabel(Text.literal("Переможець: ").append(winnerText));
        winnerLabel.setHorizontalAlignment(HorizontalAlignment.CENTER);
        root.add(winnerLabel, 0, 3, 18, 1);

        WButton backButton = new WButton(Text.literal("Повернутись до меню"));
        backButton.setOnClick(() -> {
            MinecraftClient.getInstance().setScreen(new MainMenuScreen());
        });
        root.add(backButton, 4, 6, 10, 1);

        root.validate(this);
    }
}
