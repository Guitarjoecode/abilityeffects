package net.guitarjoe.abilityeffects.mixin;

import net.guitarjoe.abilityeffects.item.AbilityItems;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Slot.class)
public abstract class SlotMixin {
    @Shadow
    @Final
    public Container container;

    @Inject(
            method = "mayPlace",
            at = @At("HEAD"),
            cancellable = true
    )
    public void cancelAbilityItemPlaceInEnderChest(ItemStack itemStack, CallbackInfoReturnable<Boolean> cir) {
        if (this.container instanceof PlayerEnderChestContainer) {
            if (AbilityItems.ALL_ABILITY_ITEMS.contains(itemStack.getItem())) {
                cir.setReturnValue(false);
            }
        }
    }
}
