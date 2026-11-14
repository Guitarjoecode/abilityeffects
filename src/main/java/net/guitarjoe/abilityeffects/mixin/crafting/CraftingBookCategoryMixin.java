package net.guitarjoe.abilityeffects.mixin.crafting;

import net.minecraft.world.item.crafting.CraftingBookCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CraftingBookCategory.class)
public enum CraftingBookCategoryMixin {
    ABILITYEFFECTS_CRAFTING_TAB("abilityeffects_crafting_tab", 4);

    @Shadow
    CraftingBookCategoryMixin(String name, int id) {
    }
}
