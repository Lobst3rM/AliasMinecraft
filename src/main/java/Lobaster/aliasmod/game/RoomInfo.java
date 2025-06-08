package Lobaster.aliasmod.game;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import java.util.UUID;

public record RoomInfo(UUID roomId, String hostName, int playerCount, int maxPlayers, GameRoom.GameState gameState) {
    private static final PacketCodec<RegistryByteBuf, UUID> UUID_CODEC = PacketCodec.of((uuid, buf) -> buf.writeUuid(uuid), buf -> buf.readUuid());

    private static final PacketCodec<RegistryByteBuf, GameRoom.GameState> GAME_STATE_CODEC = new PacketCodec<>() {
        @Override
        public GameRoom.GameState decode(RegistryByteBuf buf) {
            return GameRoom.GameState.values()[buf.readVarInt()];
        }

        @Override
        public void encode(RegistryByteBuf buf, GameRoom.GameState state) {
            buf.writeVarInt(state.ordinal());
        }
    };

    public static final PacketCodec<RegistryByteBuf, RoomInfo> CODEC = PacketCodec.tuple(
            UUID_CODEC, RoomInfo::roomId,
            PacketCodecs.STRING, RoomInfo::hostName,
            PacketCodecs.VAR_INT, RoomInfo::playerCount,
            PacketCodecs.VAR_INT, RoomInfo::maxPlayers,
            GAME_STATE_CODEC, RoomInfo::gameState,
            RoomInfo::new
    );
}
