package io.github.alkyaly.timeinabottle;

import io.github.alkyaly.timeinabottle.entity.AcceleratorEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.registry.Registry;

import java.util.UUID;

@Environment(EnvType.CLIENT)
public class TimeInABottleClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.INSTANCE.register(TimeInABottle.ACCELERATOR, AcceleratorEntityRenderer::new);
        ClientPlayNetworking.registerGlobalReceiver(TimeInABottle.id("spawn_entity"), TimeInABottleClient::spawnEntity);
    }

    private static void spawnEntity(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender sender) {
        final EntityType<?> type = Registry.ENTITY_TYPE.get(buf.readVarInt());
        final UUID entityUUID = buf.readUuid();
        final int entityID = buf.readVarInt();
        final double x = buf.readDouble();
        final double y = buf.readDouble();
        final double z = buf.readDouble();

        client.execute(() -> {
            final ClientWorld clientWorld = client.world;
            Entity entity = type.create(clientWorld);
            if (entity != null) {
                entity.setPos(x, y, z);
                entity.updatePosition(x, y, z);
                entity.updateTrackedPosition(x, y, z);
                entity.setId(entityID);
                entity.setUuid(entityUUID);
                clientWorld.addEntity(entityID, entity);
            }
        });
    }
}
