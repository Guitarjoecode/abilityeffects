package net.guitarjoe.abilityeffects.mixin;

import net.guitarjoe.abilityeffects.ability.Ability;
import net.guitarjoe.abilityeffects.attachment.handler.AbilityHandler;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    public LivingEntityMixin(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Inject(method = "hurtServer", at = @At("HEAD"), cancellable = true)
    private void cancelFireDamage(ServerLevel level, DamageSource source, float damage, CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof ServerPlayer player) {
            if (AbilityHandler.hasability(Ability.FIRE_RESISTANCE_ABILITY, player) && source.is(DamageTypeTags.IS_FIRE)) {
                cir.setReturnValue(false);
            }
        }
    }

    @Inject(method = "calculateFallDamage", at = @At("HEAD"), cancellable = true)
    private void cancelFallDamage(double fallDistance, float damageModifier, CallbackInfoReturnable<Integer> cir) {
        if ((Object) this instanceof ServerPlayer player) {
            if (AbilityHandler.hasability(Ability.FEATHER_FALLING_ABILITY, player)) {
                cir.setReturnValue(0);
            }
        }
    }
}
