package net.guitarjoe.abilityeffects.ability.util;

import net.guitarjoe.abilityeffects.AbilityEffects;
import net.guitarjoe.abilityeffects.ability.Ability;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

public class AttributeHandler {

    public static void toggleAbilityAttribute(Ability ability, Player player, boolean activate) {
        switch (ability) {
            case SPEED_ABILITY -> toggleSpeedAbilityAttribute(player, activate);
            // case FIRE_RESISTANCE_ABILITY in LivingEntityMixin
            case HEALTH_BOOST_ABILITY -> toggleHealthBoostAbilityAttribute(player, activate);
            // case FEATHER_FALLING_ABILITY in LivingEntityMixin
            case STRENGTH_ABILITY -> toggleStrengthAbilityAttribute(player, activate);
            case MINING_ABILITY -> toggleMiningAbilityAttribute(player, activate);
            //case WARDEN_ABILITY in EntityMixin
            //case WITHER_ABILITY in AttackEntityCallback
            //case ENDER_DRAGON_ABILITY in UseItemCallback
        }
    }

    private static void toggleSpeedAbilityAttribute(Player player, boolean activate) {
        AttributeInstance attributeInstance = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attributeInstance == null) return;

        if (activate) {
            if (!attributeInstance.hasModifier(Identifier.fromNamespaceAndPath(AbilityEffects.MOD_ID, "speed_ability"))) {
                AttributeModifier modifier = new AttributeModifier(
                        Identifier.fromNamespaceAndPath(AbilityEffects.MOD_ID, "speed_ability"),
                        0.5,
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                );

                attributeInstance.addTransientModifier(modifier);
            }
        } else {
            if (attributeInstance.hasModifier(Identifier.fromNamespaceAndPath(AbilityEffects.MOD_ID, "speed_ability"))) {
                attributeInstance.removeModifier(Identifier.fromNamespaceAndPath(AbilityEffects.MOD_ID, "speed_ability"));
            }
        }
    }

    private static void toggleHealthBoostAbilityAttribute(Player player, boolean activate) {
        AttributeInstance attributeInstance = player.getAttribute(Attributes.MAX_HEALTH);
        if (attributeInstance == null) return;

        if (activate) {
            if (!attributeInstance.hasModifier(Identifier.fromNamespaceAndPath(AbilityEffects.MOD_ID, "health_boost_ability"))) {
                AttributeModifier modifier = new AttributeModifier(
                        Identifier.fromNamespaceAndPath(AbilityEffects.MOD_ID, "health_boost_ability"),
                        0.25,
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                );

                attributeInstance.addTransientModifier(modifier);
            }
        } else {
            if (attributeInstance.hasModifier(Identifier.fromNamespaceAndPath(AbilityEffects.MOD_ID, "health_boost_ability"))) {
                attributeInstance.removeModifier(Identifier.fromNamespaceAndPath(AbilityEffects.MOD_ID, "health_boost_ability"));
            }
        }
    }

    private static void toggleStrengthAbilityAttribute(Player player, boolean activate) {
        AttributeInstance attributeInstance = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attributeInstance == null) return;

        if (activate) {
            if (!attributeInstance.hasModifier(Identifier.fromNamespaceAndPath(AbilityEffects.MOD_ID, "strength_ability"))) {
                AttributeModifier modifier = new AttributeModifier(
                        Identifier.fromNamespaceAndPath(AbilityEffects.MOD_ID, "strength_ability"),
                        3.0F,
                        AttributeModifier.Operation.ADD_VALUE
                );

                attributeInstance.addTransientModifier(modifier);
            }
        } else {
            if (attributeInstance.hasModifier(Identifier.fromNamespaceAndPath(AbilityEffects.MOD_ID, "strength_ability"))) {
                attributeInstance.removeModifier(Identifier.fromNamespaceAndPath(AbilityEffects.MOD_ID, "strength_ability"));
            }
        }
    }

    private static void toggleMiningAbilityAttribute(Player player, boolean activate) {
        AttributeInstance attributeInstance = player.getAttribute(Attributes.MINING_EFFICIENCY);
        if (attributeInstance == null) return;

        if (activate) {
            if (!attributeInstance.hasModifier(Identifier.fromNamespaceAndPath(AbilityEffects.MOD_ID, "mining_ability"))) {
                AttributeModifier modifier = new AttributeModifier(
                        Identifier.fromNamespaceAndPath(AbilityEffects.MOD_ID, "mining_ability"),
                        0.3F,
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                );

                attributeInstance.addTransientModifier(modifier);
            }
        } else {
            if (attributeInstance.hasModifier(Identifier.fromNamespaceAndPath(AbilityEffects.MOD_ID, "mining_ability"))) {
                attributeInstance.removeModifier(Identifier.fromNamespaceAndPath(AbilityEffects.MOD_ID, "mining_ability"));
            }
        }
    }

}
