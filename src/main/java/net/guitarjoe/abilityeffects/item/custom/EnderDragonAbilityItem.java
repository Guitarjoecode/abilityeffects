package net.guitarjoe.abilityeffects.item.custom;

import net.guitarjoe.abilityeffects.ability.Ability;
import net.guitarjoe.abilityeffects.attachment.handler.AbilityHandler;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class EnderDragonAbilityItem extends Item {
    public EnderDragonAbilityItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level instanceof ServerLevel) {
            ItemStack itemStack = player.getItemInHand(hand);

            if (!AbilityHandler.hasability(Ability.ENDER_DRAGON_ABILITY, player)) {

                if (AbilityHandler.toggleAbility(Ability.ENDER_DRAGON_ABILITY, player, true)) {
                    level.playSound(null, player, SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0F, 1.0F);
                    itemStack.consume(1, player);
                    return InteractionResult.SUCCESS;
                } else {
                    return InteractionResult.FAIL;
                }
            } else {
                player.sendSystemMessage(Component.translatable("ability.abilityeffects.already_have"));
                return InteractionResult.PASS;
            }
        }

        return InteractionResult.SUCCESS;
    }
}