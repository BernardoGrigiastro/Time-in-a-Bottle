package io.github.alkyaly.timeinabottle.entity;

import io.github.alkyaly.timeinabottle.ModConfig;
import io.github.alkyaly.timeinabottle.TimeInABottle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

import java.util.UUID;

public class AcceleratorEntity extends Entity {

    private static final TrackedData<Integer> TIME_RATE = DataTracker.registerData(AcceleratorEntity.class, TrackedDataHandlerRegistry.INTEGER);
    int remainingTime;
    BlockPos target;

    public AcceleratorEntity(EntityType<? extends Entity> type, World world) {
        super(type, world);
    }

    @Environment(EnvType.CLIENT)
    public AcceleratorEntity(World world, double x, double y, double z, int entityID, UUID entityUUID) {
        super(TimeInABottle.ACCELERATOR, world);
        updatePosition(x, y, z);
        updateTrackedPosition(x, y, z);
        setEntityId(entityID);
        setUuid(entityUUID);
    }

    public AcceleratorEntity(World world, BlockPos target) {
        super(TimeInABottle.ACCELERATOR, world);
        this.noClip = true;
        this.target = target;
        this.setPos(target.getX(), target.getY(), target.getZ());
        this.updateTrackedPosition(target.getX(), target.getY(), target.getZ());
    }

    @Override
    public void tick() {
        super.tick();
        if(!world.isClient && this.target != null) {
            BlockEntity be = null;
            if (world.getBlockEntity(target) != null) {
                be = world.getBlockEntity(target);
            }
            for (int i = 0; i < getTimeRate(); i++) {
                if (be instanceof Tickable) ((Tickable) be).tick();
                if (world.random.nextInt(1500 / (world.getGameRules().getInt(GameRules.RANDOM_TICK_SPEED) * ModConfig.RANDOM_TICK)) == 0) {
                    BlockState targetBlock = world.getBlockState(target);
                    if (targetBlock.getBlock().hasRandomTicks(targetBlock)) {
                        targetBlock.randomTick((ServerWorld) world, target, world.random);
                    }
                }
            }
        }
        remainingTime--;
        if (remainingTime == 0 && !world.isClient) {
            remove();
        }
    }

    @Override
    protected void initDataTracker() {
        dataTracker.startTracking(TIME_RATE, 1);
    }

    @Override
    protected void readCustomDataFromTag(CompoundTag tag) {
        if (tag.contains("posX")) {
            target = new BlockPos(tag.getInt("posX"), tag.getInt("posY"), tag.getInt("posZ"));
        }
        remainingTime = tag.getInt("remainingTime");
        setTimeRate(tag.getInt("timeRate"));
    }

    @Override
    protected void writeCustomDataToTag(CompoundTag tag) {
        BlockPos pos = getBlockPos();
        if (pos != null) {
            tag.putInt("posX", pos.getX());
            tag.putInt("posY", pos.getY());
            tag.putInt("posZ", pos.getZ());
        }
        tag.putInt("remainingTime", remainingTime);
        tag.putInt("timeRate", getTimeRate());
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return EntityPacketUtils.createPacket(this);
    }

    public void setTimeRate(int timeRate) {
        dataTracker.set(TIME_RATE, timeRate);
    }

    public int getTimeRate() {
        return dataTracker.get(TIME_RATE);
    }

    public void setRemainingTime(int remainingTime) {
        this.remainingTime = remainingTime;
    }

    public int getRemainingTime() {
        return remainingTime;
    }
}
