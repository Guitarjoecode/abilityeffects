package net.guitarjoe.abilityeffects.client.screen;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.guitarjoe.abilityeffects.packets.packet.RequestPlayerCoordsPayloadC2S;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

public class PlayerSelectionScreen extends Screen {

    private final Screen previousScreen;
    private final List<PlayerInfo> onlinePlayers = new ArrayList<>();
    private int startIndex = 0;

    public PlayerSelectionScreen(Screen previousScreen) {
        super(Component.translatable("playerselectionscreen.abilityeffects.title"));
        this.previousScreen = previousScreen;
    }

    @Override
    protected void init() {
        super.init();
        this.onlinePlayers.clear();

        if (this.minecraft.getConnection() != null) {
            this.onlinePlayers.addAll(this.minecraft.getConnection().getOnlinePlayers());
        }

        this.addRenderableOnly(new net.minecraft.client.gui.components.StringWidget(
                this.width / 2 - 100, 20, 200, 20,
                this.title,
                this.font
        ));

        if (this.onlinePlayers.isEmpty()) {
            net.minecraft.client.gui.components.StringWidget noPlayersWidget = new net.minecraft.client.gui.components.StringWidget(
                    this.width / 2 - 100, this.height / 2, 200, 20,
                    Component.translatable("playerselectionscreen.abilityeffects.no_players_online"),
                    this.font
            );
            this.addRenderableOnly(noPlayersWidget);
        }

        this.rebuildPlayerButtons();
    }

    private void rebuildPlayerButtons() {
        this.clearWidgets();

        int buttonWidth = 200;
        int buttonHeight = 20;
        int spacing = 5;
        int startY = 60;
        int xPos = this.width / 2 - (buttonWidth / 2);

        int maxToShow = Math.min(this.onlinePlayers.size() - startIndex, 5);

        for (int i = 0; i < maxToShow; i++) {
            PlayerInfo playerInfo = this.onlinePlayers.get(this.startIndex + i);
            String playerName = playerInfo.getProfile().name();
            int currentY = startY + i * (buttonHeight + spacing);

            this.addRenderableWidget(Button.builder(Component.literal(playerName), button -> {
                        this.onPlayerSelected(playerInfo);
                    })
                    .bounds(xPos, currentY, buttonWidth, buttonHeight)
                    .build());
        }

        int navY = startY + 5 * (buttonHeight + spacing) + 10;

        this.addRenderableWidget(Button.builder(Component.literal("◄"), button -> {
                    this.startIndex = Math.max(0, this.startIndex - 5);
                    this.rebuildPlayerButtons();
                })
                .bounds(this.width / 2 - 55, navY, 50, 20)
                .build())
                .active = (this.startIndex > 0);

        this.addRenderableWidget(Button.builder(Component.literal("►"), button -> {
                    this.startIndex = this.startIndex + 5;
                    this.rebuildPlayerButtons();
                })
                .bounds(this.width / 2 + 5, navY, 50, 20)
                .build())
                .active = (this.startIndex + 5 < this.onlinePlayers.size());


        this.addRenderableWidget(Button.builder(Component.translatable("screen.abilityeffects.close"), button -> {
                    this.minecraft.setScreen(this.previousScreen);
                })
                .bounds(this.width / 2 - 100, this.height - 30, 200, 20)
                .build());
    }


    private void onPlayerSelected(PlayerInfo selectedPlayer) {
        ClientPlayNetworking.send(new RequestPlayerCoordsPayloadC2S(selectedPlayer.getProfile()));

        this.minecraft.setScreen(null);
    }

    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}