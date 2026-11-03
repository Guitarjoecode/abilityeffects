package net.guitarjoe.abilityeffects.event.callback;

import net.guitarjoe.abilityeffects.ability.Ability;
import net.guitarjoe.abilityeffects.attachment.handler.AbilityHandler;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class UseItemCallback {
    public static InteractionResult handleUseDragonBreath(Player player, Level level, InteractionHand interactionHand) {
        ItemStack stack = player.getItemInHand(interactionHand);

        if (stack.is(Items.DRAGON_BREATH)) {
            if (AbilityHandler.hasability(Ability.ENDER_DRAGON_ABILITY, player)) {
                if (level.isClientSide()) {
                    return InteractionResult.SUCCESS;
                }

                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 200, 1));
                player.addEffect(new MobEffectInstance(MobEffects.ABSORPTION, 2400, 1));
                player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, 6000, 0));
                player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 6000, 0));

                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.PLAYER_BURP, SoundSource.PLAYERS, 0.5F, level.getRandom().nextFloat() * 0.1F + 0.9F);

                if (!player.isCreative()) {
                    stack.shrink(1);
                    if (stack.isEmpty()) {
                        player.setItemInHand(interactionHand, new ItemStack(Items.GLASS_BOTTLE));
                    } else {
                        if (!player.getInventory().add(new ItemStack(Items.GLASS_BOTTLE))) {
                            player.drop(new ItemStack(Items.GLASS_BOTTLE), false);
                        }
                    }
                }
                return InteractionResult.SUCCESS_SERVER;
            }
        }
        return InteractionResult.PASS;
    }
}
