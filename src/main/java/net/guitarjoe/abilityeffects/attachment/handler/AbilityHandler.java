package net.guitarjoe.abilityeffects.attachment.handler;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.guitarjoe.abilityeffects.ability.Ability;
import net.guitarjoe.abilityeffects.attachment.AttachmentTypes;
import net.guitarjoe.abilityeffects.item.AbilityItems;
import net.guitarjoe.abilityeffects.packets.packet.SyncAbilitiesPayloadS2C;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

import java.util.HashSet;
import java.util.Set;

public class AbilityHandler {
    public static boolean toggleAbility(Ability ability, Player player, boolean activate) {
        Set<Ability> current = player.getAttached(AttachmentTypes.ACTIVE_ABILITIES);
        Set<Ability> updated = (current == null) ? new HashSet<>() : new HashSet<>(current);

        if (activate) {
            if (!updated.contains(ability) && updated.size() >= 2) {
                player.sendSystemMessage(Component.translatable("ability.abilityeffects.max"));
                return false;
            }
            updated.add(ability);
        } else {
            updated.remove(ability);
        }

        player.setAttached(AttachmentTypes.ACTIVE_ABILITIES, updated);

        if (player instanceof ServerPlayer serverPlayer) {
            ServerPlayNetworking.send(serverPlayer, new SyncAbilitiesPayloadS2C(updated));
        }

        return true;
    }

    public static boolean hasability(Ability ability, Player player) {
        Set<Ability> activeAbilities = player.getAttached(AttachmentTypes.ACTIVE_ABILITIES);
        return activeAbilities != null && activeAbilities.contains(ability);
    }

    public static Ability fromName(String name) {
        for (Ability ability : Ability.values()) {
            if (ability.getName().equals(name)) {
                return ability;
            }
        }
        return null;
    }

    public static Item fromAbility(Ability ability) {
        switch (ability) {
            case SPEED_ABILITY -> {
                return AbilityItems.SPEED_ABILITY_ITEM;
            }
            case FIRE_RESISTANCE_ABILITY -> {
                return AbilityItems.FIRE_RESISTANCE_ABILITY_ITEM;
            }
            case HEALTH_BOOST_ABILITY -> {
                return AbilityItems.HEALTH_BOOST_ABILITY_ITEM;
            }
            case FEATHER_FALLING_ABILITY -> {
                return AbilityItems.FEATHER_FALLING_ABILITY_ITEM;
            }
            case STRENGTH_ABILITY -> {
                return AbilityItems.STRENGTH_ABILITY_ITEM;
            }
            case MINING_ABILITY -> {
                return AbilityItems.MINING_ABILITY_ITEM;
            }
            case WARDEN_ABILITY -> {
                return AbilityItems.WARDEN_ABILITY_ITEM;
            }
            case WITHER_ABILITY -> {
                return AbilityItems.WITHER_ABILITY_ITEM;
            }
            case PULL_ABILITY -> {
                return AbilityItems.PULL_ABILITY_ITEM;
            }
            case TRACKER_ABILITY -> {
                return AbilityItems.TRACKER_ABILITY_ITEM;
            }
            case ENDER_DRAGON_ABILITY -> {
                return AbilityItems.ENDER_DRAGON_ABILITY_ITEM;
            }
        }
        return null;
    }
}
