package Lobaster.aliasmod.client;

import Lobaster.aliasmod.Aliasmod;
import Lobaster.aliasmod.client.gui.GameOverScreen;
import Lobaster.aliasmod.client.gui.GameScreen;
import Lobaster.aliasmod.client.gui.LobbyScreen;
import Lobaster.aliasmod.client.gui.MainMenuScreen;
import Lobaster.aliasmod.game.RoomInfo;
import Lobaster.aliasmod.networking.payload.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AliasmodClient implements ClientModInitializer {
    public static final List<String> receivedThemeNames = new ArrayList<>();
    public static final List<RoomInfo> activeRoomInfos = new ArrayList<>();
    public static Map<Integer, List<String>> currentLobbyState = new ConcurrentHashMap<>();
    public static UUID currentRoomId = null;
    public static UUID currentRoomHostId = null;
    public static boolean canStartGame = false;
    public static String activePlayerName = "";
    private static KeyBinding openMenuKeyBinding;

    @Override
    public void onInitializeClient() {
        openMenuKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding("key.aliasmod.open_menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_P, "category.aliasmod.main"));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (openMenuKeyBinding.wasPressed() && client.player != null && client.currentScreen == null) {
                client.setScreen(new MainMenuScreen());
            }
        });
        registerS2CPacketHandlers();
    }

    private void registerS2CPacketHandlers() {
        ClientPlayNetworking.registerGlobalReceiver(ThemeListS2CPayload.ID, (payload, context) -> {
            receivedThemeNames.clear();
            receivedThemeNames.addAll(payload.themeNames());
        });
        ClientPlayNetworking.registerGlobalReceiver(RoomListS2CPayload.ID, (payload, context) -> {
            activeRoomInfos.clear();
            activeRoomInfos.addAll(payload.rooms());
            context.client().execute(() -> {
                if (context.client().currentScreen instanceof MainMenuScreen) {
                    context.client().setScreen(new MainMenuScreen());
                }
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(LobbyStateS2CPayload.ID, (payload, context) -> {
            currentLobbyState = payload.teamPlayers();
            currentRoomId = payload.roomId();
            currentRoomHostId = payload.hostId();
            canStartGame = payload.canStart();
            context.client().execute(() -> {
                context.client().setScreen(new LobbyScreen(payload.roomId()));
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(GameRoundS2CPayload.ID, (payload, context) -> {
            activePlayerName = payload.activePlayerName();
            context.client().execute(() -> context.client().setScreen(new GameScreen(
                    payload.activePlayerName(),
                    payload.team1Score(),
                    payload.team2Score(),
                    payload.wordToShow()
            )));
        });
        ClientPlayNetworking.registerGlobalReceiver(TimerTickS2CPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                if (context.client().currentScreen instanceof GameScreen screen) {
                    screen.getGuiDescription().updateTimer(payload.remainingSeconds());
                }
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(GameOverS2CPayload.ID, (payload, context) -> {
            context.client().execute(() -> {
                context.client().setScreen(new GameOverScreen(payload.winnerText()));
            });
        });
    }
}