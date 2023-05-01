package net.minecraft.server;

import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.com.mojang.authlib.properties.Property;
import net.minecraft.util.com.mojang.authlib.properties.PropertyMap;
import org.bukkit.craftbukkit.util.CraftChatMessage;

import java.io.IOException;

public class PacketPlayOutPlayerInfo extends Packet {

    private static final int ADD_PLAYER = 0;
    private static final int UPDATE_GAMEMODE = 1;
    private static final int UPDATE_LATENCY = 2;
    private static final int UPDATE_DISPLAY_NAME = 3;
    private static final int REMOVE_PLAYER = 4;

    //CavePvP public
    // private int length; We don't batch (yet)
    public int ping;
    public int action;
    public int gamemode;
    public String username;

    public GameProfile player;

    public PacketPlayOutPlayerInfo() {}

    /* removed to force breaking
    public PacketPlayOutPlayerInfo(String s, boolean flag, int i) {
        this.a = s;
        this.b = flag;
        this.c = i;
    }
    */

    public static PacketPlayOutPlayerInfo addPlayer(EntityPlayer player) {
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo();
        packet.action = ADD_PLAYER;
        packet.username = player.listName;
        packet.player = player.getDisguiseProfile(); // MineHQ - disguises
        packet.ping = player.ping;
        packet.gamemode = player.playerInteractManager.getGameMode().getId();
        return packet;
    }

    public static PacketPlayOutPlayerInfo updatePing(EntityPlayer player) {
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo();
        packet.action = UPDATE_LATENCY;
        packet.username = player.listName;
        packet.player = player.getDisguiseProfile(); // MineHQ - disguises
        packet.ping = player.ping;
        return packet;
    }

    public static PacketPlayOutPlayerInfo updateGamemode(EntityPlayer player) {
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo();
        packet.action = UPDATE_GAMEMODE;
        packet.username = player.listName;
        packet.player = player.getDisguiseProfile(); // MineHQ - disguises
        packet.gamemode = player.playerInteractManager.getGameMode().getId();
        return packet;
    }

    public static PacketPlayOutPlayerInfo updateDisplayName(EntityPlayer player) {
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo();
        packet.action = UPDATE_DISPLAY_NAME;
        packet.username = player.listName;
        packet.player = player.getDisguiseProfile(); // MineHQ - disguises
        return packet;
    }

    public static PacketPlayOutPlayerInfo removePlayer(EntityPlayer player) {
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo();
        packet.action = REMOVE_PLAYER;
        packet.username = player.listName;
        packet.player = player.getDisguiseProfile(); // MineHQ - disguises
        return packet;
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        // Not needed
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        // PaperSpigot start - Fix scoreboard prefix and suffix in tab list
        String username = this.username;
        if (packetdataserializer.version >= 47 && action == ADD_PLAYER && username != null && username.equals(player.getName())) {
            username = null;
        }
        // PaperSpigot end
        if ( packetdataserializer.version >= 20 )
        {
            packetdataserializer.b( action );
            packetdataserializer.b( 1 );
            packetdataserializer.writeUUID( player.getId() );
            switch ( action )
            {
                case ADD_PLAYER:
                    packetdataserializer.a( player.getName() );
                    PropertyMap properties = player.getProperties();
                    packetdataserializer.b( properties.size() );
                    for ( Property property : properties.values() )
                    {
                        packetdataserializer.a( property.getName() );
                        packetdataserializer.a( property.getValue() );
                        packetdataserializer.writeBoolean( property.hasSignature() );
                        if ( property.hasSignature() )
                        {
                            packetdataserializer.a( property.getSignature() );
                        }
                    }
                    packetdataserializer.b( gamemode );
                    packetdataserializer.b( ping );
                    packetdataserializer.writeBoolean( username != null );
                    if ( username != null )
                    {
                        packetdataserializer.a( ChatSerializer.a( CraftChatMessage.fromString( username )[0] ) );
                    }
                    break;
                case UPDATE_GAMEMODE:
                    packetdataserializer.b( gamemode );
                    break;
                case UPDATE_LATENCY:
                    packetdataserializer.b( ping );
                    break;
                case UPDATE_DISPLAY_NAME:
                    packetdataserializer.writeBoolean( username != null );
                    if ( username != null )
                    {
                        packetdataserializer.a( ChatSerializer.a( CraftChatMessage.fromString( username )[0] ) );
                    }
                    break;
                case REMOVE_PLAYER:
                    break;

            }
        } else {
            packetdataserializer.a( username );
            packetdataserializer.writeBoolean( action != REMOVE_PLAYER );
            packetdataserializer.writeShort( ping );
        }
    }

    public void a(PacketPlayOutListener packetplayoutlistener) {
        packetplayoutlistener.a(this);
    }

    public void handle(PacketListener packetlistener) {
        this.a((PacketPlayOutListener) packetlistener);
    }
}
// Spigot end