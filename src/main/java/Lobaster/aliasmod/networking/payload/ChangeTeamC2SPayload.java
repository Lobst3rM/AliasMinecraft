package Lobaster.aliasmod.networking.payload;

import Lobaster.aliasmod.Aliasmod;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import java.util.UUID;

public record ChangeTeamC2SPayload(UUID roomId, int teamId) implements CustomPayload {
    public static final Id<ChangeTeamC2SPayload> ID = new Id<>(Identifier.of(Aliasmod.MOD_ID, "change_team"));
    private static final PacketCodec<RegistryByteBuf, UUID> UUID_CODEC = PacketCodec.of((uuid, buf) -> buf.writeUuid(uuid), buf -> buf.readUuid());

    public static final PacketCodec<RegistryByteBuf, ChangeTeamC2SPayload> CODEC = PacketCodec.tuple(
            UUID_CODEC, ChangeTeamC2SPayload::roomId,
            PacketCodecs.VAR_INT, ChangeTeamC2SPayload::teamId,
            ChangeTeamC2SPayload::new
    );

    @Override public Id<? extends CustomPayload> getId() { return ID; }
}
