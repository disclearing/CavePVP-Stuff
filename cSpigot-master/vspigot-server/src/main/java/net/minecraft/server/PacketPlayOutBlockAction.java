package net.minecraft.server;

public class PacketPlayOutBlockAction extends Packet {

    private int a;
    private int b;
    private int c;
    private int d;
    private int e;
    private Block f;

    public PacketPlayOutBlockAction() {}

    public PacketPlayOutBlockAction(int i, int j, int k, Block block, int l, int i1) {
        this.a = i;
        this.b = j;
        this.c = k;
        this.d = l;
        this.e = i1;
        this.f = block;
    }

    public void a(PacketDataSerializer packetdataserializer) {
        this.a = packetdataserializer.readInt();
        this.b = packetdataserializer.readShort();
        this.c = packetdataserializer.readInt();
        this.d = packetdataserializer.readUnsignedByte();
        this.e = packetdataserializer.readUnsignedByte();
        this.f = Block.getById(packetdataserializer.a() & 4095);
    }

    public void b(PacketDataSerializer packetdataserializer) {
        // Spigot start
        if ( packetdataserializer.version < 16 )
        {
            packetdataserializer.writeInt( this.a );
            packetdataserializer.writeShort( this.b );
            packetdataserializer.writeInt( this.c );
        } else
        {
            packetdataserializer.writePosition( a, b, c );
        }
        // Spigot end
        packetdataserializer.writeByte(this.d);
        packetdataserializer.writeByte(this.e);
        packetdataserializer.b(Block.getId(this.f) & 4095);
    }

    public void a(PacketPlayOutListener packetplayoutlistener) {
        packetplayoutlistener.a(this);
    }

    public void handle(PacketListener packetlistener) {
        this.a((PacketPlayOutListener) packetlistener);
    }
}
