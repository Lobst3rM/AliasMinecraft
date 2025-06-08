package Lobaster.aliasmod.networking.payload;

import Lobaster.aliasmod.Aliasmod;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import java.util.UUID;

public record RejoinRequestC2SPayload(UUID roomId) implements CustomPayload {
    public static final Id<RejoinRequestC2SPayload> ID = new Id<>(Identifier.of(Aliasmod.MOD_ID, "rejoin_request"));
    private static final PacketCodec<RegistryByteBuf, UUID> UUID_CODEC = PacketCodec.of((uuid, buf) -> buf.writeUuid(uuid), buf -> buf.readUuid());

    public static final PacketCodec<RegistryByteBuf, RejoinRequestC2SPayload> CODEC = PacketCodec.tuple(
            UUID_CODEC, RejoinRequestC2SPayload::roomId,
            RejoinRequestC2SPayload::new
    );
    @Override public Id<? extends CustomPayload> getId() { return ID; }
}
