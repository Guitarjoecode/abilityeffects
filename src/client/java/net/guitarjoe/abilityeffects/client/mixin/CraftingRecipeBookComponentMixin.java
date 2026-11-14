package net.guitarjoe.abilityeffects.client.mixin;

import net.guitarjoe.abilityeffects.item.AbilityItems;
import net.guitarjoe.abilityeffects.limitedcrafting.AbilityRecipeBookCategories;
import net.minecraft.client.gui.screens.recipebook.CraftingRecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(CraftingRecipeBookComponent.class)
public abstract class CraftingRecipeBookComponentMixin {
    @Shadow
    @Final
    @Mutable
    private static List<RecipeBookComponent.TabInfo> TABS;

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void onClinit(CallbackInfo ci) {
        List<RecipeBookComponent.TabInfo> tabs = new ArrayList<>(TABS);
        tabs.add(new RecipeBookComponent.TabInfo(
                AbilityItems.SPEED_ABILITY_ITEM,
                AbilityRecipeBookCategories.ABILITYEFFECTS_TAB
        ));
        TABS = List.copyOf(tabs);
    }
}