package net.guitarjoe.abilityeffects.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.guitarjoe.abilityeffects.AbilityEffects;
import net.guitarjoe.abilityeffects.client.packets.ClientBoundPackets;
import net.guitarjoe.abilityeffects.entity.RidableEnderDragonRegister;
import net.guitarjoe.abilityeffects.client.entity.RidableEnderDragonRenderer;
import net.guitarjoe.abilityeffects.client.event.ModClientEvents;
import net.guitarjoe.abilityeffects.client.keybind.KeyMappings;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.resources.Identifier;

public class AbilityEffectsClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		ClientBoundPackets.registerPackets(PayloadTypeRegistry.serverboundPlay());

		KeyMappings.register();

		ClientTickEvents.END_CLIENT_TICK.register(ModClientEvents::onEndTick);

		HudElementRegistry.addFirst(Identifier.fromNamespaceAndPath(AbilityEffects.MOD_ID, "ability_hud"), ModClientEvents::addAbilityHudElement);

		EntityRenderers.register(RidableEnderDragonRegister.RIDABLE_ENDER_DRAGON, RidableEnderDragonRenderer::new);
	}
}
