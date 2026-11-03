package net.guitarjoe.abilityeffects.client.packets;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.guitarjoe.abilityeffects.attachment.AttachmentTypes;
import net.guitarjoe.abilityeffects.client.screen.CraftableAbilitiesScreen;
import net.guitarjoe.abilityeffects.packets.packet.*;
import net.minecraft.network.RegistryFriendlyByteBuf;

import java.util.HashMap;
import java.util.Map;

public class ClientBoundPackets {

    public static void registerPackets(PayloadTypeRegistry<RegistryFriendlyByteBuf> registry) {
        PayloadTypeRegistry.clientboundPlay().register(
                SyncAbilitiesPayloadS2C.TYPE,
                SyncAbilitiesPayloadS2C.STREAM_CODEC
        );
        PayloadTypeRegistry.clientboundPlay().register(
                OpenCraftableScreenPayloadS2C.TYPE,
                OpenCraftableScreenPayloadS2C.STREAM_CODEC
        );
        PayloadTypeRegistry.clientboundPlay().register(
                SyncTimesCraftedPayloadS2C.TYPE,
                SyncTimesCraftedPayloadS2C.STREAM_CODEC
        );

        ClientPlayNetworking.registerGlobalReceiver(
                SyncAbilitiesPayloadS2C.TYPE,
                ClientBoundPackets::handleSyncAbilityPayload
        );
        ClientPlayNetworking.registerGlobalReceiver(
                OpenCraftableScreenPayloadS2C.TYPE,
                ClientBoundPackets::handleOpenCraftableScreenPayload
        );
        ClientPlayNetworking.registerGlobalReceiver(
                SyncTimesCraftedPayloadS2C.TYPE,
                ClientBoundPackets::handleSyncTimesCraftedPayload
        );
    }

    private static void handleSyncAbilityPayload(SyncAbilitiesPayloadS2C payload, ClientPlayNetworking.Context context) {
        context.player().setAttached(AttachmentTypes.ACTIVE_ABILITIES, payload.abilities());
    }

    private static void handleOpenCraftableScreenPayload(OpenCraftableScreenPayloadS2C payload, ClientPlayNetworking.Context context) {
        context.client().execute(() -> context.client().setScreen(new CraftableAbilitiesScreen(payload.craftedMap())));
    }

    private static void handleSyncTimesCraftedPayload(SyncTimesCraftedPayloadS2C payload, ClientPlayNetworking.Context context) {
        context.client().execute(() -> {
            if (context.client().screen instanceof CraftableAbilitiesScreen screen) {
                Map<String, Integer> freshMap = new HashMap<>(payload.craftedMap());
                screen.setCraftedMap(freshMap);
                screen.rebuildScreen();
            }
        });
    }
}