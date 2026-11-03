package net.guitarjoe.abilityeffects.packets.packet;

import net.guitarjoe.abilityeffects.AbilityEffects;
import net.guitarjoe.abilityeffects.ability.Ability;
import net.guitarjoe.abilityeffects.attachment.AttachmentTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.Set;

public record SyncAbilitiesPayloadS2C(Set<Ability> abilities) implements CustomPacketPayload {
    public static final Type<SyncAbilitiesPayloadS2C> TYPE = new Type<>(Identifier.fromNamespaceAndPath(AbilityEffects.MOD_ID, "sync_abilities"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncAbilitiesPayloadS2C> STREAM_CODEC = StreamCodec.composite(
            AttachmentTypes.ABILITIES_STREAM_CODEC.cast(), SyncAbilitiesPayloadS2C::abilities,
            SyncAbilitiesPayloadS2C::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}