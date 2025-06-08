package Lobaster.aliasmod.networking.payload;

import Lobaster.aliasmod.Aliasmod;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record CreateRoomC2SPayload(String themeName, int teamCount, int playersPerTeam) implements CustomPayload {
    public static final Id<CreateRoomC2SPayload> ID = new Id<>(Identifier.of(Aliasmod.MOD_ID, "create_room"));
    public static final PacketCodec<RegistryByteBuf, CreateRoomC2SPayload> CODEC = PacketCodec.of(
            (value, buf) -> {
                buf.writeString(value.themeName);
                buf.writeInt(value.teamCount);
                buf.writeInt(value.playersPerTeam);
            },
            (buf) -> new CreateRoomC2SPayload(buf.readString(), buf.readInt(), buf.readInt())
    );
    @Override public Id<? extends CustomPayload> getId() { return ID; }
}
