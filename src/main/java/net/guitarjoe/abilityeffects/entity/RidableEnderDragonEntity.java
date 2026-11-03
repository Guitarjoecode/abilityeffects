package net.guitarjoe.abilityeffects.entity;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.enderdragon.EnderDragonPart;
import net.minecraft.world.entity.boss.enderdragon.phases.EnderDragonPhase;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class RidableEnderDragonEntity extends EnderDragon {
    private int rideTicks = 0;
    private static final int MAX_RIDE_TICKS = 4800;

    public RidableEnderDragonEntity(EntityType<? extends EnderDragon> entityType, Level level) {
        super(entityType, level);
        this.noPhysics = false;
    }

    @Override
    protected boolean canAddPassenger(Entity passenger) {
        return this.getPassengers().isEmpty();
    }

    @Nullable
    @Override
    public LivingEntity getControllingPassenger() {
        return this.getFirstPassenger() instanceof LivingEntity ? (LivingEntity) this.getFirstPassenger() : null;
    }

    @Override
    protected void positionRider(Entity passenger, MoveFunction moveFunction) {
        super.positionRider(passenger, moveFunction);
        if (passenger instanceof LivingEntity rider) {
            rider.setYRot(this.getYRot() + 180.0F);
            rider.yRotO = rider.getYRot();
        }
    }

    @Override
    public void travel(Vec3 travelVector) {
        if (this.isAlive() && this.getControllingPassenger() instanceof Player) {
            return;
        }
        super.travel(travelVector);
    }

    @Override
    public void aiStep() {
        this.oFlapTime = this.flapTime;
        this.rideTicks++;

        if (this.rideTicks >= MAX_RIDE_TICKS && !this.level().isClientSide()) {
            this.discard();
            return;
        }

        LivingEntity passenger = this.getControllingPassenger();

        if (passenger instanceof Player player) {
            if (this.getPhaseManager().getCurrentPhase().getPhase() != EnderDragonPhase.HOVERING) {
                this.getPhaseManager().setPhase(EnderDragonPhase.HOVERING);
            }

            this.setYRot(player.getYRot() - 180.0F);
            this.yRotO = this.getYRot();
            this.setXRot(player.getXRot() * 0.5F);
            this.setRot(this.getYRot(), this.getXRot());
            this.yBodyRot = this.getYRot();
            this.yHeadRot = this.getYRot();

            Vec3 lookDirection = player.getLookAngle();
            double speed = 1.3;

            double movementX = lookDirection.x * speed;
            double movementY = lookDirection.y * speed;
            double movementZ = lookDirection.z * speed;

            this.setDeltaMovement(movementX, movementY, movementZ);
            this.move(MoverType.SELF, this.getDeltaMovement());

            this.flapTime += 0.15F;
        } else {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.8, 0.98, 0.8));
            if (!this.isNoGravity() && !this.onGround()) {
                this.setDeltaMovement(this.getDeltaMovement().add(0, -0.04, 0));
            }
            this.flapTime += 0.05F;
            this.move(MoverType.SELF, this.getDeltaMovement());
        }

        for (EnderDragonPart part : this.getSubEntities()) {
            part.setPos(this.getX(), this.getY(), this.getZ());
            part.xo = this.getX();
            part.yo = this.getY();
            part.zo = this.getZ();
        }

        if (!this.level().isClientSide()) {
            this.updateFluidInteraction();
        }
    }

    @Override
    protected void tickDeath() {
        this.deathTime++;
        if (this.deathTime >= 100 && !this.level().isClientSide()) {
            this.remove(RemovalReason.KILLED);
        }
    }
}
