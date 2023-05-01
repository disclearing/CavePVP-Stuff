package net.minecraft.server;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;

import java.io.IOException;

public class PacketPlayOutChat extends Packet {

    private IChatBaseComponent a;
    public BaseComponent[] components; // Spigot - Update 20140909b
    private boolean b;
    private int pos; // Spigot - Update 20141001a

    public PacketPlayOutChat() {
        this.b = true;
    }

    public PacketPlayOutChat(IChatBaseComponent ichatbasecomponent) {
        this(ichatbasecomponent, true);
    }

    public PacketPlayOutChat(IChatBaseComponent ichatbasecomponent, int pos) {
        this(ichatbasecomponent, pos, true);
    }

    public PacketPlayOutChat(IChatBaseComponent ichatbasecomponent, boolean flag) {
        this(ichatbasecomponent, 0, flag);
    }

    public PacketPlayOutChat(IChatBaseComponent ichatbasecomponent, int pos, boolean flag) {
        this.b = true;
        this.a = ichatbasecomponent;
        this.b = flag;
        this.pos = pos;
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = ChatSerializer.a(packetdataserializer.c(32767));
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        // Spigot start - Update 20140909b
        if (components != null)
        {
            packetdataserializer.a( ComponentSerializer.toString(components) );
        }
        else {
            packetdataserializer.a( ChatSerializer.a(a) );
        }
        // Spigot end

        // Spigot start - protocol patch
        if ( packetdataserializer.version >= 16 )
        {
            packetdataserializer.writeByte( this.pos );
        }
        // Spigot end
    }

    public void a(PacketPlayOutListener packetplayoutlistener) {
        packetplayoutlistener.a(this);
    }

    public String b() {
        return String.format("message=\'%s\'", new Object[] { this.a});
    }

    public boolean d() {
        return this.b;
    }

    public void handle(PacketListener packetlistener) {
        this.a((PacketPlayOutListener) packetlistener);
    }
}
