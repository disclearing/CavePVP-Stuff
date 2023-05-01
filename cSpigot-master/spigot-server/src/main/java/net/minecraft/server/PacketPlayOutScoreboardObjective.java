package net.minecraft.server;

import java.io.IOException;

public class PacketPlayOutScoreboardObjective extends Packet {

    private String a;
    private String b;
    private int c;

    public PacketPlayOutScoreboardObjective() {}

    public PacketPlayOutScoreboardObjective(ScoreboardObjective scoreboardobjective, int i) {
        this.a = scoreboardobjective.getName();
        this.b = scoreboardobjective.getDisplayName();
        this.c = i;
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.c(16);
        this.b = packetdataserializer.c(32);
        this.c = packetdataserializer.readByte();
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        // Spigot start - protocol patch
        if ( packetdataserializer.version < 16 )
        {
            packetdataserializer.a( this.a );
            packetdataserializer.a( this.b );
            packetdataserializer.writeByte( this.c );
        } else
        {
            packetdataserializer.a( a );
            packetdataserializer.writeByte( c );
            if ( c == 0 || c == 2 ) {
                packetdataserializer.a( b );
                packetdataserializer.a( "integer" );
            }
        }
        // Spigot end
    }

    public void a(PacketPlayOutListener packetplayoutlistener) {
        packetplayoutlistener.a(this);
    }

    public void handle(PacketListener packetlistener) {
        this.a((PacketPlayOutListener) packetlistener);
    }
}
