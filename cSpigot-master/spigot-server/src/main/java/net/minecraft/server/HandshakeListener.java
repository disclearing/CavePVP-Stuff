package net.minecraft.server;

import net.minecraft.util.com.mojang.authlib.properties.Property; // Spigot
import net.minecraft.util.io.netty.util.concurrent.GenericFutureListener;

// CraftBukkit start
import java.net.InetAddress;
import java.util.HashMap;
import net.minecraft.util.com.mojang.util.UUIDTypeAdapter;
// CraftBukkit end

public class HandshakeListener implements PacketHandshakingInListener {

    private static final com.google.gson.Gson gson = new com.google.gson.Gson(); // Spigot
    // CraftBukkit start - add fields
    private static final HashMap<InetAddress, Long> throttleTracker = new HashMap<InetAddress, Long>();
    private static int throttleCounter = 0;
    // CraftBukkit end

    private final MinecraftServer a;
    private final NetworkManager b;

    public HandshakeListener(MinecraftServer minecraftserver, NetworkManager networkmanager) {
        this.a = minecraftserver;
        this.b = networkmanager;
    }

    public void a(PacketHandshakingInSetProtocol packethandshakinginsetprotocol) {
        // Spigot start
        if ( NetworkManager.SUPPORTED_VERSIONS.contains( packethandshakinginsetprotocol.d() ) )
        {
            NetworkManager.a( this.b ).attr( NetworkManager.protocolVersion ).set( packethandshakinginsetprotocol.d() );
        }
        // Spigot end
        switch (ProtocolOrdinalWrapper.a[packethandshakinginsetprotocol.c().ordinal()]) {
        case 1:
            this.b.a(EnumProtocol.LOGIN);
            ChatComponentText chatcomponenttext;

            // CraftBukkit start - Connection throttle
            try {
                long currentTime = System.currentTimeMillis();
                long connectionThrottle = MinecraftServer.getServer().server.getConnectionThrottle();
                InetAddress address = ((java.net.InetSocketAddress) this.b.getSocketAddress()).getAddress();

                synchronized (throttleTracker) {
                    if (throttleTracker.containsKey(address) && !"127.0.0.1".equals(address.getHostAddress()) && currentTime - throttleTracker.get(address) < connectionThrottle) {
                        throttleTracker.put(address, currentTime);
                        chatcomponenttext = new ChatComponentText("Connection throttled! Please wait before reconnecting.");
                        this.b.handle(new PacketLoginOutDisconnect(chatcomponenttext), new GenericFutureListener[0]);
                        this.b.close(chatcomponenttext);
                        return;
                    }

                    throttleTracker.put(address, currentTime);
                    throttleCounter++;
                    if (throttleCounter > 200) {
                        throttleCounter = 0;

                        // Cleanup stale entries
                        java.util.Iterator iter = throttleTracker.entrySet().iterator();
                        while (iter.hasNext()) {
                            java.util.Map.Entry<InetAddress, Long> entry = (java.util.Map.Entry) iter.next();
                            if (entry.getValue() > connectionThrottle) {
                                iter.remove();
                            }
                        }
                    }
                }
            } catch (Throwable t) {
                org.apache.logging.log4j.LogManager.getLogger().debug("Failed to check connection throttle", t);
            }
            // CraftBukkit end

            if (packethandshakinginsetprotocol.d() > 5 && packethandshakinginsetprotocol.d() != 47) { // Spigot
                chatcomponenttext = new ChatComponentText( java.text.MessageFormat.format( org.spigotmc.SpigotConfig.outdatedServerMessage, "1.7.10" ) ); // Spigot
                this.b.handle(new PacketLoginOutDisconnect(chatcomponenttext), new GenericFutureListener[0]);
                this.b.close(chatcomponenttext);
            } else if (packethandshakinginsetprotocol.d() < 4) {
                chatcomponenttext = new ChatComponentText( java.text.MessageFormat.format( org.spigotmc.SpigotConfig.outdatedClientMessage, "1.7.10" ) ); // Spigot
                this.b.handle(new PacketLoginOutDisconnect(chatcomponenttext), new GenericFutureListener[0]);
                this.b.close(chatcomponenttext);
            } else {
                this.b.a((PacketListener) (new LoginListener(this.a, this.b)));
                // Spigot Start
                if (org.spigotmc.SpigotConfig.bungee) {
                    String[] split = packethandshakinginsetprotocol.b.split("\00");
                    if ( split.length == 3 || split.length == 4 ) {
                        packethandshakinginsetprotocol.b = split[0];
                        b.n = new java.net.InetSocketAddress(split[1], ((java.net.InetSocketAddress) b.getSocketAddress()).getPort());
                        b.spoofedUUID = UUIDTypeAdapter.fromString( split[2] );
                    } else
                    {
                        chatcomponenttext = new ChatComponentText("If you wish to use IP forwarding, please enable it in your BungeeCord config as well!");
                        this.b.handle(new PacketLoginOutDisconnect(chatcomponenttext), new GenericFutureListener[0]);
                        this.b.close(chatcomponenttext);
                        return;
                    }
                    if ( split.length == 4 )
                    {
                        b.spoofedProfile = gson.fromJson(split[3], Property[].class);
                    }
                }
                // Spigot End
                ((LoginListener) this.b.getPacketListener()).hostname = packethandshakinginsetprotocol.b + ":" + packethandshakinginsetprotocol.c; // CraftBukkit - set hostname
            }
            break;

        case 2:
            this.b.a(EnumProtocol.STATUS);
            this.b.a((PacketListener) (new PacketStatusListener(this.a, this.b)));
            break;

        default:
            throw new UnsupportedOperationException("Invalid intention " + packethandshakinginsetprotocol.c());
        }
    }

    public void a(IChatBaseComponent ichatbasecomponent) {}

    public void a(EnumProtocol enumprotocol, EnumProtocol enumprotocol1) {
        if (enumprotocol1 != EnumProtocol.LOGIN && enumprotocol1 != EnumProtocol.STATUS) {
            throw new UnsupportedOperationException("Invalid state " + enumprotocol1);
        }
    }

    public void a() {}
}
