package net.guitarjoe.abilityeffects.event.callback;

import net.guitarjoe.abilityeffects.entity.RidableEnderDragonEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragonPart;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

public class UseEntityCallback {
    public static InteractionResult handleEnderDragonClick(Player player, Level level, InteractionHand interactionHand, Entity entity, EntityHitResult entityHitResult) {
        if (!level.isClientSide() && interactionHand == InteractionHand.MAIN_HAND) {

            RidableEnderDragonEntity dragon = null;

            if (entity instanceof RidableEnderDragonEntity) {
                dragon = (RidableEnderDragonEntity) entity;
            }
            else if (entity instanceof EnderDragonPart part && part.parentMob instanceof RidableEnderDragonEntity) {
                dragon = (RidableEnderDragonEntity) part.parentMob;
            }

            if (dragon != null) {
                if (player.isPassenger()) {
                    player.stopRiding();
                    return InteractionResult.SUCCESS;
                } else {
                    boolean success = player.startRiding(dragon);
                    if (success) {
                        return InteractionResult.SUCCESS;
                    } else {
                        return InteractionResult.FAIL;
                    }
                }
            }
        }
        return InteractionResult.PASS;
    }
}
