package net.guitarjoe.abilityeffects.mixin.crafting;

import net.minecraft.data.recipes.RecipeCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(RecipeCategory.class)
public enum RecipeCategoryMixin {
    ABILITYEFFECTS_RECIPE_CATEGORY("abilityeffects_recipe_category");

    @Shadow
    RecipeCategoryMixin(String recipeFolderName) {
    }
}
