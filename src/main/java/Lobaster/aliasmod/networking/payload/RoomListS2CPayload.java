package Lobaster.aliasmod.networking.payload;

import Lobaster.aliasmod.Aliasmod;
import Lobaster.aliasmod.game.RoomInfo;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import java.util.List;

public record RoomListS2CPayload(List<RoomInfo> rooms) implements CustomPayload {
    public static final Id<RoomListS2CPayload> ID = new Id<>(Identifier.of(Aliasmod.MOD_ID, "room_list"));
    public static final PacketCodec<RegistryByteBuf, RoomListS2CPayload> CODEC = PacketCodec.tuple(RoomInfo.CODEC.collect(PacketCodecs.toList()), RoomListS2CPayload::rooms, RoomListS2CPayload::new);
    @Override public Id<? extends CustomPayload> getId() { return ID; }
}
