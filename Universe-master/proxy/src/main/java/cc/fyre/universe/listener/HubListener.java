package cc.fyre.universe.listener;

import cc.fyre.universe.Universe;

import cc.fyre.universe.UniverseAPI;
import cc.fyre.universe.server.Server;
import cc.fyre.universe.server.fetch.ServerGroup;
import com.imaginarycode.minecraft.redisbungee.RedisBungee;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author xanderume@gmail (JavaProject)
 */
public class HubListener implements Listener {

    @EventHandler
    public void onPing(ProxyPingEvent event) {
        final ServerPing proxy = event.getResponse();
        final ServerPing.Players players = proxy.getPlayers();

        int playerCount = 0;

        for (Server server : Universe.getInstance().getUniverseHandler().getServers()) {
            playerCount += server.getOnlinePlayers().get();
        }

        proxy.setPlayers(new ServerPing.Players(players.getMax(), playerCount, players.getSample()));

        event.setResponse(proxy);
    }

    @EventHandler
    public void onPostLogin(ServerConnectEvent event) {

        if (event.getPlayer().getServer() != null) {
            return;
        }

        final List<Server> availableHubs = Universe.getInstance().getUniverseHandler().getAvailableServers(event.getPlayer().getUniqueId(),ServerGroup.HUB);

        if (availableHubs.isEmpty()) {
            event.getPlayer().disconnect(new TextComponent(ChatColor.RED + "Unavailable to find a suitable hub, please try again later! Exit code: 1"));
            event.setCancelled(true);
            return;
        }

        ServerInfo serverInfo = Universe.getInstance().getProxy().getServerInfo(availableHubs.get(ThreadLocalRandom.current().nextInt(availableHubs.size())).getName());

        if (UniverseAPI.getProxyName().equalsIgnoreCase("NA-01")) {
            serverInfo = Universe.getInstance().getProxy().getServerInfo("Hub-01");
        }

        if (UniverseAPI.getProxyName().equalsIgnoreCase("Crypto")) {
            serverInfo = Universe.getInstance().getProxy().getServerInfo("Crypto-Hub");
        }

        if (serverInfo == null) {
            event.getPlayer().disconnect(new TextComponent(ChatColor.RED + "Unavailable to find a suitable hub, please try again later! Exit code: 2"));
            event.setCancelled(true);
            return;
        }

        System.out.println("Connecting " + event.getPlayer().getName() + " to " + serverInfo.getName() + " on port " + serverInfo.getAddress().getPort() + " with version " + event.getPlayer().getPendingConnection().getVersion());
        event.getPlayer().sendMessage(new TextComponent(ChatColor.RED + "Connecting you to " + ChatColor.WHITE + serverInfo.getName() + ChatColor.RED + "..."));
        event.setTarget(serverInfo);
    }

    @EventHandler
    public void onKick(ServerKickEvent event) {

//        if (event.getKickReason().contains("Your account is banned")) {
//            final ServerInfo serverInfo = Universe.getInstance().getProxy().getServerInfo("Ban-Lobby");
//            final Server bannedServer = UniverseAPI.serverFromName("Ban-Lobby");
//
//            if (serverInfo == null || bannedServer == null || !bannedServer.canJoin(event.getPlayer().getUniqueId())) {
//                return;
//            }
//
//            event.getPlayer().sendMessage(new TextComponent(ChatColor.RED + "Sending you to " + ChatColor.WHITE + serverInfo.getName() + ChatColor.RED + "..."));
//            event.setCancelled(true);
//            event.setCancelServer(serverInfo);
//            return;
//        }

        if (Arrays.stream(event.getKickReasonComponent()).map(it -> ChatColor.stripColor(it.toPlainText())).noneMatch(it -> it.equalsIgnoreCase("Server closed") || it.contains("Server closed"))) {
            return;
        }

        final List<Server> availableHubs = Universe.getInstance().getUniverseHandler().getAvailableServers(event.getPlayer().getUniqueId(),ServerGroup.HUB);

        if (availableHubs.isEmpty()) {
            event.getPlayer().disconnect(new TextComponent(ChatColor.RED + "Unavailable to find a suitable hub, please try again later!"));
            event.setCancelled(true);
            return;
        }

        ServerInfo serverInfo = Universe.getInstance().getProxy().getServerInfo(availableHubs.get(ThreadLocalRandom.current().nextInt(availableHubs.size())).getName());

        if (UniverseAPI.getProxyName().equalsIgnoreCase("Crypto")) {
            serverInfo = Universe.getInstance().getProxy().getServerInfo("Crypto-Hub");
        }
        if (UniverseAPI.getProxyName().equalsIgnoreCase("NA-01")) {
            serverInfo = Universe.getInstance().getProxy().getServerInfo("Hub-01");
        }

        if (serverInfo == null) {
            event.getPlayer().disconnect(new TextComponent(ChatColor.RED + "Unavailable to find a suitable hub, please try again later!"));
            event.setCancelled(true);
            return;
        }

        event.getPlayer().sendMessage(new TextComponent(ChatColor.RED + "Sending you to " + ChatColor.WHITE + serverInfo.getName() + ChatColor.RED + "..."));
        event.setCancelled(true);
        event.setCancelServer(serverInfo);
    }

}
