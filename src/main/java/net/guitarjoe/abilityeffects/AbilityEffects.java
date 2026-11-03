package net.guitarjoe.abilityeffects;

import com.mojang.brigadier.Command;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.guitarjoe.abilityeffects.ability.Ability;
import net.guitarjoe.abilityeffects.attachment.AttachmentTypes;
import net.guitarjoe.abilityeffects.attachment.handler.AbilityHandler;
import net.guitarjoe.abilityeffects.command.AbilityArgument;
import net.guitarjoe.abilityeffects.command.AbilityCommand;
import net.guitarjoe.abilityeffects.entity.RidableEnderDragonEntity;
import net.guitarjoe.abilityeffects.entity.RidableEnderDragonRegister;
import net.guitarjoe.abilityeffects.event.ModPlayerEvents;
import net.guitarjoe.abilityeffects.event.ModServerEvents;
import net.guitarjoe.abilityeffects.item.AbilityItems;
import net.guitarjoe.abilityeffects.limitedcrafting.TimesCraftedSavedData;
import net.guitarjoe.abilityeffects.packets.ServerBoundPackets;
import net.guitarjoe.abilityeffects.packets.packet.SyncTimesCraftedPayloadS2C;
import net.minecraft.commands.Commands;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;

import java.util.*;

public class AbilityEffects implements ModInitializer {
	public static final String MOD_ID = "abilityeffects";

	public static final Map<Item, Integer> maxTimesCrafted = new HashMap<>();

	@Override
	public void onInitialize() {
		ArgumentTypeRegistry.registerArgumentType(
				Identifier.fromNamespaceAndPath(MOD_ID, "ability"),
				AbilityArgument.class,
				SingletonArgumentInfo.contextFree(AbilityArgument::ability)
		);

		ServerBoundPackets.registerPackets(PayloadTypeRegistry.serverboundPlay());
		RidableEnderDragonRegister.registerEntity();

		ServerTickEvents.END_SERVER_TICK.register(ModServerEvents::onEndServerTick);

		ServerPlayConnectionEvents.JOIN.register(ModPlayerEvents::onPlayerJoin);
		ServerPlayConnectionEvents.DISCONNECT.register(ModPlayerEvents::onPlayerDisconnect);
		ServerPlayerEvents.COPY_FROM.register(ModPlayerEvents::onPlayerCopyFrom);

		AttackEntityCallback.EVENT.register(net.guitarjoe.abilityeffects.event.callback.AttackEntityCallback::handleAttack);
		UseEntityCallback.EVENT.register(net.guitarjoe.abilityeffects.event.callback.UseEntityCallback::handleEnderDragonClick);
		UseItemCallback.EVENT.register(net.guitarjoe.abilityeffects.event.callback.UseItemCallback::handleUseDragonBreath);

		maxTimesCrafted.put(AbilityItems.SPEED_ABILITY_ITEM, 2);
		maxTimesCrafted.put(AbilityItems.FIRE_RESISTANCE_ABILITY_ITEM, 2);
		maxTimesCrafted.put(AbilityItems.HEALTH_BOOST_ABILITY_ITEM, 2);
		maxTimesCrafted.put(AbilityItems.FEATHER_FALLING_ABILITY_ITEM, 2);
		maxTimesCrafted.put(AbilityItems.STRENGTH_ABILITY_ITEM, 2);
		maxTimesCrafted.put(AbilityItems.PULL_ABILITY_ITEM, 2);
		maxTimesCrafted.put(AbilityItems.MINING_ABILITY_ITEM, 2);
		maxTimesCrafted.put(AbilityItems.TRACKER_ABILITY_ITEM, 2);
		maxTimesCrafted.put(AbilityItems.WARDEN_ABILITY_ITEM, 1);
		maxTimesCrafted.put(AbilityItems.WITHER_ABILITY_ITEM, 1);
		maxTimesCrafted.put(AbilityItems.ENDER_DRAGON_ABILITY_ITEM, 1);

		FabricDefaultAttributeRegistry.register(RidableEnderDragonRegister.RIDABLE_ENDER_DRAGON, RidableEnderDragonEntity.createAttributes());

		CommandRegistrationCallback.EVENT.register(AbilityCommand::register);

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(Commands.literal("removeabilty")
					.executes(context -> {
						ServerPlayer player = context.getSource().getPlayer();
						for (Ability ability : Ability.values()) {
							AbilityHandler.toggleAbility(ability, player, false);
						}
						player.setAttached(AttachmentTypes.COOLDOWNS, new HashMap<>());

						return Command.SINGLE_SUCCESS;
					})
			);
		});
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(Commands.literal("craftreset")
					.executes(context -> {
						TimesCraftedSavedData savedData = TimesCraftedSavedData.get(context.getSource().getLevel());

						savedData.map.clear();
						savedData.setDirty();

						return 1;
					})
			);
		});
	}

	public static void syncTimesCrafted(ServerLevel level, TimesCraftedSavedData savedData) {
		SyncTimesCraftedPayloadS2C payload = new SyncTimesCraftedPayloadS2C(savedData.map);

		for (ServerPlayer player : level.players()) {
			ServerPlayNetworking.send(player, payload);
		}
	}
}
