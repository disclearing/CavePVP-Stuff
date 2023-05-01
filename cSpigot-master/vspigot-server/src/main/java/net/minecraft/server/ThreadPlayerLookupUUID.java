package net.minecraft.server;

import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import org.bukkit.craftbukkit.util.Waitable;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerPreLoginEvent;

import java.math.BigInteger;
import java.security.PrivateKey;
import java.util.UUID;
// Poweruser end

class ThreadPlayerLookupUUID implements Runnable { // Poweruser

    final LoginListener a;

    // Poweruser start
    final PacketLoginInEncryptionBegin packetlogininencryptionbegin;

    ThreadPlayerLookupUUID(LoginListener loginlistener, PacketLoginInEncryptionBegin packetlogininencryptionbegin) { // Poweruser
        this.a = loginlistener;
        this.packetlogininencryptionbegin = packetlogininencryptionbegin;
    }

    public ThreadPlayerLookupUUID(LoginListener loginlistener) {
        this(loginlistener, null);
    }
    // Poweruser end

    public void run() {
        // Poweruser start
        if (this.packetlogininencryptionbegin != null) {
            try {
                PrivateKey privatekey = MinecraftServer.getServer().K().getPrivate();
                if (this.a.compareRandomConnectionKey(this.packetlogininencryptionbegin.b(privatekey))) {
                    this.a.setLoginKey(packetlogininencryptionbegin.a(privatekey));
                    LoginListener.a(this.a, EnumProtocolState.AUTHENTICATING);
                    this.a.networkManager.a(LoginListener.d(this.a));
                } else {
                    throw new IllegalStateException("Invalid nonce!");
                }
            } catch (Exception e) {
                this.a.caughtAuthenticationException(e);
                return;
            }
        }
        // Poweruser end

        GameProfile gameprofile = LoginListener.a(this.a);

        try {
            // Spigot Start
            if ( !LoginListener.c( this.a ).getOnlineMode() )
            {
                a.initUUID();
                fireLoginEvents();
                return;
            }
            // Spigot End
            String s = (new BigInteger(MinecraftEncryption.a(LoginListener.b(this.a), LoginListener.c(this.a).K().getPublic(), LoginListener.d(this.a)))).toString(16);

            LoginListener.a(this.a, LoginListener.c(this.a).av().hasJoinedServer(new GameProfile((UUID) null, gameprofile.getName()), s));
            if (LoginListener.a(this.a) != null) {
                fireLoginEvents(); // Spigot
            } else if (LoginListener.c(this.a).N()) {
                LoginListener.e().warn("Failed to verify username but will let them in anyway!");
                LoginListener.a(this.a, this.a.a(gameprofile));
                LoginListener.a(this.a, EnumProtocolState.READY_TO_ACCEPT);
            } else {
                this.a.disconnect("Failed to verify username!");
                LoginListener.e().error("Username \'" + LoginListener.a(this.a).getName() + "\' tried to join with an invalid session");
            }
        } catch (AuthenticationUnavailableException authenticationunavailableexception) {
            if (LoginListener.c(this.a).N()) {
                LoginListener.e().warn("Authentication servers are down but will let them in anyway!");
                LoginListener.a(this.a, this.a.a(gameprofile));
                LoginListener.a(this.a, EnumProtocolState.READY_TO_ACCEPT);
            } else {
                this.a.disconnect("Authentication servers are down. Please try again later, sorry!");
                LoginListener.e().error("Couldn\'t verify username because servers are unavailable");
            }
            // CraftBukkit start - catch all exceptions
        } catch (Exception exception) {
            this.a.disconnect("Failed to verify username!");
            LoginListener.c(this.a).server.getLogger().log(java.util.logging.Level.WARNING, "Exception verifying " + LoginListener.a(this.a).getName(), exception);
            // CraftBukkit end
        }
    }

    private void fireLoginEvents() throws Exception
    {
        // CraftBukkit start - fire PlayerPreLoginEvent
        if (!this.a.networkManager.isConnected()) {
            return;
        }

        String playerName = LoginListener.a(this.a).getName();
        java.net.InetAddress address = ((java.net.InetSocketAddress) a.networkManager.getSocketAddress()).getAddress();
        java.util.UUID uniqueId = LoginListener.a(this.a).getId();
        final org.bukkit.craftbukkit.CraftServer server = LoginListener.c(this.a).server;

        AsyncPlayerPreLoginEvent asyncEvent = new AsyncPlayerPreLoginEvent(playerName, address, uniqueId);
        server.getPluginManager().callEvent(asyncEvent);

        if (PlayerPreLoginEvent.getHandlerList().getRegisteredListeners().length != 0) {
            final PlayerPreLoginEvent event = new PlayerPreLoginEvent(playerName, address, uniqueId);
            if (asyncEvent.getResult() != PlayerPreLoginEvent.Result.ALLOWED) {
                event.disallow(asyncEvent.getResult(), asyncEvent.getKickMessage());
            }
            Waitable<PlayerPreLoginEvent.Result> waitable = new Waitable<PlayerPreLoginEvent.Result>() {
                @Override
                protected PlayerPreLoginEvent.Result evaluate() {
                    server.getPluginManager().callEvent(event);
                    return event.getResult();
                }};

            LoginListener.c(this.a).processQueue.add(waitable);
            if (waitable.get() != PlayerPreLoginEvent.Result.ALLOWED) {
                this.a.disconnect(event.getKickMessage());
                return;
            }
        } else {
            if (asyncEvent.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
                this.a.disconnect(asyncEvent.getKickMessage());
                return;
            }
        }
        // CraftBukkit end

        //LoginListener.e().info("UUID of player " + LoginListener.a(this.a).getName() + " is " + LoginListener.a(this.a).getId());
        LoginListener.a(this.a, EnumProtocolState.READY_TO_ACCEPT);
    }
}
