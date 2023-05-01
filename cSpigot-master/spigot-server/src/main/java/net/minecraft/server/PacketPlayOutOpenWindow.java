package net.minecraft.server;

import org.bukkit.craftbukkit.util.CraftChatMessage;

import java.io.IOException;

public class PacketPlayOutOpenWindow extends Packet {

    private int a;
    private int b;
    private String c;
    private int d;
    private boolean e;
    private int f;

    public PacketPlayOutOpenWindow() {}

    public PacketPlayOutOpenWindow(int i, int j, String s, int k, boolean flag) {
        if (s.length() > 32) s = s.substring( 0, 32 ); // Spigot - Cap window name to prevent client disconnects
        this.a = i;
        this.b = j;
        this.c = s;
        this.d = k;
        this.e = flag;
    }

    public PacketPlayOutOpenWindow(int i, int j, String s, int k, boolean flag, int l) {
        this(i, j, s, k, flag);
        this.f = l;
    }

    public void a(PacketPlayOutListener packetplayoutlistener) {
        packetplayoutlistener.a(this);
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.readUnsignedByte();
        this.b = packetdataserializer.readUnsignedByte();
        this.c = packetdataserializer.c(32);
        this.d = packetdataserializer.readUnsignedByte();
        this.e = packetdataserializer.readBoolean();
        if (this.b == 11) {
            this.f = packetdataserializer.readInt();
        }
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        if ( packetdataserializer.version < 16 )
        {
            packetdataserializer.writeByte( this.a );
            packetdataserializer.writeByte( this.b );
            packetdataserializer.a( this.c );
            packetdataserializer.writeByte( this.d );
            packetdataserializer.writeBoolean( this.e );
            if ( this.b == 11 )
            {
                packetdataserializer.writeInt( this.f );
            }
        } else
        {
            packetdataserializer.writeByte( a );
            packetdataserializer.a( getInventoryString( b ) );
            if ( e )
            {
                packetdataserializer.a( ChatSerializer.a( CraftChatMessage.fromString( c )[ 0 ] ) );
            } else
            {
                packetdataserializer.a( ChatSerializer.a( new ChatMessage( c ) ) );
            }
            packetdataserializer.writeByte( d );
            if ( this.b == 11 )
            {
                packetdataserializer.writeInt( this.f );
            }
        }
    }

    // Spigot start - protocol patch
    private String getInventoryString(int b)
    {
        switch ( b ) {
            case 0:
                return "minecraft:chest";
            case 1:
                return "minecraft:crafting_table";
            case 2:
                return "minecraft:furnace";
            case 3:
                return "minecraft:dispenser";
            case 4:
                return "minecraft:enchanting_table";
            case 5:
                return "minecraft:brewing_stand";
            case 6:
                return "minecraft:villager";
            case 7:
                return "minecraft:beacon";
            case 8:
                return "minecraft:anvil";
            case 9:
                return "minecraft:hopper";
            case 10:
                return "minecraft:dropper";
            case 11:
                return "EntityHorse";
        }
        throw new IllegalArgumentException( "Unknown type " + b );
    }
    // Spigot end

    public void handle(PacketListener packetlistener) {
        this.a((PacketPlayOutListener) packetlistener);
    }
}
