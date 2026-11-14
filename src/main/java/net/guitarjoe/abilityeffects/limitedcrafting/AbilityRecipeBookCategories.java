package net.guitarjoe.abilityeffects.limitedcrafting;

import net.guitarjoe.abilityeffects.AbilityEffects;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeBookCategory;

public class AbilityRecipeBookCategories {
    public static final RecipeBookCategory ABILITYEFFECTS_TAB = Registry.register(
            BuiltInRegistries.RECIPE_BOOK_CATEGORY,
            Identifier.fromNamespaceAndPath(AbilityEffects.MOD_ID, "abilityeffects_tab"),
            new RecipeBookCategory()
    );

    public static void init() {
    }
}
