package Lobaster.aliasmod.client.gui;

import io.github.cottonmc.cotton.gui.client.CottonClientScreen;

public class LoadingScreen extends CottonClientScreen {
    public LoadingScreen(String message) {
        super(new LoadingGui(message));
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }
}