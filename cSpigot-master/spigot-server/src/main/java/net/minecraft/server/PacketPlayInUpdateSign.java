package net.minecraft.server;

import java.io.IOException;

public class PacketPlayInUpdateSign extends Packet {

    private int a;
    private int b;
    private int c;
    private String[] d;

    public PacketPlayInUpdateSign() {}

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        // Spigot start - protocol patch
        if ( packetdataserializer.version < 16 )
        {
            this.a = packetdataserializer.readInt();
            this.b = packetdataserializer.readShort();
            this.c = packetdataserializer.readInt();
        } else
        {
            long position = packetdataserializer.readLong();
            a = packetdataserializer.readPositionX( position );
            b = packetdataserializer.readPositionY( position );
            c = packetdataserializer.readPositionZ( position );
        }
        // Spigot end
        this.d = new String[4];

        for (int i = 0; i < 4; ++i) {
            // Spigot start - protocol patch
            if ( packetdataserializer.version < 21 )
            {
                this.d[ i ] = packetdataserializer.c( 15 );
            } else
            {
                this.d[ i ] = ChatSerializer.a( packetdataserializer.c( Short.MAX_VALUE ) ).c();
            }
            if (this.d[i].length() > 15) {
                this.d[i] = this.d[i].substring( 0, 15 );
            }
            // Spigot end
        }
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.writeInt(this.a);
        packetdataserializer.writeShort(this.b);
        packetdataserializer.writeInt(this.c);

        for (int i = 0; i < 4; ++i) {
            packetdataserializer.a(this.d[i]);
        }
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

    public String[] f() {
        return this.d;
    }

    public void handle(PacketListener packetlistener) {
        this.a((PacketPlayInListener) packetlistener);
    }
}
