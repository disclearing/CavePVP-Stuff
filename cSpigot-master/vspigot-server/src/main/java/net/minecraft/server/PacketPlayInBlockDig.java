package net.minecraft.server;

public class PacketPlayInBlockDig extends Packet {

    private int a;
    private int b;
    private int c;
    private int face;
    private int e;

    public PacketPlayInBlockDig() {}

    public void a(PacketDataSerializer packetdataserializer) {
        this.e = packetdataserializer.readUnsignedByte();
        // Spigot start
        if ( packetdataserializer.version < 16)
        {
            this.a = packetdataserializer.readInt();
            this.b = packetdataserializer.readUnsignedByte();
            this.c = packetdataserializer.readInt();
        } else
        {
            long position = packetdataserializer.readLong();
            a = packetdataserializer.readPositionX( position );
            b = packetdataserializer.readPositionY( position );
            c = packetdataserializer.readPositionZ( position );
        }
        // Spigot end
        this.face = packetdataserializer.readUnsignedByte();
    }

    public void b(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeByte(this.e);
        packetdataserializer.writeInt(this.a);
        packetdataserializer.writeByte(this.b);
        packetdataserializer.writeInt(this.c);
        packetdataserializer.writeByte(this.face);
    }

    public void a(PacketPlayInListener packetplayinlistener) {
        packetplayinlistener.a(this);
    }

    public int c() {
        return this.a;
    }

    public int d() {
        return this.b;
    }

    public int e() {
        return this.c;
    }

    public int f() {
        return this.face;
    }

    public int g() {
        return this.e;
    }

    public void handle(PacketListener packetlistener) {
        this.a((PacketPlayInListener) packetlistener);
    }
}
