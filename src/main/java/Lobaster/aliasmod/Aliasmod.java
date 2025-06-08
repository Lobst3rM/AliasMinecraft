package Lobaster.aliasmod;

import Lobaster.aliasmod.game.GameManager;
import Lobaster.aliasmod.networking.payload.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Aliasmod implements ModInitializer {
    public static final String MOD_ID = "aliasmod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Aliasmod завантажено.");
        registerPayloads();
        ThemeManager.loadThemes();
        registerServerEvents();
    }

    private void registerPayloads() {
        // S2C
        PayloadTypeRegistry.playS2C().register(ThemeListS2CPayload.ID, ThemeListS2CPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(LobbyStateS2CPayload.ID, LobbyStateS2CPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(RoomListS2CPayload.ID, RoomListS2CPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(GameRoundS2CPayload.ID, GameRoundS2CPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(TimerTickS2CPayload.ID, TimerTickS2CPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(GameOverS2CPayload.ID, GameOverS2CPayload.CODEC);
        // C2S
        PayloadTypeRegistry.playC2S().register(CreateRoomC2SPayload.ID, CreateRoomC2SPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(RequestRoomListC2SPayload.ID, RequestRoomListC2SPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(JoinRoomC2SPayload.ID, JoinRoomC2SPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(StartGameC2SPayload.ID, StartGameC2SPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(ChangeTeamC2SPayload.ID, ChangeTeamC2SPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(LeaveRoomC2SPayload.ID, LeaveRoomC2SPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(PlayerActionC2SPayload.ID, PlayerActionC2SPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(RejoinRequestC2SPayload.ID, RejoinRequestC2SPayload.CODEC);
    }

    private void registerServerEvents() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> ServerPlayNetworking.send(handler.player, new ThemeListS2CPayload(ThemeManager.getThemeNames())));
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> GameManager.onPlayerDisconnect(handler.getPlayer()));
        ServerTickEvents.END_SERVER_TICK.register(GameManager::tick);

        // пакєти
        ServerPlayNetworking.registerGlobalReceiver(CreateRoomC2SPayload.ID, (payload, context) -> context.server().execute(() -> GameManager.createRoom(context.player(), payload.themeName(), payload.teamCount())));
        ServerPlayNetworking.registerGlobalReceiver(RequestRoomListC2SPayload.ID, (payload, context) -> context.server().execute(() -> ServerPlayNetworking.send(context.player(), new RoomListS2CPayload(GameManager.getRoomInfos(context.server())))));
        ServerPlayNetworking.registerGlobalReceiver(JoinRoomC2SPayload.ID, (payload, context) -> context.server().execute(() -> GameManager.joinRoom(context.player(), payload.roomId())));
        ServerPlayNetworking.registerGlobalReceiver(StartGameC2SPayload.ID, (payload, context) -> context.server().execute(() -> GameManager.startGame(context.player(), payload.roomId(), context.server())));
        ServerPlayNetworking.registerGlobalReceiver(ChangeTeamC2SPayload.ID, (payload, context) -> context.server().execute(() -> GameManager.changePlayerTeam(context.player(), payload.roomId(), payload.teamId())));
        ServerPlayNetworking.registerGlobalReceiver(LeaveRoomC2SPayload.ID, (payload, context) -> context.server().execute(() -> GameManager.removePlayerFromRoom(context.player(), payload.roomId())));
        ServerPlayNetworking.registerGlobalReceiver(PlayerActionC2SPayload.ID, (payload, context) -> context.server().execute(() -> GameManager.handlePlayerAction(context.player(), payload.roomId(), payload.action())));
        ServerPlayNetworking.registerGlobalReceiver(RejoinRequestC2SPayload.ID, (payload, context) -> context.server().execute(() -> GameManager.resyncPlayer(context.player(), payload.roomId())));
    }
}