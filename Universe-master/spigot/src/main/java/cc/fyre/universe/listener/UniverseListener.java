package cc.fyre.universe.listener;


import cc.fyre.universe.Universe;
import cc.fyre.universe.packet.ProxyUpdatePacket;
import cc.fyre.universe.packet.ServerUpdatePacket;

import cc.fyre.universe.packet.maintenance.MaintenanceKickPacket;
import cc.fyre.universe.packet.maintenance.MaintenanceListPacket;
import cc.fyre.universe.packet.maintenance.MaintenanceModePacket;
import cc.fyre.universe.pidgin.packet.handler.IncomingPacketHandler;
import cc.fyre.universe.pidgin.packet.listener.PacketListener;
import cc.fyre.universe.proxy.Proxy;

import cc.fyre.universe.server.Server;

import java.util.UUID;

/**
 * @author xanderume@gmail (JavaProject)
 */
public class UniverseListener implements PacketListener {

    @IncomingPacketHandler
    public void onProxyUpdate(ProxyUpdatePacket packet) {

        Proxy proxy = Universe.getInstance().getUniverseHandler().proxyFromName(packet.getJsonObject().get("name").getAsString());

        if (proxy == null) {
            proxy = new Proxy(packet.getJsonObject().get("name").getAsString(),packet.getJsonObject().get("port").getAsInt());
            Universe.getInstance().getUniverseHandler().getProxies().add(proxy);
        }

        proxy.refresh(packet.getJsonObject());

//        Universe.getInstance().getServer().broadcastMessage(ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + "Universe" + ChatColor.DARK_GRAY + "][" + ChatColor.YELLOW + "Proxy" + ChatColor.DARK_GRAY + "] " + ChatColor.WHITE + proxy.getName() + " -> updated.");
    }

    @IncomingPacketHandler
    public void onServerUpdate(ServerUpdatePacket packet) {

        Server server = Universe.getInstance().getUniverseHandler().serverFromName(packet.getJsonObject().get("name").getAsString());

        if (server == null) {
            server = new Server(packet.getJsonObject().get("name").getAsString(),packet.getJsonObject().get("port").getAsInt());
            Universe.getInstance().getUniverseHandler().getServers().add(server);
        }

        server.refresh(packet.getJsonObject());

//        Universe.getInstance().getServer().broadcastMessage(ChatColor.DARK_GRAY + "[" + ChatColor.YELLOW + "Universe" + ChatColor.DARK_GRAY + "][" + ChatColor.YELLOW + "Server" + ChatColor.DARK_GRAY + "] " + ChatColor.WHITE + server.getName() + " -> updated.");
    }

    @IncomingPacketHandler
    public void onMaintenanceKick(MaintenanceKickPacket packet) {}

    @IncomingPacketHandler
    public void onMaintenanceMode(MaintenanceModePacket packet) {
        Universe.getInstance().getUniverseHandler().setMaintenanceMode(packet.getJsonObject().get("enabled").getAsBoolean());
    }

    @IncomingPacketHandler
    public void onMaintennaceList(MaintenanceListPacket packet) {

        final UUID uuid = UUID.fromString(packet.getJsonObject().get("uuid").getAsString());
        final MaintenanceListPacket.Action action = MaintenanceListPacket.Action.valueOf(packet.getJsonObject().get("action").getAsString());

        if (action == MaintenanceListPacket.Action.ADD) {

            if (!Universe.getInstance().getUniverseHandler().getMaintenanceList().contains(uuid)) {
                Universe.getInstance().getUniverseHandler().getMaintenanceList().add(uuid);
            }

        } else if (action == MaintenanceListPacket.Action.REMOVE) {

            if (Universe.getInstance().getUniverseHandler().getMaintenanceList().contains(uuid)) {
                Universe.getInstance().getUniverseHandler().getMaintenanceList().remove(uuid);
            }

        }
    }

}
