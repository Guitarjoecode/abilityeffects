package net.guitarjoe.abilityeffects.event.callback;

import net.guitarjoe.abilityeffects.ability.Ability;
import net.guitarjoe.abilityeffects.attachment.handler.AbilityHandler;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class AttackEntityCallback {
    public static InteractionResult handleAttack(Player player, Level level, InteractionHand hand, Entity entity, @Nullable EntityHitResult hitResult) {
        if (!level.isClientSide()) {

            if (entity instanceof ServerPlayer targetPlayer) {
                if (AbilityHandler.hasability(Ability.PULL_ABILITY, player)) {
                    if (player.getRandom().nextFloat() < 0.20f) {
                        Vec3 direction = player.position().subtract(targetPlayer.position());
                        double pullStrength = 0.8;
                        Vec3 motion = direction.normalize().scale(pullStrength);
                        targetPlayer.setDeltaMovement(motion.x, motion.y + 0.2, motion.z);
                        targetPlayer.connection.send(new ClientboundSetEntityMotionPacket(targetPlayer));
                    }
                }
            }

            if (entity instanceof LivingEntity serverTarget) {
                if (AbilityHandler.hasability(Ability.WITHER_ABILITY, player)) {
                    MobEffectInstance effectInstance = new MobEffectInstance(MobEffects.WITHER, 400);
                    if (player.getRandom().nextFloat() < 0.10f) {
                        serverTarget.addEffect(effectInstance);
                    }
                }
            }

            if (entity instanceof ServerPlayer targetPlayer) {
                if (AbilityHandler.hasability(Ability.TRACKER_ABILITY, player)) {
                    MobEffectInstance effectInstance = new MobEffectInstance(MobEffects.GLOWING, 1200);
                    targetPlayer.addEffect(effectInstance);
                }
            }
        }

        return InteractionResult.PASS;
    }
}
