package cc.fyre.universe.runnable;

import cc.fyre.universe.Universe;

import cc.fyre.universe.packet.ProxyUpdatePacket;

import cc.fyre.universe.proxy.Proxy;

/**
 * @author xanderume@gmail (JavaProject)
 */
public class UniverseRunnable implements Runnable {

    @Override
    public void run() {

        //TODO find a weight to see what port the proxy is running on

        final Proxy proxy = new Proxy(Universe.getInstance().getProxyName(),Universe.getInstance().getProxyRegion(),Universe.getInstance());

        proxy.cache(Universe.getInstance().getPidginHandler().getPool());

        Universe.getInstance().getPidginHandler().sendPacket(new ProxyUpdatePacket(proxy.toJsonObject()));
    }

}
