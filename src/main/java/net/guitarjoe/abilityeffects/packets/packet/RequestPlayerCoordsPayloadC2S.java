package net.guitarjoe.abilityeffects.packets.packet;

import com.mojang.authlib.GameProfile;
import net.guitarjoe.abilityeffects.AbilityEffects;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record RequestPlayerCoordsPayloadC2S(GameProfile targetProfile) implements CustomPacketPayload {
    public static final Type<RequestPlayerCoordsPayloadC2S> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath(AbilityEffects.MOD_ID, "request_coords"));

    public static final StreamCodec<RegistryFriendlyByteBuf, RequestPlayerCoordsPayloadC2S> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.GAME_PROFILE,
            RequestPlayerCoordsPayloadC2S::targetProfile,
            RequestPlayerCoordsPayloadC2S::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}