package Lobaster.aliasmod.networking.payload;

import Lobaster.aliasmod.Aliasmod;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import java.util.*;

public record LobbyStateS2CPayload(UUID roomId, UUID hostId, Map<Integer, List<String>> teamPlayers, boolean canStart) implements CustomPayload {
    public static final Id<LobbyStateS2CPayload> ID = new Id<>(Identifier.of(Aliasmod.MOD_ID, "lobby_state"));
    private static final PacketCodec<RegistryByteBuf, UUID> UUID_CODEC = PacketCodec.of((uuid, buf) -> buf.writeUuid(uuid), buf -> buf.readUuid());

    private static final PacketCodec<RegistryByteBuf, Map<Integer, List<String>>> TEAM_MAP_CODEC = new PacketCodec<>() {
        @Override
        public Map<Integer, List<String>> decode(RegistryByteBuf buf) {
            int mapSize = buf.readVarInt();
            Map<Integer, List<String>> map = new HashMap<>(mapSize);
            for (int i = 0; i < mapSize; i++) {
                int key = buf.readVarInt();
                List<String> list = buf.readList(b -> b.readString());
                map.put(key, list);
            }
            return map;
        }
        @Override
        public void encode(RegistryByteBuf buf, Map<Integer, List<String>> map) {
            buf.writeVarInt(map.size());
            for (Map.Entry<Integer, List<String>> entry : map.entrySet()) {
                buf.writeVarInt(entry.getKey());
                buf.writeCollection(entry.getValue(), (b, s) -> b.writeString(s));
            }
        }
    };

    private static final PacketCodec<RegistryByteBuf, Boolean> BOOL_CODEC = PacketCodec.ofStatic(
            RegistryByteBuf::writeBoolean,
            RegistryByteBuf::readBoolean
    );

    public static final PacketCodec<RegistryByteBuf, LobbyStateS2CPayload> CODEC = PacketCodec.tuple(
            UUID_CODEC, LobbyStateS2CPayload::roomId,
            UUID_CODEC, LobbyStateS2CPayload::hostId,
            TEAM_MAP_CODEC, LobbyStateS2CPayload::teamPlayers,
            BOOL_CODEC,
            LobbyStateS2CPayload::canStart,
            LobbyStateS2CPayload::new
    );
    @Override public Id<? extends CustomPayload> getId() { return ID; }
}