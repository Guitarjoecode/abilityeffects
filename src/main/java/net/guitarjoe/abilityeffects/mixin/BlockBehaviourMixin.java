package net.guitarjoe.abilityeffects.mixin;

import net.guitarjoe.abilityeffects.ability.Ability;
import net.guitarjoe.abilityeffects.ability.util.BreakStateManager;
import net.guitarjoe.abilityeffects.attachment.handler.AbilityHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.class)
public abstract class BlockBehaviourMixin {

    @Inject(method = "getDestroyProgress", at = @At("RETURN"), cancellable = true)
    private void makeInstantMineAble(BlockState state, Player player, BlockGetter level, BlockPos pos, CallbackInfoReturnable<Float> cir) {
        if (BreakStateManager.isInstantBreakActive(player.getUUID()) &&
                AbilityHandler.hasability(Ability.MINING_ABILITY, player) &&
                BreakStateManager.canInstantBreak(state, level, pos)) {

            cir.setReturnValue(1.0F);
        }
    }
}