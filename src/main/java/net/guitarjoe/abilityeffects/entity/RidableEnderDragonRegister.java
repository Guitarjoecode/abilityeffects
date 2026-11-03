package net.guitarjoe.abilityeffects.entity;

import net.guitarjoe.abilityeffects.AbilityEffects;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public class RidableEnderDragonRegister {
    public static final ResourceKey<EntityType<?>> RIDABLE_ENDER_DRAGON_KEY = ResourceKey.create(Registries.ENTITY_TYPE,
            Identifier.fromNamespaceAndPath(AbilityEffects.MOD_ID, "ridable_ender_dragon"));

    public static EntityType<RidableEnderDragonEntity> RIDABLE_ENDER_DRAGON;

    public static void registerEntity() {
        RIDABLE_ENDER_DRAGON = Registry.register(BuiltInRegistries.ENTITY_TYPE,
                Identifier.fromNamespaceAndPath(AbilityEffects.MOD_ID, "ridable_ender_dragon"),
                EntityType.Builder.of(RidableEnderDragonEntity::new, MobCategory.MONSTER).sized(6.0f, 2.0f)
                        .build(RIDABLE_ENDER_DRAGON_KEY));
    }
}
