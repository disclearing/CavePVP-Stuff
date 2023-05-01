package net.minecraft.server;

import java.io.IOException;

public class PacketPlayOutScoreboardScore extends Packet {

    private String a = "";
    private String b = "";
    private int c;
    private int d;

    public PacketPlayOutScoreboardScore() {}

    public PacketPlayOutScoreboardScore(ScoreboardScore scoreboardscore, int i) {
        this.a = scoreboardscore.getPlayerName();
        this.b = scoreboardscore.getObjective().getName();
        this.c = scoreboardscore.getScore();
        this.d = i;
    }

    public PacketPlayOutScoreboardScore(String s) {
        this.a = s;
        this.b = "";
        this.c = 0;
        this.d = 1;
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.c(16);
        this.d = packetdataserializer.readByte();
        if (this.d != 1) {
            this.b = packetdataserializer.c(16);
            this.c = packetdataserializer.readInt();
        }
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.a( this.a );
        packetdataserializer.writeByte( this.d );
        // Spigot start - protocol patch
        if ( packetdataserializer.version < 16 )
        {
            if ( this.d != 1 )
            {
                packetdataserializer.a( this.b );
                packetdataserializer.writeInt( this.c );
            }
        } else
        {
            packetdataserializer.a( this.b );
            if ( this.d != 1 )
            {
                packetdataserializer.b( c );
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
