package io.github.alkyaly.timeinabottle.entity;

import io.github.alkyaly.timeinabottle.TimeInABottle;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.TagKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public class AcceleratorEntity extends Entity {
    public static final TagKey<Block> ACCELERATION_BLACKLIST = TagKey.of(Registry.BLOCK_KEY, TimeInABottle.id("acceleration_blacklist"));
    private static final TrackedData<Integer> TIME_RATE = DataTracker.registerData(AcceleratorEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private int remainingTime;

    public AcceleratorEntity(EntityType<? extends Entity> type, World world) {
        super(type, world);
    }

    public AcceleratorEntity(World world, BlockPos target) {
        super(TimeInABottle.ACCELERATOR, world);
        this.noClip = true;
        this.setPos(target.getX(), target.getY(), target.getZ());
        this.updateTrackedPosition(target.getX(), target.getY(), target.getZ());
    }

    @Override
    public void tick() {
        super.tick();
        if (world.isClient) return;

        var pos = getBlockPos();
        BlockEntityTicker<BlockEntity> ticker = null;
        BlockState state = world.getBlockState(pos);
        BlockEntity be = world.getBlockEntity(pos);

        if (!state.isIn(ACCELERATION_BLACKLIST)) {
            if (state.getBlock() instanceof BlockEntityProvider provider && be != null) {
                //noinspection unchecked
                ticker = provider.getTicker(world, state, (BlockEntityType<BlockEntity>) be.getType());
            }

            for (int i = 0; i < getTimeRate(); i++) {
                if (ticker != null) {
                    ticker.tick(world, pos, state, be);
                }

                if (world.random.nextInt(1365) == 0) {
                    BlockState targetBlock = world.getBlockState(pos);
                    if (targetBlock.getBlock().hasRandomTicks(targetBlock)) {
                        targetBlock.randomTick((ServerWorld) world, pos, world.random);
                    }
                }
            }
        }
        remainingTime--;
        if (remainingTime <= 0) {
            discard();
        }
    }

    @Override
    protected void initDataTracker() {
        dataTracker.startTracking(TIME_RATE, 1);
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound tag) {
        remainingTime = tag.getInt("remainingTime");
        setTimeRate(tag.getInt("timeRate"));
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound tag) {
        tag.putInt("remainingTime", remainingTime);
        tag.putInt("timeRate", getTimeRate());
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
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
