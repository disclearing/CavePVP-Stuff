package net.minecraft.server;

import net.minecraft.util.io.netty.buffer.ByteBuf;

import java.io.IOException;

public class PacketPlayOutCustomPayload extends Packet {

    private String tag;
    private byte[] data;

    public PacketPlayOutCustomPayload() {}

    public PacketPlayOutCustomPayload(String s, ByteBuf bytebuf) {
        this(s, bytebuf.array());
    }

    public PacketPlayOutCustomPayload(String s, byte[] abyte) {
        this.tag = s;
        this.data = abyte;
        if (abyte.length >= 1048576) {
            throw new IllegalArgumentException("Payload may not be larger than 1048576 bytes");
        }
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.tag = packetdataserializer.c(20);
        this.data = new byte[packetdataserializer.readUnsignedShort()];
        packetdataserializer.readBytes(this.data);
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.a(this.tag);
        // Spigot start - protocol patch
        if ( packetdataserializer.version < 29 )
        {
            packetdataserializer.writeShort( this.data.length );
        }
        if ( packetdataserializer.version >= 47 && tag.equals( "MC|Brand" ) )
        {
            packetdataserializer.a( new String( data, "UTF-8" ) );
            return;
        }
        packetdataserializer.writeBytes(this.data);
        if ( packetdataserializer.version >= 29 && tag.equals( "MC|AdvCdm" ) )
        {
            packetdataserializer.writeBoolean( true );
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
