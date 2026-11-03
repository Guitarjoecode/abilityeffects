package net.guitarjoe.abilityeffects.mixin;

import net.guitarjoe.abilityeffects.AbilityEffects;
import net.guitarjoe.abilityeffects.ability.Ability;
import net.guitarjoe.abilityeffects.attachment.AttachmentTypes;
import net.guitarjoe.abilityeffects.attachment.handler.AbilityHandler;
import net.guitarjoe.abilityeffects.limitedcrafting.TimesCraftedSavedData;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stat;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {

    @Shadow
    public abstract ServerLevel level();

    @Inject(
            method = "awardStat",
            at = @At("HEAD")
    )
    private void addTimesCrafted(Stat<?> stat, int count, CallbackInfo ci) {
        if (stat.getValue() instanceof Item item) {
            TimesCraftedSavedData savedData = TimesCraftedSavedData.get(this.level());
            String id = BuiltInRegistries.ITEM.getKey(item).toString();

            if (AbilityEffects.maxTimesCrafted.containsKey(item)) {
                int maxAllowed = AbilityEffects.maxTimesCrafted.get(item);

                int currentCrafted = savedData.map.getOrDefault(id, 0);

                if (currentCrafted < maxAllowed) {

                    int nextValue = Math.min(maxAllowed, currentCrafted + count);
                    savedData.map.put(id, nextValue);
                    savedData.setDirty();
                    System.out.println("Crafting registriert für: " + id + " | Neuer synchronisierter Wert: " + nextValue);

                    AbilityEffects.syncTimesCrafted(this.level(), savedData);
                }
            }
        }
    }

    @Inject(method = "die", at = @At("HEAD"))
    private void dropRandomAbilityOnPlayerKill(DamageSource source, CallbackInfo ci) {
        ServerPlayer player = (ServerPlayer) (Object) this;

        if (source != null && source.getEntity() instanceof Player killer) {
            Set<Ability> activeAbilities = player.getAttached(AttachmentTypes.ACTIVE_ABILITIES);

            if (activeAbilities != null && !activeAbilities.isEmpty()) {
                List<Ability> abilityList = new ArrayList<>(activeAbilities);
                Random random = new Random();
                Ability randomAbility = abilityList.get(random.nextInt(abilityList.size()));

                AbilityHandler.toggleAbility(randomAbility, player, false);

                Item itemToDrop = AbilityHandler.fromAbility(randomAbility);
                if (itemToDrop != null) {
                    ItemStack dropStack = new ItemStack(itemToDrop);
                    ServerLevel serverLevel = player.level();

                    if (serverLevel != null) {
                        ItemEntity itemEntity = new ItemEntity(serverLevel, player.getX(), player.getY(), player.getZ(), dropStack);

                        itemEntity.setDefaultPickUpDelay();

                        itemEntity.setDeltaMovement(
                                (serverLevel.getRandom().nextDouble() - 0.5) * 0.2,
                                0.2,
                                (serverLevel.getRandom().nextDouble() - 0.5) * 0.2
                        );
                        serverLevel.addFreshEntity(itemEntity);
                    }
                }
            }
        }
    }
}
