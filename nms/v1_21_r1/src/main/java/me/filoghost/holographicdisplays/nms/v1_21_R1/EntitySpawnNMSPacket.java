/*
 * Copyright (C) filoghost and contributors
 *
 * SPDX-License-Identifier: GPL-3.0-or-later
 */
package me.filoghost.holographicdisplays.nms.v1_21_R1;

import me.filoghost.holographicdisplays.common.PositionCoordinates;
import me.filoghost.holographicdisplays.nms.common.EntityID;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutSpawnEntity;

class EntitySpawnNMSPacket extends VersionNMSPacket {

    private final Packet<?> rawPacket;

    /**
     * This is the constructor in Minecraft to create a PacketPlayOutSpawnEntity:
     private PacketPlayOutSpawnEntity(RegistryFriendlyByteBuf var0) {
         this.d = var0.l();  // Deobfuscated: var.readVarInt()
         this.e = var0.n();  // Deobfuscated: var.readUUID()
         this.f = (EntityTypes) ByteBufCodecs.a(Registries.z).decode(var0);  // Not sure but this is the entity type
         this.g = var0.readDouble();
         this.h = var0.readDouble();
         this.i = var0.readDouble();
         this.m = var0.readByte();  // Beware that these are out of order!
         this.n = var0.readByte();
         this.o = var0.readByte();
         this.p = var0.l();  // Deobfuscated: var.readVarInt()
         this.j = var0.readShort();  // Read the variables in the correct order again
         this.k = var0.readShort();
         this.l = var0.readShort();
     }
     */

    EntitySpawnNMSPacket(EntityID entityID, int entityTypeID, PositionCoordinates position, double positionOffsetY) {
        PacketByteBuffer packetByteBuffer = PacketByteBuffer.get();

        packetByteBuffer.writeVarInt(entityID.getNumericID());
        packetByteBuffer.writeUUID(entityID.getUUID());
        packetByteBuffer.writeVarInt(entityTypeID);

        // Position
        packetByteBuffer.writeDouble(position.getX());
        packetByteBuffer.writeDouble(position.getY() + positionOffsetY);
        packetByteBuffer.writeDouble(position.getZ());

        // Rotation
        packetByteBuffer.writeByte(0);
        packetByteBuffer.writeByte(0);
//        packetByteBuffer.writeByte(0);

        // To be honest I don't understand why "o" is not written here, reading the protocol and the constructor of PacketPlayOutSpawnEntity
        // it seems like it should be required... but it's not written here

        // Object data
        if (entityTypeID == EntityTypeID.ITEM) {
            packetByteBuffer.writeInt(1); // Velocity is present and zero (otherwise by default a random velocity is applied)
        } else {
            packetByteBuffer.writeInt(0);
        }

        // Velocity
        packetByteBuffer.writeShort(0);
        packetByteBuffer.writeShort(0);
        packetByteBuffer.writeShort(0);

        StreamCodec<RegistryFriendlyByteBuf, PacketPlayOutSpawnEntity> nmsCodec = PacketPlayOutSpawnEntity.a;
        RegistryFriendlyByteBuf registryFriendlyByteBuf = packetByteBuffer.getInternalBuffer();

        this.rawPacket = nmsCodec.decode(registryFriendlyByteBuf);
    }

    @Override
    Packet<?> getRawPacket() {
        return rawPacket;
    }

}
