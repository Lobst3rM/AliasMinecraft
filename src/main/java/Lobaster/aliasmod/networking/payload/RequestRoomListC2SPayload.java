package Lobaster.aliasmod.networking.payload;

import Lobaster.aliasmod.Aliasmod;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record RequestRoomListC2SPayload() implements CustomPayload {
    public static final RequestRoomListC2SPayload INSTANCE = new RequestRoomListC2SPayload();
    public static final Id<RequestRoomListC2SPayload> ID = new Id<>(Identifier.of(Aliasmod.MOD_ID, "request_room_list"));
    public static final PacketCodec<RegistryByteBuf, RequestRoomListC2SPayload> CODEC = PacketCodec.unit(INSTANCE);
    @Override public Id<? extends CustomPayload> getId() { return ID; }
}