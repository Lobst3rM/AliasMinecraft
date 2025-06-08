package Lobaster.aliasmod.client.gui;

import io.github.cottonmc.cotton.gui.client.CottonClientScreen;
import java.util.UUID;

public class GameScreen extends CottonClientScreen {
    public GameScreen(String activePlayerName, int team1Score, int team2Score, String wordToShow) {
        super(new GameGui(activePlayerName, team1Score, team2Score, wordToShow));
    }
    public GameGui getGuiDescription() { return (GameGui) this.description; }
}