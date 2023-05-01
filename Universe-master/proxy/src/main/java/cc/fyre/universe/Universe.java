package cc.fyre.universe;

import cc.fyre.universe.listener.HubListener;
import lombok.Getter;

import cc.fyre.universe.config.UniverseConfig;
import cc.fyre.universe.listener.MaintenanceListener;
import cc.fyre.universe.listener.UniverseListener;
import cc.fyre.universe.packet.ProxyUpdatePacket;
import cc.fyre.universe.packet.ServerUpdatePacket;
import cc.fyre.universe.packet.maintenance.MaintenanceKickPacket;
import cc.fyre.universe.packet.maintenance.MaintenanceListPacket;
import cc.fyre.universe.packet.maintenance.MaintenanceModePacket;
import cc.fyre.universe.pidgin.PidginHandler;

import cc.fyre.universe.proxy.Proxy;
import cc.fyre.universe.proxy.fetch.ProxyRegion;

import cc.fyre.universe.proxy.fetch.ProxyStatus;
import cc.fyre.universe.runnable.UniverseRunnable;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.concurrent.TimeUnit;

/**
 * @author xanderume@gmail (JavaProject)
 */
public class Universe extends Plugin {

    @Getter private static Universe instance;

    @Getter private UniverseConfig config;

    @Getter private String proxyName;
    @Getter private ProxyRegion proxyRegion;

    @Getter private PidginHandler pidginHandler;
    @Getter private UniverseHandler universeHandler;

    @Override
    public void onEnable() {
        instance = this;

        this.config = new UniverseConfig(this);

        this.proxyName = this.config.getConfiguration().getString("name");
        this.proxyRegion = ProxyRegion.valueOf(this.config.getConfiguration().getString("region"));

        this.pidginHandler = new PidginHandler(this.config.getConfiguration().getString("redis.host"));

        this.pidginHandler.registerPacket(MaintenanceKickPacket.class);
        this.pidginHandler.registerPacket(MaintenanceListPacket.class);
        this.pidginHandler.registerPacket(MaintenanceModePacket.class);

        this.pidginHandler.registerPacket(ProxyUpdatePacket.class);
        this.pidginHandler.registerPacket(ServerUpdatePacket.class);

        this.pidginHandler.registerListener(new UniverseListener());

        this.universeHandler = new UniverseHandler(this.pidginHandler.getPool());

        this.getProxy().getPluginManager().registerListener(this,new HubListener());
        this.getProxy().getPluginManager().registerListener(this,new MaintenanceListener());
        this.getProxy().getScheduler().schedule(this,new UniverseRunnable(),0,3L,TimeUnit.SECONDS);

        this.getProxy().getConfig().getDisabledCommands().add("server");
    }

    @Override
    public void onDisable() {

        final Proxy proxy = new Proxy(this.proxyName,this.proxyRegion,this);

        proxy.setStatus(ProxyStatus.OFFLINE);
        proxy.getOnlinePlayers().set(0);

        this.pidginHandler.sendPacket(new ProxyUpdatePacket(proxy.toJsonObject()));
        this.pidginHandler.getPool().close();
    }

}
