package net.guitarjoe.abilityeffects.ability.util;

import net.guitarjoe.abilityeffects.ability.Ability;
import net.guitarjoe.abilityeffects.attachment.AttachmentTypes;
import net.guitarjoe.abilityeffects.entity.RidableEnderDragonEntity;
import net.guitarjoe.abilityeffects.entity.RidableEnderDragonRegister;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.projectile.hurtingprojectile.WitherSkull;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.ExplosionDamageCalculator;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class SpecialAbilityAttributeHandler {
    public static void executeSpecialEffect(Ability ability, ServerPlayer player) {
        Map<Ability, Integer> cooldowns = player.getAttachedOrCreate(AttachmentTypes.COOLDOWNS, HashMap::new);

        if (cooldowns.getOrDefault(ability, 0) > 0) {
            return;
        }

        switch (ability) {
            case SPEED_ABILITY -> executeSpeedSpecialEffect(player);
            case FIRE_RESISTANCE_ABILITY -> executeFireResistanceSpecialEffect(player);
            case HEALTH_BOOST_ABILITY -> executeHealthBoostSpecialEffect(player);
            case FEATHER_FALLING_ABILITY -> executeFeatherFallingSpecialEffect(player);
            case STRENGTH_ABILITY -> executeStrengthSpecialEffect(player);
            case MINING_ABILITY -> executeMiningSpecialEffect(player);
            case WARDEN_ABILITY -> executeWardenSpecialEffect(player);
            case WITHER_ABILITY -> executeWitherSpecialEffect(player);
            case PULL_ABILITY -> executePullSpecialEffect(player);
            // case TRACKER_ABILITY -> executeTrackerSpecialEffect(player, cooldowns); in AttackEntityCallback
            case ENDER_DRAGON_ABILITY -> executeEnderDragonSpecialEffect(player);
            default -> {}
        }

        Map<Ability, Integer> updatedCooldowns = new HashMap<>(cooldowns);
        updatedCooldowns.put(ability, ability.getCooldown());

        player.setAttached(AttachmentTypes.COOLDOWNS, updatedCooldowns);
    }

    private static void executeSpeedSpecialEffect(ServerPlayer player) {
        Vec3 lookAngle = player.getLookAngle();

        double boostStrength = 5.0;
        player.setDeltaMovement(lookAngle.x * boostStrength, 0.2, lookAngle.z * boostStrength);
        player.hurtMarked = true;

        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.LUNGE_1, SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    private static void executeFireResistanceSpecialEffect(ServerPlayer player) {
        ServerLevel level = player.level();
        ExplosionDamageCalculator customCalculator = new ExplosionDamageCalculator() {
            @Override
            public boolean shouldDamageEntity(@NonNull Explosion explosion, @NonNull Entity entity) {
                if (entity == player) {
                    return false;
                }
                return super.shouldDamageEntity(explosion, entity);
            }
        };

        level.explode(
                player,
                player.damageSources().explosion(player, player),
                customCalculator,
                player.getX(), player.getY(), player.getZ(),
                5.0F,
                true,
                Level.ExplosionInteraction.BLOCK

        );

        BlockPos playerPos = player.blockPosition();
        int radius = 10;

        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    if ((x * x + y * y + z * z) <= radius * radius) {
                        BlockPos targetPos = playerPos.offset(x, y, z);

                        if (level.getFluidState(targetPos).is(Fluids.WATER)) {
                            level.setBlock(targetPos, Blocks.AIR.defaultBlockState(), 3);
                        }
                    }
                }
            }
        }
    }

    private static void executeHealthBoostSpecialEffect(ServerPlayer player) {
        player.heal(8.0F);
    }

    private static void executeFeatherFallingSpecialEffect(ServerPlayer player) {
        double boostStrength = 5.0;
        player.setDeltaMovement(0, boostStrength, 0);
        player.hurtMarked = true;

        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.WIND_CHARGE_BURST, SoundSource.PLAYERS, 1.0F, 1.0F);
    }

    private static void executeStrengthSpecialEffect(ServerPlayer player) {
        MobEffectInstance effectInstance = new MobEffectInstance(MobEffects.STRENGTH, 80, 2, true, false, false);
        player.addEffect(effectInstance);
    }

    private static void executeMiningSpecialEffect(ServerPlayer player) {
        BreakStateManager.activateForPlayer(player.getUUID());
    }

    private static void executeWardenSpecialEffect(ServerPlayer player) {
        ServerLevel world = player.level();
        double maxDistance = 20.0;

        Vec3 startPos = player.getEyePosition(1.0F);
        Vec3 lookVec = player.getViewVector(1.0F);
        Vec3 endPos = startPos.add(lookVec.scale(maxDistance));
        AABB area = player.getBoundingBox().expandTowards(lookVec.scale(maxDistance)).inflate(1.0D);

        EntityHitResult hitResult = null;
        double closestDistance = maxDistance;

        for (Entity entity : world.getEntities(player, area, e -> e instanceof LivingEntity && !e.isSpectator())) {
            AABB entityBox = entity.getBoundingBox().inflate(entity.getPickRadius());
            Optional<Vec3> clipResult = entityBox.clip(startPos, endPos);

            if (clipResult.isPresent()) {
                double distanceToHit = startPos.distanceTo(clipResult.get());
                if (distanceToHit < closestDistance) {
                    closestDistance = distanceToHit;
                    hitResult = new EntityHitResult(entity, clipResult.get());
                }
            }
        }

        if (hitResult != null && hitResult.getEntity() instanceof LivingEntity target) {
            target.hurtServer(world, world.damageSources().sonicBoom(player), 13.0F);

            double dX = target.getX() - player.getX();
            double dZ = target.getZ() - player.getZ();
            target.knockback(0.5D, -dX, -dZ, player.damageSources().playerAttack(player), 0.4F);

            double distanceToTarget = startPos.distanceTo(target.getEyePosition());
            for (int i = 1; i < (int) distanceToTarget; i++) {
                Vec3 particlePos = startPos.add(lookVec.scale(i));
                world.sendParticles(
                        ParticleTypes.SONIC_BOOM,
                        particlePos.x, particlePos.y, particlePos.z,
                        1, 0.0, 0.0, 0.0, 0.0
                );
            }

            world.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.WARDEN_SONIC_BOOM,
                    SoundSource.PLAYERS, 3.0F, 1.0F);
        } else {
            world.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.WARDEN_SONIC_BOOM,
                    SoundSource.PLAYERS, 3.0F, 1.0F);

            for (int i = 1; i < (int) maxDistance; i++) {
                Vec3 particlePos = startPos.add(lookVec.scale(i));
                world.sendParticles(ParticleTypes.SONIC_BOOM, particlePos.x, particlePos.y, particlePos.z, 1, 0.0, 0.0, 0.0, 0.0);
            }
        }
    }

    private static void executeWitherSpecialEffect(ServerPlayer player) {
        ServerLevel serverLevel = player.level();

        Vec3 lookAngle = player.getLookAngle();

        double lookX = lookAngle.x;
        double lookZ = lookAngle.z;
        double lateralX = -lookZ * 0.4;
        double lateralZ = lookX * 0.4;

        for (int i = 0; i < 4; i++) {
            WitherSkull skull = new WitherSkull(EntityTypes.WITHER_SKULL, serverLevel) {
                @Override
                public void tick() {
                    super.tick();
                    if (this.getY() > 320.0) {
                        this.discard();
                    }
                }
            };

            skull.setOwner(player);
            skull.setDangerous(true);

            double startX = player.getX();
            double startY = player.getEyeY() - 0.2;
            double startZ = player.getZ();

            double offset = (i - 1.5);
            startX += lateralX * offset;
            startZ += lateralZ * offset;

            skull.setPos(startX, startY, startZ);
            skull.setXRot(player.getXRot());
            skull.setYRot(player.getYRot());

            skull.shoot(lookAngle.x, lookAngle.y, lookAngle.z, 2.5F, 0.0F);

            serverLevel.addFreshEntity(skull);
        }
    }

    private static void executePullSpecialEffect(ServerPlayer player) {
        AABB searchBox = player.getBoundingBox().inflate(50.0);

        List<ServerPlayer> nearbyPlayers = player.level().getEntitiesOfClass(
                ServerPlayer.class,
                searchBox,
                targetPlayer -> true
        );

        for (ServerPlayer target : nearbyPlayers) {
            if (target == player || target.isSpectator()) continue;

            for (int tick = 0; tick < 300; tick++) {
                long delayInMilliseconds = tick * 50L;

                CompletableFuture.runAsync(() -> {

                    player.level().getServer().execute(() -> {

                        if (player.isRemoved() || target.isRemoved()) return;

                        Vec3 direction = player.position().subtract(target.position());
                        double distance = direction.length();

                        if (distance < 1.5) {
                            target.setDeltaMovement(0, 0, 0);
                            return;
                        }

                        double pullStrength = 0.25;
                        Vec3 motion = direction.normalize().scale(pullStrength);

                        target.setDeltaMovement(motion.x, motion.y + 0.05, motion.z);
                        target.connection.send(new ClientboundSetEntityMotionPacket(target));
                    });

                }, CompletableFuture.delayedExecutor(delayInMilliseconds, TimeUnit.MILLISECONDS));
            }
        }

    }

    private static void executeEnderDragonSpecialEffect(ServerPlayer player) {
        ServerLevel world = player.level();

        RidableEnderDragonEntity dragon = RidableEnderDragonRegister.RIDABLE_ENDER_DRAGON.create(
                world,
                EntitySpawnReason.TRIGGERED
        );

        if (dragon != null) {
            dragon.setPos(player.getX(), player.getY(), player.getZ());
            dragon.setYRot(player.getYRot());
            dragon.setXRot(player.getXRot());

            world.addFreshEntity(dragon);

            dragon.startRiding(player);

            world.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.ENDER_DRAGON_GROWL, SoundSource.PLAYERS, 1.0F, 1.0F);
        }
    }


    public static void handleCooldownTick(ServerPlayer player) {
        if (!player.hasAttached(AttachmentTypes.COOLDOWNS)) return;

        Map<Ability, Integer> cooldowns = player.getAttached(AttachmentTypes.COOLDOWNS);
        Map<Ability, Integer> updatedCooldowns = new HashMap<>(cooldowns);
        boolean changed = false;

        for (Map.Entry<Ability, Integer> entry : new HashMap<>(cooldowns).entrySet()) {
            if (entry.getValue() > 0) {
                updatedCooldowns.put(entry.getKey(), entry.getValue() - 1);
            } else {
                updatedCooldowns.remove(entry.getKey());
            }
            changed = true;
        }

        if (changed) {
            player.setAttached(AttachmentTypes.COOLDOWNS, updatedCooldowns);
        }
    }
}
