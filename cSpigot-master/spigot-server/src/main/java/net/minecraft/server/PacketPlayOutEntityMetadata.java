package net.minecraft.server;

import java.util.List;

public class PacketPlayOutEntityMetadata extends Packet {

    private int a;
    private List b;

    public PacketPlayOutEntityMetadata() {}

    public PacketPlayOutEntityMetadata(int i, DataWatcher datawatcher, boolean flag) {
        this.a = i;
        if (flag) {
            this.b = datawatcher.c();
        } else {
            this.b = datawatcher.b();
        }
    }

    public void a(PacketDataSerializer packetdataserializer) {
        this.a = packetdataserializer.readInt();
        this.b = DataWatcher.b(packetdataserializer);
    }

    public void b(PacketDataSerializer packetdataserializer) {
        // Spigot start - protocol patch
        if ( packetdataserializer.version < 16 )
        {
            packetdataserializer.writeInt( this.a );
        } else
        {
            packetdataserializer.b( a );
        }
        DataWatcher.a(this.b, packetdataserializer, packetdataserializer.version);
        // Spigot end
    }

    public void a(PacketPlayOutListener packetplayoutlistener) {
        packetplayoutlistener.a(this);
    }

    public void handle(PacketListener packetlistener) {
        this.a((PacketPlayOutListener) packetlistener);
    }
}
