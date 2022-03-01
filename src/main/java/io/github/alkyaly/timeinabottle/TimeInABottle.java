package io.github.alkyaly.timeinabottle;

import io.github.alkyaly.timeinabottle.entity.AcceleratorEntity;
import io.github.alkyaly.timeinabottle.item.TimeInABottleItem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class TimeInABottle implements ModInitializer {

    public static final String MOD_ID = "timeinabottle";
    public static final Logger LOGGER = LogManager.getLogger("Time in a Bottle");
    public static ModConfig config;

    public static final TimeInABottleItem TIME_IN_A_BOTTLE = new TimeInABottleItem(new FabricItemSettings().maxCount(1).rarity(Rarity.UNCOMMON).group(ItemGroup.TOOLS));
    public static final EntityType<AcceleratorEntity> ACCELERATOR = Registry.register(
        Registry.ENTITY_TYPE,
        id("accelerator"),
        FabricEntityTypeBuilder.<AcceleratorEntity>create(SpawnGroup.MISC, AcceleratorEntity::new)
                .dimensions(EntityDimensions.fixed(.1f, .1f)).build()
    );

    @Override
    public void onInitialize() {
        config = new ModConfig();
        Registry.register(Registry.ITEM, id("time_in_a_bottle"), TIME_IN_A_BOTTLE);

        ServerLifecycleEvents.START_DATA_PACK_RELOAD.register((__, ___) -> {
            try {
                config.load();
            } catch (IOException e) {
                LOGGER.fatal("Something went wrong while reloading the config!", e);
            }
        });
    }

    public static Identifier id(String path) {
        return new Identifier(MOD_ID, path);
    }
}
