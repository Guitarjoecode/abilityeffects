package net.guitarjoe.abilityeffects.event;

import net.guitarjoe.abilityeffects.ability.Ability;
import net.guitarjoe.abilityeffects.ability.util.AttributeHandler;
import net.guitarjoe.abilityeffects.ability.util.BreakStateManager;
import net.guitarjoe.abilityeffects.ability.util.SpecialAbilityAttributeHandler;
import net.guitarjoe.abilityeffects.attachment.handler.AbilityHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class ModServerEvents {
    public static void onEndServerTick(MinecraftServer server) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {

            BreakStateManager.tickPlayerInstantBreak(player);
            SpecialAbilityAttributeHandler.handleCooldownTick(player);

            for (Ability ability : Ability.values()) {
                boolean has_ability = AbilityHandler.hasability(ability, player);

                AttributeHandler.toggleAbilityAttribute(ability, player, has_ability);
            }
        }
    }
}