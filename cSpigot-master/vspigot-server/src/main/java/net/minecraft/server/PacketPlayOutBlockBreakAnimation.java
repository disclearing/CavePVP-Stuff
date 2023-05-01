package net.minecraft.server;

public class PacketPlayOutBlockBreakAnimation extends Packet {

    private int a;
    private int b;
    private int c;
    private int d;
    private int e;

    public PacketPlayOutBlockBreakAnimation() {}

    public PacketPlayOutBlockBreakAnimation(int i, int j, int k, int l, int i1) {
        this.a = i;
        this.b = j;
        this.c = k;
        this.d = l;
        this.e = i1;
    }

    public void a(PacketDataSerializer packetdataserializer) {
        this.a = packetdataserializer.a();
        this.b = packetdataserializer.readInt();
        this.c = packetdataserializer.readInt();
        this.d = packetdataserializer.readInt();
        this.e = packetdataserializer.readUnsignedByte();
    }

    public void b(PacketDataSerializer packetdataserializer) {
        packetdataserializer.b(this.a);
        // Spigot start - protocol patch
        if ( packetdataserializer.version < 16 )
        {
            packetdataserializer.writeInt( this.b );
            packetdataserializer.writeInt( this.c );
            packetdataserializer.writeInt( this.d );
        } else
        {
            packetdataserializer.writePosition( b, c, d );
        }
        // Spigot end
        packetdataserializer.writeByte(this.e);
    }

    public void a(PacketPlayOutListener packetplayoutlistener) {
        packetplayoutlistener.a(this);
    }

    public void handle(PacketListener packetlistener) {
        this.a((PacketPlayOutListener) packetlistener);
    }
}
