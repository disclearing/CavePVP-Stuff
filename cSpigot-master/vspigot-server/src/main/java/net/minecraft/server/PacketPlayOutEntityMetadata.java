package net.minecraft.server;

import java.util.Iterator;
import java.util.List;

public class PacketPlayOutEntityMetadata extends Packet {

    private int a;
    private List b;
    private boolean found = false; // MineHQ

    public PacketPlayOutEntityMetadata() {}

    public PacketPlayOutEntityMetadata(int i, DataWatcher datawatcher, boolean flag) {
        this.a = i;
        if (flag) {
            this.b = datawatcher.c();
        } else {
            this.b = datawatcher.b();
        }
    }

    // Kohi start
    // Constructor accepting List of change metadata
    public PacketPlayOutEntityMetadata(int i, List list, boolean flag) {
        this.a = i;
        this.b = list;
    }

    // replaces health with 1.0F
    public PacketPlayOutEntityMetadata obfuscateHealth() {
        Iterator iter = b.iterator();
        found = false;

        while (iter.hasNext()) {
            WatchableObject watchable = (WatchableObject) iter.next();
            if (watchable.a() == 6) {
                iter.remove();
                found = true;
            }
        }

        if (found) {
            b.add(new WatchableObject(3, 6, Float.valueOf(1.0F)));
        }

        return this;
    }
    // Kohi end

    // MineHQ start
    public boolean didFindHealth() {
        return this.found;
    }

    public List getMetadata() {
        return this.b;
    }
    // MineHQ end

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
