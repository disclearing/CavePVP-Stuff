package cc.fyre.universe;

import cc.fyre.universe.proxy.Proxy;
import cc.fyre.universe.server.Server;
import cc.fyre.universe.server.fetch.ServerGroup;
import org.bukkit.ChatColor;

import java.util.List;

/**
 * @author xanderume@gmail (JavaProject)
 */
public class UniverseAPI {

    public static List<Proxy> getProxies() {
        return Universe.getInstance().getUniverseHandler().getProxies();
    }

    public static List<Server> getServers() {
        return Universe.getInstance().getUniverseHandler().getServers();
    }


    public static Proxy proxyFromName(String name) {
        return Universe.getInstance().getUniverseHandler().proxyFromName(name);
    }

    public static Server serverFromName(String name) {
        return Universe.getInstance().getUniverseHandler().serverFromName(name);
    }

    public static String getServerName() {
        return Universe.getInstance().getServerName();
    }

    public static ServerGroup getServerGroup() {
        return Universe.getInstance().getGroup();
    }

    public static String getServerMessage(String server) {
        return ChatColor.GOLD + "Sending you to " + ChatColor.WHITE + server + ChatColor.GOLD + "..";
    }
}
