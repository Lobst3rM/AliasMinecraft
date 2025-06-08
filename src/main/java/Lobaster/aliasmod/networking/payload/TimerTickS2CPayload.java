package Lobaster.aliasmod.networking.payload;

import Lobaster.aliasmod.Aliasmod;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record TimerTickS2CPayload(int remainingSeconds) implements CustomPayload {
    public static final Id<TimerTickS2CPayload> ID = new Id<>(Identifier.of(Aliasmod.MOD_ID, "timer_tick"));
    public static final PacketCodec<RegistryByteBuf, TimerTickS2CPayload> CODEC = PacketCodec.tuple(PacketCodecs.VAR_INT, TimerTickS2CPayload::remainingSeconds, TimerTickS2CPayload::new);
    @Override public Id<? extends CustomPayload> getId() { return ID; }
}