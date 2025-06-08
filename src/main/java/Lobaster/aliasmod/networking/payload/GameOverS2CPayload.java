package Lobaster.aliasmod.networking.payload;

import Lobaster.aliasmod.Aliasmod;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.util.Identifier;

public record GameOverS2CPayload(Text winnerText) implements CustomPayload {
    public static final Id<GameOverS2CPayload> ID = new Id<>(Identifier.of(Aliasmod.MOD_ID, "game_over"));
    public static final PacketCodec<RegistryByteBuf, GameOverS2CPayload> CODEC = PacketCodec.tuple(
            TextCodecs.REGISTRY_PACKET_CODEC, GameOverS2CPayload::winnerText,
            GameOverS2CPayload::new
    );
    @Override public Id<? extends CustomPayload> getId() { return ID; }
}