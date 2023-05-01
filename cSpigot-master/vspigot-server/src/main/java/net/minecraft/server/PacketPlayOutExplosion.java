package net.minecraft.server;

import java.util.ArrayList;
import java.util.List;

public class PacketPlayOutExplosion extends Packet
{
    private double a;
    private double b;
    private double c;
    private float d;
    private List e;
    public float f;
    public float g;
    public float h;

    public PacketPlayOutExplosion() {
    }

    public PacketPlayOutExplosion(final double a, final double b, final double c, final float d, final List list, final Vec3D vec3D) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.e = new ArrayList(list);
        if (vec3D != null) {
            this.f = (float)vec3D.a;
            this.g = (float)vec3D.b;
            this.h = (float)vec3D.c;
        }
    }

    @Override
    public void a(final PacketDataSerializer packetDataSerializer) {
        this.a = packetDataSerializer.readFloat();
        this.b = packetDataSerializer.readFloat();
        this.c = packetDataSerializer.readFloat();
        this.d = packetDataSerializer.readFloat();
        final int int1 = packetDataSerializer.readInt();
        this.e = new ArrayList(int1);
        final int n = (int)this.a;
        final int n2 = (int)this.b;
        final int n3 = (int)this.c;
        for (int i = 0; i < int1; ++i) {
            this.e.add(new ChunkPosition(packetDataSerializer.readByte() + n, packetDataSerializer.readByte() + n2, packetDataSerializer.readByte() + n3));
        }
        this.f = packetDataSerializer.readFloat();
        this.g = packetDataSerializer.readFloat();
        this.h = packetDataSerializer.readFloat();
    }

    @Override
    public void b(final PacketDataSerializer packetDataSerializer) {
        packetDataSerializer.writeFloat((float)this.a);
        packetDataSerializer.writeFloat((float)this.b);
        packetDataSerializer.writeFloat((float)this.c);
        packetDataSerializer.writeFloat(this.d);
        packetDataSerializer.writeInt(this.e.size());
        final int n = (int)this.a;
        final int n2 = (int)this.b;
        final int n3 = (int)this.c;
        for (final Object object : this.e) {
            ChunkPosition chunkPosition = (ChunkPosition) object;
            final int i = chunkPosition.x - n;
            final int j = chunkPosition.y - n2;
            final int k = chunkPosition.z - n3;
            packetDataSerializer.writeByte(i);
            packetDataSerializer.writeByte(j);
            packetDataSerializer.writeByte(k);
        }
        packetDataSerializer.writeFloat(this.f);
        packetDataSerializer.writeFloat(this.g);
        packetDataSerializer.writeFloat(this.h);
    }

    public void a(final PacketPlayOutListener packetPlayOutListener) {
        packetPlayOutListener.a(this);
    }

    public void handle(PacketListener packetlistener) {
        this.a((PacketPlayOutListener) packetlistener);
    }
}