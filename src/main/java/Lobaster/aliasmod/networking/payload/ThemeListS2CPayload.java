package Lobaster.aliasmod.networking.payload;

import Lobaster.aliasmod.Aliasmod;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import java.util.List;

public record ThemeListS2CPayload(List<String> themeNames) implements CustomPayload {
    public static final Id<ThemeListS2CPayload> ID = new Id<>(Identifier.of(Aliasmod.MOD_ID, "theme_list"));
    public static final PacketCodec<RegistryByteBuf, ThemeListS2CPayload> CODEC = PacketCodec.tuple(PacketCodecs.STRING.collect(PacketCodecs.toList()), ThemeListS2CPayload::themeNames, ThemeListS2CPayload::new);
    @Override public Id<? extends CustomPayload> getId() { return ID; }
}