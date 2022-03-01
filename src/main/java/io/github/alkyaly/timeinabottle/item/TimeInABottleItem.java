package io.github.alkyaly.timeinabottle.item;

import io.github.alkyaly.timeinabottle.TimeInABottle;
import io.github.alkyaly.timeinabottle.entity.AcceleratorEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import org.apache.commons.lang3.ArrayUtils;

import java.util.List;

public class TimeInABottleItem extends Item implements FabricItem {

    private static final String DATA_KEY = "timeData";
    private static final String STORED_KEY = "storedTime";

    public TimeInABottleItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext ctx) {
        var world = ctx.getWorld();

        if (world.isClient) return ActionResult.PASS;

        var player = ctx.getPlayer();
        var pos = ctx.getBlockPos();
        var nbt = ctx.getStack().getSubNbt(DATA_KEY);
        var box = new Box(pos.add(.5, .5, .5)).shrink(.2, .2, .2);

        List<AcceleratorEntity> accelerators = world.getEntitiesByClass(AcceleratorEntity.class, box, e -> true);

        if (accelerators.isEmpty()) {
            int timeAvailable = nbt.getInt(STORED_KEY);

            if (timeAvailable >= 20 * TimeInABottle.config.getDuration()) {
                if (!player.getAbilities().creativeMode) {
                    nbt.putInt(STORED_KEY, timeAvailable - 20 * TimeInABottle.config.getDuration());
                }

                var accelerator = new AcceleratorEntity(world, pos);
                accelerator.setTimeRate(1);
                accelerator.setRemainingTime(20 * TimeInABottle.config.getDuration());
                accelerator.setBoundingBox(box);

                world.spawnEntity(accelerator);
                playSound(world, pos, accelerator.getTimeRate());

                return ActionResult.SUCCESS;
            }
        } else {
            AcceleratorEntity accelerator = accelerators.get(0);
            int curRate = accelerator.getTimeRate();
            int usedUp = 20 * TimeInABottle.config.getDuration() - accelerator.getRemainingTime();
            var speedLevels = TimeInABottle.config.getSpeedLevels();

            if (curRate < speedLevels[speedLevels.length - 1]) {
                int curIndex = ArrayUtils.indexOf(speedLevels, curRate);
                int next = speedLevels[curIndex + 1];
                int timeRequired = next / 2 * 20 * TimeInABottle.config.getDuration();

                int timeAvailable = nbt.getInt(STORED_KEY);

                if (timeAvailable >= timeRequired) {
                    int added = (next * usedUp - curRate * usedUp) / next;

                    if (!player.getAbilities().creativeMode) {
                        nbt.putInt(STORED_KEY, timeAvailable - timeRequired);
                    }

                    accelerator.setTimeRate(next);
                    accelerator.setRemainingTime(accelerator.getRemainingTime() + added);
                    playSound(world, pos, next);

                    return ActionResult.SUCCESS;
                }

            }
        }
        return super.useOnBlock(ctx);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        if (world.isClient) return;

        int time = TimeInABottle.config.getTimeSecond();

        if (world.getTime() % 20 == 0) {
            NbtCompound timeData = stack.getOrCreateSubNbt(DATA_KEY);

            if (timeData.getInt(STORED_KEY) < TimeInABottle.config.getMaxTime()) {
                timeData.putInt(STORED_KEY, timeData.getInt(STORED_KEY) + time);
            }
        }

        if (world.getTime() % 60 == 0 && entity instanceof PlayerEntity player) {
            for (int i = 0; i < player.getInventory().size(); i++) {
                ItemStack other = player.getInventory().getStack(i);

                if (other.isOf(stack.getItem()) && other != stack) {
                    NbtCompound duplicate = other.getOrCreateSubNbt(DATA_KEY);
                    NbtCompound original = stack.getOrCreateSubNbt(DATA_KEY);

                    int duplicateTime = duplicate.getInt(STORED_KEY);
                    int originalTime = original.getInt(STORED_KEY);

                    if (originalTime < duplicateTime) {
                        original.putInt(STORED_KEY, 0);
                    }
                }
            }
        }
    }

    @Environment(EnvType.CLIENT)
    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext tooltipContext) {
        int storedTime = stack.getOrCreateSubNbt(DATA_KEY).getInt(STORED_KEY);

        int storedSeconds = storedTime / 20;

        int hours = storedSeconds / 3600;
        int minutes = (storedSeconds % 3600) / 60;
        int seconds = storedSeconds % 60;
        tooltip.add(new TranslatableText("item.timeinabottle.time_in_a_bottle.tooltip", hours, minutes, seconds).formatted(Formatting.AQUA));
    }

    @Override
    public boolean allowNbtUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack) {
        return false;
    }

    protected static void playSound(World world, BlockPos pos, int nextRate) {
        switch (nextRate) {
            case 1 -> world.playSound(null, pos, SoundEvents.BLOCK_NOTE_BLOCK_HARP, SoundCategory.BLOCKS, 0.5f, 0.749154f);
            case 2 -> world.playSound(null, pos, SoundEvents.BLOCK_NOTE_BLOCK_HARP, SoundCategory.BLOCKS, 0.5F, 0.793701F);
            case 4, 32 -> world.playSound(null, pos, SoundEvents.BLOCK_NOTE_BLOCK_HARP, SoundCategory.BLOCKS, 0.5F, 0.890899F);
            case 8 -> world.playSound(null, pos, SoundEvents.BLOCK_NOTE_BLOCK_HARP, SoundCategory.BLOCKS, 0.5F, 1.059463F);
            case 16 -> world.playSound(null, pos, SoundEvents.BLOCK_NOTE_BLOCK_HARP, SoundCategory.BLOCKS, 0.5F, 0.943874F);
            default -> world.playSound(null, pos, SoundEvents.BLOCK_NOTE_BLOCK_HARP, SoundCategory.BLOCKS, 0.5F, 1F);
        }
    }
}
