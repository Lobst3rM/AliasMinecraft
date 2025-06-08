package Lobaster.aliasmod.client.gui;

import io.github.cottonmc.cotton.gui.client.CottonClientScreen;
import net.minecraft.text.Text;

public class GameOverScreen extends CottonClientScreen {
    public GameOverScreen(Text winnerText) {
        super(new GameOverGui(winnerText));
    }
}

