package net.guitarjoe.abilityeffects.client.event;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.guitarjoe.abilityeffects.ability.Ability;
import net.guitarjoe.abilityeffects.attachment.AttachmentTypes;
import net.guitarjoe.abilityeffects.attachment.handler.AbilityHandler;
import net.guitarjoe.abilityeffects.client.keybind.KeyMappings;
import net.guitarjoe.abilityeffects.client.screen.PlayerSelectionScreen;
import net.guitarjoe.abilityeffects.packets.packet.AbilityPayloadC2S;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public class ModClientEvents {
    private static final Map<Ability, Integer> visualClientTicks = new HashMap<>();
    private static int lastClientTick = -1;

    public static void onEndTick(Minecraft client) {
        if (client.player == null) return;

        while (KeyMappings.SPECIAL_ABILITY_KEYBIND_1.consumeClick()) {
            handleAbilityActivation(client, 0);

        }

        while (KeyMappings.SPECIAL_ABILITY_KEYBIND_2.consumeClick()) {
            handleAbilityActivation(client, 1);
        }
    }

    public static void addAbilityHudElement(GuiGraphicsExtractor guiGraphicsExtractor, DeltaTracker tracker) {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null || client.options.hideGui) return;

        Set<Ability> activeAbilities = client.player.getAttachedOrCreate(AttachmentTypes.ACTIVE_ABILITIES, HashSet::new);
        Map<Ability, Integer> cooldowns = client.player.getAttachedOrCreate(AttachmentTypes.COOLDOWNS, HashMap::new);

        if (activeAbilities.isEmpty()) return;

        guiGraphicsExtractor.pose().pushMatrix();

        int x = client.getWindow().getGuiScaledWidth() / 2;
        int y = client.getWindow().getGuiScaledHeight();

        int yOffset = 58;
        if (client.player.getArmorValue() > 0) yOffset += 10;
        if (client.player.getAbsorptionAmount() > 0) yOffset += 10;

        int startX = x - 95;
        int index = 0;

        if (client.player.tickCount != lastClientTick && !client.isPaused()) {
            lastClientTick = client.player.tickCount;
            visualClientTicks.forEach((ab, ticks) -> {
                if (ticks > 0) visualClientTicks.put(ab, ticks - 1);
            });
        }

        for (Ability ability : activeAbilities) {
            int currentX = startX + (index * 22);
            int currentY = y - yOffset;

            ItemStack itemStack = ItemStack.EMPTY;

            Item item = AbilityHandler.fromAbility(ability);
            itemStack = new ItemStack(item);

            if (!itemStack.isEmpty()) {
                guiGraphicsExtractor.item(itemStack, currentX, currentY);

                int baseCooldown = cooldowns.getOrDefault(ability, 0);

                if (baseCooldown > 0 && visualClientTicks.getOrDefault(ability, 0) < baseCooldown) {
                    visualClientTicks.put(ability, baseCooldown);
                }

                int cooldownTicks = visualClientTicks.getOrDefault(ability, 0);

                if (cooldownTicks > 0) {
                    float cooldownProgress = (float) cooldownTicks / (float) ability.getCooldown();
                    int pixelHeight = (int) Math.ceil(cooldownProgress * 16.0F);

                    int startY = currentY + (16 - pixelHeight);

                    guiGraphicsExtractor.fill(currentX, startY, currentX + 16, currentY + 16, 0xBF000000);
                } else {
                    visualClientTicks.remove(ability);
                    guiGraphicsExtractor.itemDecorations(client.font, itemStack, currentX, currentY);
                }
            } else {
                guiGraphicsExtractor.itemDecorations(client.font, itemStack, currentX, currentY);
            }

            index++;
        }
        guiGraphicsExtractor.pose().popMatrix();
    }

    private static void handleAbilityActivation(Minecraft client, int slotIndex) {
        if (client.player == null) return;

        Set<Ability> activeSet = client.player.getAttached(AttachmentTypes.ACTIVE_ABILITIES);
        if (activeSet == null || slotIndex >= activeSet.size()) return;
        Ability chosenAbility = new ArrayList<>(activeSet).get(slotIndex);

        if (chosenAbility == Ability.TRACKER_ABILITY) {
            Map<Ability, Integer> cooldowns = client.player.getAttachedOrCreate(AttachmentTypes.COOLDOWNS, HashMap::new);
            int currentCooldown = cooldowns.getOrDefault(Ability.TRACKER_ABILITY, 0);

            if (currentCooldown > 0) {
                return;
            }
            client.setScreen(new PlayerSelectionScreen(client.screen));
        } else {
            ClientPlayNetworking.send(new AbilityPayloadC2S(slotIndex));
        }
    }

}