package net.minecraft.server;

public class PacketPlayOutRemoveEntityEffect extends Packet {

    private int a;
    private int b;

    public PacketPlayOutRemoveEntityEffect() {}

    public PacketPlayOutRemoveEntityEffect(int i, MobEffect mobeffect) {
        this.a = i;
        this.b = mobeffect.getEffectId();
    }

    public void a(PacketDataSerializer packetdataserializer) {
        this.a = packetdataserializer.readInt();
        this.b = packetdataserializer.readUnsignedByte();
    }

    public void b(PacketDataSerializer packetdataserializer) {
        // Spigot start - protocol patch
        if ( packetdataserializer.version < 16 )
        {
            packetdataserializer.writeInt( this.a );
        } else {
            packetdataserializer.b( a );
        }
        // Spigot end
        packetdataserializer.writeByte(this.b);
    }

    public void a(PacketPlayOutListener packetplayoutlistener) {
        packetplayoutlistener.a(this);
    }

    public void handle(PacketListener packetlistener) {
        this.a((PacketPlayOutListener) packetlistener);
    }
}
