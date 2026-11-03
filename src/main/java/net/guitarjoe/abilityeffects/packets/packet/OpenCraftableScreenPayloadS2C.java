package net.guitarjoe.abilityeffects.packets.packet;

import net.guitarjoe.abilityeffects.AbilityEffects;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;

public record OpenCraftableScreenPayloadS2C(Map<String, Integer> craftedMap) implements CustomPacketPayload {
    public static final Type<OpenCraftableScreenPayloadS2C> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(AbilityEffects.MOD_ID, "open_craftable_screen"));

    public static final StreamCodec<RegistryFriendlyByteBuf, OpenCraftableScreenPayloadS2C> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(HashMap::new, ByteBufCodecs.STRING_UTF8, ByteBufCodecs.VAR_INT),
            OpenCraftableScreenPayloadS2C::craftedMap,
            OpenCraftableScreenPayloadS2C::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}