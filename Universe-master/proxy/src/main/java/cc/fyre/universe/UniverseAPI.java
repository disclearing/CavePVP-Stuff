package cc.fyre.universe;

import cc.fyre.universe.proxy.Proxy;
import cc.fyre.universe.proxy.fetch.ProxyRegion;
import cc.fyre.universe.server.Server;

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

    public static String getProxyName() {
        return Universe.getInstance().getProxyName();
    }

    public static ProxyRegion getProxyRegion() {
        return Universe.getInstance().getProxyRegion();
    }
}
