package net.guitarjoe.abilityeffects.attachment;

import io.netty.buffer.ByteBuf;
import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.guitarjoe.abilityeffects.AbilityEffects;
import net.guitarjoe.abilityeffects.ability.Ability;
import net.guitarjoe.abilityeffects.attachment.handler.AbilityHandler;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AttachmentTypes {
    private static final Codec<Ability> ABILITY_CODEC = Codec.stringResolver(Ability::getName, AbilityHandler::fromName);
    private static final StreamCodec<ByteBuf, Ability> ABILITY_STREAM_CODEC = ByteBufCodecs.stringUtf8(32767).map(AbilityHandler::fromName, Ability::getName);

    public static final Codec<Set<Ability>> ABILITIES_CODEC = ABILITY_CODEC.listOf().xmap(HashSet::new, java.util.ArrayList::new);
    public static final StreamCodec<ByteBuf, Set<Ability>> ABILITIES_STREAM_CODEC = ABILITY_STREAM_CODEC.apply(ByteBufCodecs.collection(HashSet::new));

    public static final Codec<Map<Ability, Integer>> COOLDOWN_CODEC = Codec.unboundedMap(ABILITY_CODEC, Codec.INT).xmap(HashMap::new, HashMap::new);
    public static final StreamCodec<ByteBuf, Map<Ability, Integer>> COOLDOWNS_STREAM_CODEC = ByteBufCodecs.map(HashMap::new, ABILITY_STREAM_CODEC, ByteBufCodecs.VAR_INT);

    public static final AttachmentType<Set<Ability>> ACTIVE_ABILITIES = AttachmentRegistry.create(
            Identifier.fromNamespaceAndPath(AbilityEffects.MOD_ID, "active_abilities"),
            builder -> builder
                    .persistent(ABILITIES_CODEC)
                    .syncWith(ABILITIES_STREAM_CODEC, AttachmentSyncPredicate.all())
                    .initializer(HashSet::new)
    );

    public static final AttachmentType<Map<Ability, Integer>> COOLDOWNS = AttachmentRegistry.create(
            Identifier.fromNamespaceAndPath(AbilityEffects.MOD_ID, "ability_cooldowns"),
            builder -> builder
                    .persistent(COOLDOWN_CODEC)
                    .syncWith(COOLDOWNS_STREAM_CODEC, AttachmentSyncPredicate.targetOnly())
                    .initializer(HashMap::new)
    );
}
