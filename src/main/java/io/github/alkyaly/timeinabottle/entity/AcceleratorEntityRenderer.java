package io.github.alkyaly.timeinabottle.entity;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public class AcceleratorEntityRenderer extends EntityRenderer<AcceleratorEntity> {

    public AcceleratorEntityRenderer(EntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(AcceleratorEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);

        /*
            TODO: Learn how to render those things, or pay someone to do it
         */
    }

    @Override
    public Identifier getTexture(AcceleratorEntity entity) {
        return null;
    }
}