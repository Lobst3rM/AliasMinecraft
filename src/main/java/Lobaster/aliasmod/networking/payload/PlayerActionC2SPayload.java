
package Lobaster.aliasmod.networking.payload;

import Lobaster.aliasmod.Aliasmod;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

import java.util.UUID;

public record PlayerActionC2SPayload(UUID roomId, ActionType action) implements CustomPayload {
    public static final Id<PlayerActionC2SPayload> ID = new Id<>(Identifier.of(Aliasmod.MOD_ID, "player_action"));

    public enum ActionType { GUESSED, SKIPPED }

    private static final PacketCodec<RegistryByteBuf, UUID> UUID_CODEC = PacketCodec.of((uuid, buf) -> buf.writeUuid(uuid), buf -> buf.readUuid());

    private static final PacketCodec<RegistryByteBuf, ActionType> ACTION_TYPE_CODEC = new PacketCodec<>() {
        @Override
        public ActionType decode(RegistryByteBuf buf) {
            return ActionType.values()[buf.readVarInt()];
        }

        @Override
        public void encode(RegistryByteBuf buf, ActionType action) {
            buf.writeVarInt(action.ordinal());
        }
    };

    public static final PacketCodec<RegistryByteBuf, PlayerActionC2SPayload> CODEC = PacketCodec.tuple(
            UUID_CODEC, PlayerActionC2SPayload::roomId,
            ACTION_TYPE_CODEC, PlayerActionC2SPayload::action,
            PlayerActionC2SPayload::new
    );

    @Override public Id<? extends CustomPayload> getId() { return ID; }
}
