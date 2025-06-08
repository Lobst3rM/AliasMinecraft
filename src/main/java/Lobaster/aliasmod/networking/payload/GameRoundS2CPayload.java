package Lobaster.aliasmod.networking.payload;

import Lobaster.aliasmod.Aliasmod;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record GameRoundS2CPayload(String activePlayerName, int team1Score, int team2Score, String wordToShow) implements CustomPayload {
    public static final Id<GameRoundS2CPayload> ID = new Id<>(Identifier.of(Aliasmod.MOD_ID, "game_round"));
    public static final PacketCodec<RegistryByteBuf, GameRoundS2CPayload> CODEC = PacketCodec.tuple(
            PacketCodecs.STRING, GameRoundS2CPayload::activePlayerName,
            PacketCodecs.VAR_INT, GameRoundS2CPayload::team1Score,
            PacketCodecs.VAR_INT, GameRoundS2CPayload::team2Score,
            PacketCodecs.STRING, GameRoundS2CPayload::wordToShow,
            GameRoundS2CPayload::new
    );
    @Override public Id<? extends CustomPayload> getId() { return ID; }
}
