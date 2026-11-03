package net.guitarjoe.abilityeffects.mixin;

import net.guitarjoe.abilityeffects.AbilityEffects;
import net.guitarjoe.abilityeffects.ability.Ability;
import net.guitarjoe.abilityeffects.attachment.handler.AbilityHandler;
import net.guitarjoe.abilityeffects.limitedcrafting.TimesCraftedSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow
    public abstract Level level();

    @Inject(
            method = "walkingStepSound",
            at = @At("HEAD"),
            cancellable = true
    )
    private void cancelWalkingStepSound(BlockPos onPos, BlockState onState, CallbackInfo ci) {
        if ((Object) this instanceof Player player) {
            if (AbilityHandler.hasability(Ability.WARDEN_ABILITY, player)) {
                ci.cancel();
            }
        }
    }

    @Inject(
            method = "playStepSound",
            at = @At("HEAD"),
            cancellable = true
    )
    private void cancelWalkingStepPlayingSound(BlockPos pos, BlockState blockState, CallbackInfo ci) {
        if ((Object) this instanceof Player player) {
            if (AbilityHandler.hasability(Ability.WARDEN_ABILITY, player)) {
                ci.cancel();
            }
        }
    }

    @Inject(
            method = "vibrationAndSoundEffectsFromBlock",
            at = @At("HEAD"),
            cancellable = true
    )
    private void cancelWardenImpulse(BlockPos pos, BlockState blockState, boolean shouldSound, boolean shouldVibrate, Vec3 clippedMovement, CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof Player player) {
            if (AbilityHandler.hasability(Ability.WARDEN_ABILITY, player)) {
                cir.cancel();
            }
        }
    }

    @Inject(method = "dampensVibrations", at = @At("HEAD"), cancellable = true)
    private void cancelWardenImpulse2(CallbackInfoReturnable<Boolean> cir) {
        if ((Object) this instanceof Player player) {
            if (AbilityHandler.hasability(Ability.WARDEN_ABILITY, player)) {
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(
            method = "onBelowWorld",
            at = @At("HEAD")
    )
    private void handleBelowWorld(CallbackInfo ci) {
        if ((Object) this instanceof ItemEntity itemEntity) {
            if (itemEntity.level().isClientSide()) return;

            if (!itemEntity.isAlive() || itemEntity.isRemoved()) return;

            ItemStack stack = itemEntity.getItem();
            if (stack.isEmpty()) return;
            Item item = stack.getItem();

            if (AbilityEffects.maxTimesCrafted.containsKey(item)) {
                String key = BuiltInRegistries.ITEM.getKey(item).toString();

                ServerLevel serverLevel = (ServerLevel) itemEntity.level();
                TimesCraftedSavedData savedData = TimesCraftedSavedData.get(serverLevel);

                if (savedData != null && savedData.map != null) {
                    itemEntity.setItem(ItemStack.EMPTY);
                    itemEntity.discard();

                    int already_crafted = savedData.map.getOrDefault(key, 0);

                    if (already_crafted > 0) {
                        savedData.map.put(key, already_crafted - 1);

                        savedData.setDirty();
                        AbilityEffects.syncTimesCrafted(serverLevel, savedData);
                    }
                }
            }
        }
    }
}