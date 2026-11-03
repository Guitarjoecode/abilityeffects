package net.guitarjoe.abilityeffects.mixin;

import net.guitarjoe.abilityeffects.AbilityEffects;
import net.minecraft.world.level.block.CrafterBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.CrafterBlockEntity;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(CrafterBlock.class)
public class CrafterBlockMixin {

    @Inject(
            method = "dispenseFrom",
            at = @At("HEAD"),
            cancellable = true
    )
    private void dispenseFrom(BlockState state, ServerLevel level, BlockPos pos, CallbackInfo cir) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof CrafterBlockEntity crafterEntity) {

            CraftingInput input = crafterEntity.asCraftInput();
            Optional<RecipeHolder<CraftingRecipe>> recipe = CrafterBlock.getPotentialResults(level, input);

            if (recipe.isPresent()) {
                ItemStack results = recipe.get().value().assemble(input);
                if (results.isEmpty()) return;

                Item item = results.getItem();

                if (AbilityEffects.maxTimesCrafted.containsKey(item)) {
                    level.levelEvent(1050, pos, 0);

                    cir.cancel();
                }
            }
        }
    }
}
