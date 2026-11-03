package net.guitarjoe.abilityeffects.mixin;

import net.guitarjoe.abilityeffects.AbilityEffects;
import net.guitarjoe.abilityeffects.limitedcrafting.TimesCraftedSavedData;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CraftingMenu.class)
public class CraftingMenuMixin {

    @Inject(
            method = "slotChangedCraftingGrid",
            at = @At("TAIL")
    )
    private static void slotChangedCraftingGrid(AbstractContainerMenu menu, ServerLevel level, Player player,
                                                CraftingContainer container, ResultContainer resultSlots,
                                                @Nullable RecipeHolder<CraftingRecipe> recipeHint, CallbackInfo cir) {
        ItemStack result = resultSlots.getItem(0);
        if (result.isEmpty()) return;

        Item item = result.getItem();

        if (AbilityEffects.maxTimesCrafted.containsKey(item)) {
            String key = BuiltInRegistries.ITEM.getKey(item).toString();

            TimesCraftedSavedData savedData = TimesCraftedSavedData.get(level);
            int limit = AbilityEffects.maxTimesCrafted.get(item);

            int already_crafted = savedData.map.getOrDefault(key, 0);

            if (already_crafted >= limit) {
                resultSlots.setItem(0, ItemStack.EMPTY);
                menu.setRemoteSlot(0, ItemStack.EMPTY);

                if (player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.connection.send(new ClientboundContainerSetSlotPacket(
                            menu.containerId, menu.incrementStateId(), 0, ItemStack.EMPTY
                    ));
                }
            }
        }
    }
}
