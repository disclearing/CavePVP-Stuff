package net.minecraft.server;

public class PacketPlayInEntityAction extends Packet {

    private int a;
    private int animation;
    private int c;

    public PacketPlayInEntityAction() {}

    public void a(PacketDataSerializer packetdataserializer) {
        // Spigot start - protocol patch
        if ( packetdataserializer.version < 16 )
        {
            this.a = packetdataserializer.readInt();
            this.animation = packetdataserializer.readByte();
            this.c = packetdataserializer.readInt();
        } else
        {
            a = packetdataserializer.a();
            animation = packetdataserializer.readUnsignedByte() + 1;
            c = packetdataserializer.a();
        }
        // Spigot end
    }

    public void b(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeInt(this.a);
        packetdataserializer.writeByte(this.animation);
        packetdataserializer.writeInt(this.c);
    }

    public void a(PacketPlayInListener packetplayinlistener) {
        packetplayinlistener.a(this);
    }

    public int d() {
        return this.animation;
    }

    public int e() {
        return this.c;
    }

    public void handle(PacketListener packetlistener) {
        this.a((PacketPlayInListener) packetlistener);
    }
}
