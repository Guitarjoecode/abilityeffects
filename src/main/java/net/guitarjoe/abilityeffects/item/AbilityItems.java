package net.guitarjoe.abilityeffects.item;

import net.guitarjoe.abilityeffects.AbilityEffects;
import net.guitarjoe.abilityeffects.item.custom.*;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.DamageResistant;

import java.util.function.Function;

public class AbilityItems {
    public static final Item HEALTH_BOOST_ABILITY_ITEM = registerItem("health_boost_ability_item", HealthBoostAbilityItem::new);
    public static final Item FEATHER_FALLING_ABILITY_ITEM = registerItem("feather_falling_ability_item", FeatherFallingAbilityItem::new);
    public static final Item FIRE_RESISTANCE_ABILITY_ITEM = registerItem("fire_resistance_ability_item", FireresistanceAbilityItem::new);
    public static final Item MINING_ABILITY_ITEM = registerItem("mining_ability_item", MiningAbilityItem::new);
    public static final Item PULL_ABILITY_ITEM = registerItem("pull_ability_item", PullAbilityItem::new);
    public static final Item SPEED_ABILITY_ITEM = registerItem("speed_ability_item", SpeedAbilityItem::new);
    public static final Item STRENGTH_ABILITY_ITEM = registerItem("strength_ability_item", StrengthAbilityItem::new);
    public static final Item TRACKER_ABILITY_ITEM = registerItem("tracker_ability_item", TrackerAbilityItem::new);
    public static final Item WARDEN_ABILITY_ITEM = registerItem("warden_ability_item", WardenAbilityItem::new);
    public static final Item WITHER_ABILITY_ITEM = registerItem("wither_ability_item", WitherAbilityItem::new);
    public static final Item ENDER_DRAGON_ABILITY_ITEM = registerItem("ender_dragon_ability_item", EnderDragonAbilityItem::new);


    public static final TagKey<DamageType> IMMUNE_DAMAGE_TYPES = TagKey.create(
            Registries.DAMAGE_TYPE,
            Identifier.fromNamespaceAndPath(AbilityEffects.MOD_ID, "is_item_damage_type")
    );

    private static Item.Properties AbilityProperties(Item.Properties properties) {
        return properties
                .stacksTo(1)
                .rarity(Rarity.RARE)
                .component(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true)
                .delayedComponent(DataComponents.DAMAGE_RESISTANT, (context) ->
                        new DamageResistant(context.getOrThrow(IMMUNE_DAMAGE_TYPES)));
    }

    private static Item registerItem(String name, Function<Item.Properties, Item> function) {
        Item.Properties abilityProperties = AbilityProperties(new Item.Properties().setId(
                ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(AbilityEffects.MOD_ID, name))));

        return Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(AbilityEffects.MOD_ID, name),
                function.apply(abilityProperties));
    }
}
