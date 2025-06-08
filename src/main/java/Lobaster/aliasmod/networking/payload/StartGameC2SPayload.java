package Lobaster.aliasmod.networking.payload;

import Lobaster.aliasmod.Aliasmod;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import java.util.UUID;

public record StartGameC2SPayload(UUID roomId) implements CustomPayload {
    public static final Id<StartGameC2SPayload> ID = new Id<>(Identifier.of(Aliasmod.MOD_ID, "start_game"));
    private static final PacketCodec<RegistryByteBuf, UUID> UUID_CODEC = PacketCodec.of((uuid, buf) -> buf.writeUuid(uuid), buf -> buf.readUuid());
    public static final PacketCodec<RegistryByteBuf, StartGameC2SPayload> CODEC = PacketCodec.tuple(UUID_CODEC, StartGameC2SPayload::roomId, StartGameC2SPayload::new);
    @Override public Id<? extends CustomPayload> getId() { return ID; }
}