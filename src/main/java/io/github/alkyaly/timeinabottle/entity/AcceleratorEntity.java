package io.github.alkyaly.timeinabottle.entity;

import io.github.alkyaly.timeinabottle.TimeInABottle;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AcceleratorEntity extends Entity {

    private static final TrackedData<Integer> TIME_RATE = DataTracker.registerData(AcceleratorEntity.class, TrackedDataHandlerRegistry.INTEGER);
    int remainingTime;
    BlockPos target;
    @Environment(EnvType.CLIENT)
    int angle;

    public AcceleratorEntity(EntityType<? extends Entity> type, World world) {
        super(type, world);
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

        if (!world.isClient && this.target != null) {
            BlockEntityTicker<BlockEntity> ticker = null;
            BlockState state = world.getBlockState(getBlockPos());
            BlockEntity be = world.getBlockEntity(getBlockPos());

            if (state.getBlock() instanceof BlockEntityProvider provider && be != null) {
                //noinspection unchecked
                ticker = provider.getTicker(world, state, (BlockEntityType<BlockEntity>) be.getType());
            }

            for (int i = 0; i < getTimeRate(); i++) {
                if (ticker != null) {
                    ticker.tick(world, getBlockPos(), state, be);
                }

                if (world.random.nextInt(1365) == 0) {
                    BlockState targetBlock = world.getBlockState(target);
                    if (targetBlock.getBlock().hasRandomTicks(targetBlock)) {
                        targetBlock.randomTick((ServerWorld) world, target, world.random);
                    }
                }
            }
        }
        remainingTime--;
        if (remainingTime == 0 && !world.isClient) {
            remove(RemovalReason.DISCARDED);
        }
        if (world.isClient) {
            this.angle = this.angle + this.getTimeRate();
        }
    }

    @Override
    protected void initDataTracker() {
        dataTracker.startTracking(TIME_RATE, 1);
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound tag) {
        if (tag.contains("posX")) {
            target = new BlockPos(tag.getInt("posX"), tag.getInt("posY"), tag.getInt("posZ"));
        }
        remainingTime = tag.getInt("remainingTime");
        setTimeRate(tag.getInt("timeRate"));
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound tag) {
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
    
    // This field is to prevent the overlay from jumping when increasing the time rate
    @Environment(EnvType.CLIENT)
    public int getAngle() {
        return this.angle;
    }
}
