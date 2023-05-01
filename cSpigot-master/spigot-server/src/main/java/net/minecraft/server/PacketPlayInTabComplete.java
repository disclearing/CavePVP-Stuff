package net.minecraft.server;

import net.minecraft.util.org.apache.commons.lang3.StringUtils;

import java.io.IOException;

public class PacketPlayInTabComplete extends Packet {

    private String a;

    public PacketPlayInTabComplete() {}

    public PacketPlayInTabComplete(String s) {
        this.a = s;
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.c(32767);
        // Spigot start - protocol patch
        if ( packetdataserializer.version >= 37 )
        {
            if (packetdataserializer.readBoolean()) {
                long position = packetdataserializer.readLong();
            }
        }
        // Spigot end
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.a(StringUtils.substring(this.a, 0, 32767));
    }

    public void a(PacketPlayInListener packetplayinlistener) {
        packetplayinlistener.a(this);
    }

    public String c() {
        return this.a;
    }

    public String b() {
        return String.format("message=\'%s\'", new Object[] { this.a});
    }

    public void handle(PacketListener packetlistener) {
        this.a((PacketPlayInListener) packetlistener);
    }
}
