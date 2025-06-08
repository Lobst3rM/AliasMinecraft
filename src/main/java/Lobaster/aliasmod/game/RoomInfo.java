package Lobaster.aliasmod.game;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import java.util.UUID;

public record RoomInfo(UUID roomId, String hostName, int playerCount, int maxPlayers) {
    private static final PacketCodec<RegistryByteBuf, UUID> UUID_CODEC = PacketCodec.of((uuid, buf) -> buf.writeUuid(uuid), buf -> buf.readUuid());

    public static final PacketCodec<RegistryByteBuf, RoomInfo> CODEC = PacketCodec.tuple(
            UUID_CODEC, RoomInfo::roomId,
            PacketCodecs.STRING, RoomInfo::hostName,
            PacketCodecs.VAR_INT, RoomInfo::playerCount,
            PacketCodecs.VAR_INT, RoomInfo::maxPlayers,
            RoomInfo::new
    );
}