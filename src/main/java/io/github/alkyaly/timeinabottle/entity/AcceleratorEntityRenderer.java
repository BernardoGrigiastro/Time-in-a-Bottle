package io.github.alkyaly.timeinabottle.entity;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL11;

public class AcceleratorEntityRenderer extends EntityRenderer<AcceleratorEntity> {

    public AcceleratorEntityRenderer(EntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(AcceleratorEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        int progress = (int) (2 * (entity.getRemainingTime() + tickDelta));
        matrices.push();
        //matrices.translate(entity.getX() + 0.5f, entity.getY() + 0.5f, entity.getZ() + 0.5f);
        draw(entity);
        //RenderSystem.rotatef(90, (float)entity.getX(), (float)entity.getY(), (float)entity.getZ());
        //RenderSystem.rotatef(progress, 0, 1, 0);
        //matrices.translate(-entity.getX(), -entity.getY(), -entity.getZ());
        matrices.pop();
    }

    @Override
    public Identifier getTexture(AcceleratorEntity entity) {
        return null;
    }

    protected void draw(AcceleratorEntity entity) {
        GL11.glBegin(GL11.GL_LINE_LOOP);
        GL11.glColor4i(255, 255, 255, 255);
        for (int theta = 0; theta <= 360; theta++) {
            double angle = 2 * Math.PI * theta / 360;
            double x = Math.cos(angle);
            double y = Math.sin(angle);
            GL11.glVertex2d(x, y);
        }
        GL11.glEnd();
        GL11.glFlush();
    }
}
