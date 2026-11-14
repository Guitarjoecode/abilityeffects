package net.guitarjoe.abilityeffects.client.mixin;

import net.guitarjoe.abilityeffects.limitedcrafting.AbilityRecipeBookCategories;
import net.minecraft.client.gui.screens.recipebook.SearchRecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(SearchRecipeBookCategory.class)
public abstract class SearchRecipeBookCategoryMixin {

    @Inject(method = "includedCategories", at = @At("RETURN"), cancellable = true)
    private void onIncludedCategories(CallbackInfoReturnable<List<RecipeBookCategory>> cir) {
        if (this.equals(SearchRecipeBookCategory.CRAFTING)) {
            List<RecipeBookCategory> categories = new ArrayList<>(cir.getReturnValue());
            categories.add(AbilityRecipeBookCategories.ABILITYEFFECTS_TAB);
            cir.setReturnValue(List.copyOf(categories));
        }
    }
}