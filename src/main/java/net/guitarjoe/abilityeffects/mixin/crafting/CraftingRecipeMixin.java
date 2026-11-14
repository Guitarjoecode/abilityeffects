package net.guitarjoe.abilityeffects.mixin.crafting;

import net.guitarjoe.abilityeffects.limitedcrafting.AbilityRecipeBookCategories;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CraftingRecipe.class)
public interface CraftingRecipeMixin {

    @Shadow
    CraftingBookCategory category();

    @Inject(method = "recipeBookCategory", at = @At("HEAD"), cancellable = true)
    private void onRecipeBookCategory(CallbackInfoReturnable<RecipeBookCategory> cir) {
        if (this.category() == CraftingBookCategory.ABILITYEFFECTS_CRAFTING_TAB) {
            cir.setReturnValue(AbilityRecipeBookCategories.ABILITYEFFECTS_TAB);
        }
    }
}
