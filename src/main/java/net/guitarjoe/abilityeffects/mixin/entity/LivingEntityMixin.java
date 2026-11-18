package net.guitarjoe.abilityeffects.mixin.entity;

import net.guitarjoe.abilityeffects.ability.Ability;
import net.guitarjoe.abilityeffects.attachment.handler.AbilityHandler;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Shadow
    public abstract float getSpeed();

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

    @Unique
    private boolean abilityeffects$canMoveFastInLava() {
        if (!((Object) this instanceof ServerPlayer player)) {
            return false;
        }

        return AbilityHandler.hasability(Ability.FIRE_RESISTANCE_ABILITY, player);
    }

    @ModifyArg(
            method = "travelInLava",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;moveRelative(FLnet/minecraft/world/phys/Vec3;)V"
            ),
            index = 0
    )
    private float abilityeffects$modifyLavaSpeed(float originalSpeed) {
        if (this.abilityeffects$canMoveFastInLava()) {
            return this.getSpeed();
        }
        return originalSpeed;
    }

    @ModifyArg(
            method = "travelInLava",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/phys/Vec3;scale(D)Lnet/minecraft/world/phys/Vec3;"
            ),
            index = 0
    )
    private double abilityeffects$modifyDeepLavaDrag(double originalDrag) {
        if (this.abilityeffects$canMoveFastInLava()) {
            return this.isSprinting() ? 1.0 : 0.9;
        }
        return originalDrag;
    }

    @ModifyArg(
            method = "travelInLava",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/phys/Vec3;multiply(DDD)Lnet/minecraft/world/phys/Vec3;"
            ),
            index = 0
    )
    private double abilityeffects$modifyShallowLavaDragX(double originalDrag) {
        if (this.abilityeffects$canMoveFastInLava()) {
            return this.isSprinting() ? 1.0 : 0.9;
        }
        return originalDrag;
    }

    @ModifyArg(
            method = "travelInLava",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/phys/Vec3;multiply(DDD)Lnet/minecraft/world/phys/Vec3;"
            ),
            index = 2
    )
    private double abilityeffects$modifyShallowLavaDragZ(double originalDrag) {
        if (this.abilityeffects$canMoveFastInLava()) {
            return this.isSprinting() ? 1.0 : 0.9;
        }
        return originalDrag;
    }
}
