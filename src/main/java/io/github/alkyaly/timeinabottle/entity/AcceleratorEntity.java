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
        this.noClip = true;
    }

    @Environment(EnvType.CLIENT)
    public AcceleratorEntity(World world, double x, double y, double z, int entityID, UUID entityUUID) {
        super(TimeInABottle.ACCELERATOR, world);
        updatePosition(x, y, z);
        updateTrackedPosition(x, y, z);
        setEntityId(entityID);
        setUuid(entityUUID);
    }

    public AcceleratorEntity(EntityType<? extends Entity> type, World world, BlockPos target) {
        this(TimeInABottle.ACCELERATOR, world);
        this.target = target;
        this.setPos(target.getX(), target.getY(), target.getZ());
        this.updateTrackedPosition(target.getX(), target.getY(), target.getZ());
    }

    @Override
    public void tick() {
        super.tick();
        if(!world.isClient) {
            BlockEntity be = world.getBlockEntity(target);
            for (int i = 0; i < getTimeRate(); i++) {

                if (be instanceof Tickable) {
                    ((Tickable) be).tick();
                }
                if (world.random.nextInt(1365 / (world.getGameRules().getInt(GameRules.RANDOM_TICK_SPEED) * ModConfig.RANDOM_TICK)) == 0) {
                    BlockState targetBlock = world.getBlockState(target);
                    if (targetBlock.getBlock().hasRandomTicks(targetBlock)) {
                        targetBlock.getBlock().randomTick(targetBlock, (ServerWorld) world, target, world.random);
                    }
                }
            }
        }
        remainingTime -= 1;
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
        target = readBlockPosFromTag(tag, "target");
        remainingTime = tag.getInt("remainingTime");
        setTimeRate(tag.getInt("timeRate"));
    }

    @Override
    protected void writeCustomDataToTag(CompoundTag tag) {
        writeBlockPosToTag(tag, "target", target);
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

    public static void writeBlockPosToTag(CompoundTag tag, String tagName, BlockPos pos) {
        CompoundTag posPounds = new CompoundTag();
        posPounds.putInt("posX", pos.getX());
        posPounds.putInt("posY", pos.getY());
        posPounds.putInt("getZ", pos.getZ());
        tag.put(tagName, posPounds);
    }

    public static BlockPos readBlockPosFromTag(CompoundTag tag, String tagName) {
        CompoundTag posPounds = (CompoundTag) tag.get(tagName);
        return new BlockPos(posPounds.getInt("posX"), posPounds.getInt("posY"), posPounds.getInt("posZ"));
    }
}
