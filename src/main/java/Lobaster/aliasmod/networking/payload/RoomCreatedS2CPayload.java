
package Lobaster.aliasmod.networking.payload;

import Lobaster.aliasmod.Aliasmod;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import java.util.UUID;

//хуіта
public record RoomCreatedS2CPayload(UUID roomId) implements CustomPayload {

    public static final Id<RoomCreatedS2CPayload> ID = new Id<>(Identifier.of(Aliasmod.MOD_ID, "room_created"));

    public static final PacketCodec<RegistryByteBuf, RoomCreatedS2CPayload> CODEC = PacketCodec.of(
            (value, buf) -> buf.writeUuid(value.roomId()),
            (buf) -> new RoomCreatedS2CPayload(buf.readUuid())
    );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
