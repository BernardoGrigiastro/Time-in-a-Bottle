package io.github.alkyaly.timeinabottle;

import io.github.alkyaly.timeinabottle.entity.AcceleratorEntityRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;

@Environment(EnvType.CLIENT)
public class TimeInABottleClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.INSTANCE.register(TimeInABottle.ACCELERATOR, AcceleratorEntityRenderer::new);
    }
}
