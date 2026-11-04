package net.guitarjoe.abilityeffects.packets;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.guitarjoe.abilityeffects.ability.Ability;
import net.guitarjoe.abilityeffects.ability.util.SpecialAbilityAttributeHandler;
import net.guitarjoe.abilityeffects.attachment.AttachmentTypes;
import net.guitarjoe.abilityeffects.packets.packet.*;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerBoundPackets {
    public static void registerPackets(PayloadTypeRegistry<RegistryFriendlyByteBuf> registry) {
        PayloadTypeRegistry.serverboundPlay().register(
                AbilityPayloadC2S.TYPE,
                AbilityPayloadC2S.STREAM_CODEC
        );
        PayloadTypeRegistry.serverboundPlay().register(
                RequestPlayerCoordsPayloadC2S.TYPE,
                RequestPlayerCoordsPayloadC2S.STREAM_CODEC
        );
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

        ServerPlayNetworking.registerGlobalReceiver(
                AbilityPayloadC2S.TYPE,
                ServerBoundPackets::handleAbilityPayload
        );
        ServerPlayNetworking.registerGlobalReceiver(
                RequestPlayerCoordsPayloadC2S.TYPE,
                ServerBoundPackets::handleRequestPlayerCoordsPayload
        );
    }

    public static void handleAbilityPayload(AbilityPayloadC2S payload, ServerPlayNetworking.Context context) {
        ServerPlayer player = context.player();
        int slot = payload.slotindex();

        context.server().execute(() -> {
            Set<Ability> activeSet = player.getAttached(AttachmentTypes.ACTIVE_ABILITIES);

            if (activeSet == null || activeSet.isEmpty()) {
                player.sendSystemMessage(Component.translatable("ability.abilityeffects.dont_have_ability"));
                return;
            }

            List<Ability> abilityList = new ArrayList<>(activeSet);

            if (slot >= abilityList.size()) {
                player.sendSystemMessage(Component.translatable("ability.abilityeffects.ability_slot_empty"));
                return;
            }

            Ability chosenAbility = abilityList.get(slot);

            if (chosenAbility == Ability.TRACKER_ABILITY) {
                return;
            }

            SpecialAbilityAttributeHandler.executeSpecialEffect(chosenAbility, player);
        });
    }

    public static void handleRequestPlayerCoordsPayload(RequestPlayerCoordsPayloadC2S payload, ServerPlayNetworking.Context context) {
        ServerPlayer sendingPlayer = context.player();

        context.server().execute(() -> {
            Map<Ability, Integer> cooldowns = sendingPlayer.getAttachedOrCreate(AttachmentTypes.COOLDOWNS, HashMap::new);
            int currentCooldown = cooldowns.getOrDefault(Ability.TRACKER_ABILITY, 0);

            if (currentCooldown > 0) {
                return;
            }

            ServerPlayer targetPlayer = context.server().getPlayerList().getPlayer(payload.targetProfile().id());

            if (targetPlayer != null) {
                int x = (int) targetPlayer.getX();
                int y = (int) targetPlayer.getY();
                int z = (int) targetPlayer.getZ();
                String worldName = targetPlayer.level().dimension().toString();

                sendingPlayer.sendSystemMessage(Component.literal(
                        "Player " + targetPlayer.getScoreboardName() +
                                " is at: X: " + x + " Y: " + y + " Z: " + z + " (" + worldName + ")"
                ));

                if (targetPlayer.level() != sendingPlayer.level()) {
                    sendingPlayer.sendSystemMessage(Component.literal(
                            "Tip: The Player is in another dimension"
                    ));
                    return;
                }

                final ResourceKey<Level> targetDimension = targetPlayer.level().dimension();

                ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
                AtomicInteger ticks = new AtomicInteger(0);
                int maxTicks = 240;

                scheduler.scheduleAtFixedRate(() -> {
                    context.server().execute(() -> {
                        Set<Ability> currentAbilities = sendingPlayer.getAttached(AttachmentTypes.ACTIVE_ABILITIES);
                        if (currentAbilities == null || !currentAbilities.contains(Ability.TRACKER_ABILITY)) {
                            scheduler.shutdown();
                            return;
                        }

                        if (sendingPlayer.isRemoved() || targetPlayer.isRemoved() || sendingPlayer.level().dimension() != targetDimension) {
                            scheduler.shutdown();
                            return;
                        }

                        Vec3 start = sendingPlayer.getEyePosition();
                        Vec3 currentTargetPos = targetPlayer.position().add(0, targetPlayer.getBbHeight() / 2.0, 0);

                        Vec3 direction = currentTargetPos.subtract(start);
                        double distance = direction.length();

                        if (distance <= 10.0) {
                            scheduler.shutdown();
                            return;
                        }

                        Vec3 step = direction.normalize().scale(0.5);
                        DustParticleOptions redLaser = new DustParticleOptions(0xFF0000, 1.5f);
                        ServerLevel serverLevel = sendingPlayer.level();
                        Vec3 currentPos = start;

                        for (double d = 0; d < distance; d += 0.5) {
                            serverLevel.sendParticles(
                                    redLaser,
                                    currentPos.x, currentPos.y, currentPos.z,
                                    1,
                                    0.0, 0.0, 0.0,
                                    0.0
                            );
                            currentPos = currentPos.add(step);
                        }
                    });

                    if (ticks.incrementAndGet() >= maxTicks) {
                        scheduler.shutdown();
                    }
                }, 0, 500, TimeUnit.MILLISECONDS);

                Map<Ability, Integer> updatedCooldowns = new HashMap<>(cooldowns);
                updatedCooldowns.put(Ability.TRACKER_ABILITY, Ability.TRACKER_ABILITY.getCooldown());
                sendingPlayer.setAttached(AttachmentTypes.COOLDOWNS, updatedCooldowns);

            } else {
                sendingPlayer.sendSystemMessage(Component.literal(
                        "The selected player is offline"
                ));
            }
        });
    }
}