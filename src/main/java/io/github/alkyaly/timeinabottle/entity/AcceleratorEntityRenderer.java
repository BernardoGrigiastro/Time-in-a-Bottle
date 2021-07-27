package io.github.alkyaly.timeinabottle.entity;

import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.Vector4f;

public class AcceleratorEntityRenderer extends EntityRenderer<AcceleratorEntity> {

    private static final Identifier RING_TEXTURE = new Identifier("timeinabottle", "textures/time_ring.png");

    public AcceleratorEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    public void render(AcceleratorEntity entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light) {
        super.render(entity, yaw, tickDelta, matrices, vertexConsumers, light);
        VertexConsumer consumer = vertexConsumers.getBuffer(RenderLayer.getEntityTranslucent(this.getTexture(entity)));
        matrices.push();
        matrices.translate(0.5, 0.5, 0.5);
        
        Vector4f vec1, vec2, vec3, vec4;
        for (Direction dir : Direction.values()) {
            matrices.push();
            Vec3i dirVector = dir.getVector();
            float angle = entity.age+tickDelta;
            matrices.multiply(new Quaternion(new Vec3f(dirVector.getX(), dirVector.getY(), dirVector.getZ()), angle, true));
            MatrixStack.Entry entry = matrices.peek();

            float offset = 0.5001f * (dir.getDirection() == Direction.AxisDirection.NEGATIVE ? -1 : 1); // 0.5001 is to prevent Z-fighting
            if (dir.getAxis() == Direction.Axis.X) {
                vec1 = new Vector4f(offset, -0.5f, -0.5f, 1.0f);
                vec2 = new Vector4f(offset,  0.5f, -0.5f, 1.0f);
                vec3 = new Vector4f(offset,  0.5f,  0.5f, 1.0f);
                vec4 = new Vector4f(offset, -0.5f,  0.5f, 1.0f);
            } else if (dir.getAxis() == Direction.Axis.Y) {
                vec1 = new Vector4f(-0.5f, offset, -0.5f, 1.0f);
                vec2 = new Vector4f(-0.5f, offset,  0.5f, 1.0f);
                vec3 = new Vector4f( 0.5f, offset,  0.5f, 1.0f);
                vec4 = new Vector4f( 0.5f, offset, -0.5f, 1.0f);
            } else {
                vec1 = new Vector4f(-0.5f, -0.5f, offset, 1.0f);
                vec2 = new Vector4f(-0.5f,  0.5f, offset, 1.0f);
                vec3 = new Vector4f( 0.5f,  0.5f, offset, 1.0f);
                vec4 = new Vector4f( 0.5f, -0.5f, offset, 1.0f);
            }
            
            vec1.transform(entry.getModel()); vec2.transform(entry.getModel()); vec3.transform(entry.getModel()); vec4.transform(entry.getModel());
            
            int frame;
            if (entity.getTimeRate() >= 32) {
                frame = 5;
            } else if (entity.getTimeRate() >= 16) {
                frame = 4;
            } else if (entity.getTimeRate() >= 8) {
                frame = 3;
            } else if (entity.getTimeRate() >= 4) {
                frame = 2;
            } else if (entity.getTimeRate() >= 2) {
                frame = 1;
            } else {
                frame = 0;
            }
            
            float minU = (frame)/6f;
            float maxU = (frame+1)/6f;
            
            consumer.vertex(vec1.getX(), vec1.getY(), vec1.getZ()).color(1.0f, 1.0f, 1.0f, 1.0f).texture(minU, 1.0f)
                .overlay(OverlayTexture.DEFAULT_UV).light(15728880).normal(dirVector.getX(), dirVector.getY(), dirVector.getZ()).next();
            consumer.vertex(vec2.getX(), vec2.getY(), vec2.getZ()).color(1.0f, 1.0f, 1.0f, 1.0f).texture(minU, 0.0f)
                .overlay(OverlayTexture.DEFAULT_UV).light(15728880).normal(dirVector.getX(), dirVector.getY(), dirVector.getZ()).next();
            consumer.vertex(vec3.getX(), vec3.getY(), vec3.getZ()).color(1.0f, 1.0f, 1.0f, 1.0f).texture(maxU, 0.0f)
                .overlay(OverlayTexture.DEFAULT_UV).light(15728880).normal(dirVector.getX(), dirVector.getY(), dirVector.getZ()).next();
            consumer.vertex(vec4.getX(), vec4.getY(), vec4.getZ()).color(1.0f, 1.0f, 1.0f, 1.0f).texture(maxU, 1.0f)
                .overlay(OverlayTexture.DEFAULT_UV).light(15728880).normal(dirVector.getX(), dirVector.getY(), dirVector.getZ()).next();
            matrices.pop();
        }
        matrices.pop();
    }

    @Override
    public Identifier getTexture(AcceleratorEntity entity) {
        return RING_TEXTURE;
    }
}