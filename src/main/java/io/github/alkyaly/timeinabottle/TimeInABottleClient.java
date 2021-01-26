package io.github.alkyaly.timeinabottle;

import io.github.alkyaly.timeinabottle.entity.AcceleratorEntity;
import io.github.alkyaly.timeinabottle.entity.AcceleratorEntityRenderer;
import io.github.alkyaly.timeinabottle.entity.EntityPacketUtils;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.registry.Registry;

import java.util.UUID;

@Environment(EnvType.CLIENT)
public class TimeInABottleClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.INSTANCE.register(TimeInABottle.ACCELERATOR, (dispatcher, context) -> new AcceleratorEntityRenderer(dispatcher));

        // Super cool, mojank, don't y'all love when some non-sense things are hardcoded?
        ClientSidePacketRegistry.INSTANCE.register(EntityPacketUtils.SPAWN_PACKET_ID, ((context, buffer) -> {
            final EntityType<?> type = Registry.ENTITY_TYPE.get(buffer.readVarInt());
            final UUID entityUUID = buffer.readUuid();
            final int entityID = buffer.readVarInt();
            final double x = buffer.readDouble();
            final double y = buffer.readDouble();
            final double z = buffer.readDouble();

            context.getTaskQueue().execute(() -> {
                final MinecraftClient client = MinecraftClient.getInstance();
                final ClientWorld clientWorld = client.world;
                final AcceleratorEntity entity = new AcceleratorEntity(clientWorld, x, y, z, entityID, entityUUID);
                if (clientWorld != null) {
                    clientWorld.addEntity(entityID, entity);
                }

            });
        }));
    }
}
