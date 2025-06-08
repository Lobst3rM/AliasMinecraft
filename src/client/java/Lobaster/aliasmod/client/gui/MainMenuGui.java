package Lobaster.aliasmod.client.gui;

import Lobaster.aliasmod.client.AliasmodClient;
import Lobaster.aliasmod.game.RoomInfo;
import Lobaster.aliasmod.networking.payload.JoinRoomC2SPayload;
import Lobaster.aliasmod.networking.payload.RequestRoomListC2SPayload;
import io.github.cottonmc.cotton.gui.client.LightweightGuiDescription;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

import java.util.List;

public class MainMenuGui extends LightweightGuiDescription {
    public MainMenuGui() {
        WGridPanel root = new WGridPanel();
        setRootPanel(root);
        root.setInsets(Insets.ROOT_PANEL);

        root.add(new WLabel(Text.literal("Лобі гри Aliasmod")), 0, 0, 6, 1);
        root.add(new WLabel(Text.literal("Доступні кімнати:")), 0, 2);

        List<RoomInfo> roomData = AliasmodClient.activeRoomInfos;

        WListPanel<RoomInfo, WButton> roomList = new WListPanel<>(roomData, WButton::new, (roomInfo, button) -> {
            String buttonText = String.format("Кімната %s (%d/%d)",
                    roomInfo.hostName(), roomInfo.playerCount(), roomInfo.maxPlayers());
            button.setLabel(Text.literal(buttonText));

            button.setOnClick(() -> {
                MinecraftClient.getInstance().setScreen(new LoadingScreen("Приєднання до кімнати..."));

                ClientPlayNetworking.send(new JoinRoomC2SPayload(roomInfo.roomId()));
            });
        });
        root.add(roomList, 0, 3, 10, 6);

        WButton createGameButton = new WButton(Text.literal("Створити гру"));

        createGameButton.setOnClick(() -> {
            MinecraftClient.getInstance().setScreen(new LoadingScreen("Відкриття меню..."));

            MinecraftClient.getInstance().execute(() -> {
                MinecraftClient.getInstance().setScreen(new CreateGameScreen());
            });
        });
        root.add(createGameButton, 12, 3, 6, 1);

        WButton refreshButton = new WButton(Text.literal("Оновити список"));
        refreshButton.setOnClick(() -> ClientPlayNetworking.send(RequestRoomListC2SPayload.INSTANCE));
        root.add(refreshButton, 12, 5, 6, 1);

        ClientPlayNetworking.send(RequestRoomListC2SPayload.INSTANCE);

        root.validate(this);
    }
}