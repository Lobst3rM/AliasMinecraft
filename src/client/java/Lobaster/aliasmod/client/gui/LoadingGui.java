package Lobaster.aliasmod.client.gui;

import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WGridPanel;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import net.minecraft.text.Text;

public class LoadingGui extends LightweightGuiDescription {
    public LoadingGui(String message) {
        WGridPanel root = new WGridPanel();
        setRootPanel(root);
        root.setInsets(Insets.ROOT_PANEL);

        WLabel loadingLabel = new WLabel(Text.literal(message));
        loadingLabel.setHorizontalAlignment(HorizontalAlignment.CENTER);

        root.add(loadingLabel, 0, 4, 18, 1);

        root.validate(this);
    }
}