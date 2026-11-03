package net.guitarjoe.abilityeffects.packets.packet;

import net.guitarjoe.abilityeffects.AbilityEffects;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record AbilityPayloadC2S(int slotindex) implements CustomPacketPayload {
    public static final Type<AbilityPayloadC2S> TYPE = new Type<>(Identifier.fromNamespaceAndPath(AbilityEffects.MOD_ID, "ability_payload"));
    public static final StreamCodec<RegistryFriendlyByteBuf, AbilityPayloadC2S> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            AbilityPayloadC2S::slotindex,
            AbilityPayloadC2S::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}