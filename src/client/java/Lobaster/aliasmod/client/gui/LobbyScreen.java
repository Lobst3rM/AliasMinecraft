package Lobaster.aliasmod.client.gui;

import io.github.cottonmc.cotton.gui.client.CottonClientScreen;
import java.util.UUID;

public class LobbyScreen extends CottonClientScreen {
    public LobbyScreen(UUID roomId) {
        super(new LobbyGui(roomId));
    }


}
