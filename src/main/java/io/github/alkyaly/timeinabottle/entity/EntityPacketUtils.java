package io.github.alkyaly.timeinabottle.entity;

import io.github.alkyaly.timeinabottle.TimeInABottle;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class EntityPacketUtils {

    public static final Identifier SPAWN_PACKET_ID = new Identifier(TimeInABottle.MOD_ID, "spawn_packet");

    public static Packet<?> createPacket(Entity entity) {
        final PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
        buf.writeVarInt(Registry.ENTITY_TYPE.getRawId(entity.getType()));
        buf.writeUuid(entity.getUuid());
        buf.writeVarInt(entity.getEntityId());
        buf.writeDouble(entity.getX());
        buf.writeDouble(entity.getY());
        buf.writeDouble(entity.getZ());
        return ServerSidePacketRegistry.INSTANCE.toPacket(SPAWN_PACKET_ID, buf);
    }
}
