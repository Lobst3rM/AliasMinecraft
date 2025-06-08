package Lobaster.aliasmod.networking.payload;

import Lobaster.aliasmod.Aliasmod;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import java.util.UUID;

public record LeaveRoomC2SPayload(UUID roomId) implements CustomPayload {
    public static final Id<LeaveRoomC2SPayload> ID = new Id<>(Identifier.of(Aliasmod.MOD_ID, "leave_room"));
    private static final PacketCodec<RegistryByteBuf, UUID> UUID_CODEC = PacketCodec.of((uuid, buf) -> buf.writeUuid(uuid), buf -> buf.readUuid());

    public static final PacketCodec<RegistryByteBuf, LeaveRoomC2SPayload> CODEC = PacketCodec.tuple(
            UUID_CODEC, LeaveRoomC2SPayload::roomId,
            LeaveRoomC2SPayload::new
    );

    @Override public Id<? extends CustomPayload> getId() { return ID; }
}