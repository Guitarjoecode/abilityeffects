package net.guitarjoe.abilityeffects.client.screen;

import net.guitarjoe.abilityeffects.AbilityEffects;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CraftableAbilitiesScreen extends Screen {
    private Map<String, Integer> craftedMap;

    private final List<Map.Entry<Item, Integer>> abilitiesList;
    private int startIndex = 0;
    private final int itemsPerPage = 5;

    public CraftableAbilitiesScreen(Map<String, Integer> craftedMap) {
        super(Component.translatable("craftablescreen.abilityeffects.title"));
        this.craftedMap = craftedMap;
        this.abilitiesList = new ArrayList<>(AbilityEffects.maxTimesCrafted.entrySet());
    }

    public void setCraftedMap(Map<String, Integer> craftedMap) {
        this.craftedMap = craftedMap;
    }

    @Override
    protected void init() {
        super.init();
        this.rebuildScreen();
    }

    public void rebuildScreen() {
        this.clearWidgets();

        this.addRenderableOnly(new StringWidget(this.width / 2 - 100, 15, 200, 20, this.title, this.font));

        int startY = 45;
        int spacing = 24;

        int maxToShow = Math.min(this.abilitiesList.size() - startIndex, itemsPerPage);

        for (int i = 0; i < maxToShow; i++) {
            Map.Entry<Item, Integer> entry = this.abilitiesList.get(this.startIndex + i);
            Item item = entry.getKey();
            int maxLimit = entry.getValue();
            String statKey = BuiltInRegistries.ITEM.getKey(item).toString();

            int alreadyCrafted = this.craftedMap.getOrDefault(statKey, 0);
            int remaining = Math.max(0, maxLimit - alreadyCrafted);

            Component infoText = Component.literal("Craftable: " + remaining + " / " + maxLimit);
            int currentY = startY + (i * spacing);

            this.addRenderableOnly(new StringWidget(
                    this.width / 2 - 95, currentY, 250, 20,
                    infoText, this.font
            ));
        }

        int navY = startY + itemsPerPage * spacing + 10;

        this.addRenderableWidget(Button.builder(Component.literal("◄"), button -> {
                    this.startIndex = Math.max(0, this.startIndex - itemsPerPage);
                    this.rebuildScreen();
                })
                .bounds(this.width / 2 - 55, navY, 50, 20)
                .build())
                .active = (this.startIndex > 0);

        this.addRenderableWidget(Button.builder(Component.literal("►"), button -> {
                    this.startIndex = this.startIndex + itemsPerPage;
                    this.rebuildScreen();
                })
                .bounds(this.width / 2 + 5, navY, 50, 20)
                .build())
                .active = (this.startIndex + itemsPerPage < this.abilitiesList.size());

        this.addRenderableWidget(Button.builder(Component.translatable("screen.abilityeffects.close"), button -> this.onClose())
                .bounds(this.width / 2 - 100, this.height - 30, 200, 20)
                .build());
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);

        int startY = 47;
        int spacing = 24;

        int maxToShow = Math.min(this.abilitiesList.size() - startIndex, itemsPerPage);

        for (int i = 0; i < maxToShow; i++) {
            Map.Entry<Item, Integer> entry = this.abilitiesList.get(this.startIndex + i);
            Item item = entry.getKey();
            ItemStack visualStack = new ItemStack(item);

            int iconX = this.width / 2 - 118;
            int iconY = startY + (i * spacing);

            graphics.item(visualStack, iconX, iconY);
        }
    }

    @Override
    public boolean isPauseScreen() { return false; }
}
