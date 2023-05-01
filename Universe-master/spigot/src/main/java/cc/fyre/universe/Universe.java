package cc.fyre.universe;

import cc.fyre.universe.command.HubCommand;
import cc.fyre.universe.command.ServerCommand;
import cc.fyre.universe.command.UniverseEnvironmentCommand;
import lombok.Getter;

import cc.fyre.universe.listener.UniverseListener;
import cc.fyre.universe.packet.ProxyUpdatePacket;
import cc.fyre.universe.packet.ServerUpdatePacket;
import cc.fyre.universe.packet.maintenance.MaintenanceKickPacket;
import cc.fyre.universe.packet.maintenance.MaintenanceListPacket;
import cc.fyre.universe.packet.maintenance.MaintenanceModePacket;
import cc.fyre.universe.pidgin.PidginHandler;

import cc.fyre.universe.runnable.UniverseRunnable;
import cc.fyre.universe.server.Server;
import cc.fyre.universe.server.fetch.ServerGroup;
import cc.fyre.universe.server.fetch.ServerStatus;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author xanderume@gmail (JavaProject)
 */
public class Universe extends JavaPlugin {

    @Getter private static Universe instance;

    @Getter private String serverName;
    @Getter private ServerGroup group;

    @Getter private PidginHandler pidginHandler;
    @Getter private UniverseHandler universeHandler;

    @Getter private boolean loaded = false;
    @Getter private boolean stopping = false;

    public static int fakePlayers = 0;

    @Override
    public void onEnable() {
        instance = this;

        this.saveDefaultConfig();

        this.serverName = this.getConfig().getString("name");
        this.group = ServerGroup.valueOf(this.getConfig().getString("group"));

        this.pidginHandler = new PidginHandler(this.getConfig().getString("redis.host"));

        this.pidginHandler.registerPacket(MaintenanceKickPacket.class);
        this.pidginHandler.registerPacket(MaintenanceListPacket.class);
        this.pidginHandler.registerPacket(MaintenanceModePacket.class);

        this.pidginHandler.registerPacket(ProxyUpdatePacket.class);
        this.pidginHandler.registerPacket(ServerUpdatePacket.class);

        this.universeHandler = new UniverseHandler(this.pidginHandler.getPool());

        this.pidginHandler.registerListener(new UniverseListener());

        new UniverseRunnable().runTaskTimerAsynchronously(this,0L,3*20L);

        this.getCommand("hub").setExecutor(new HubCommand());
        this.getCommand("server").setExecutor(new ServerCommand());
        this.getCommand("universeenvironment").setExecutor(new UniverseEnvironmentCommand());

        this.getServer().getMessenger().registerOutgoingPluginChannel(this,"BungeeCord");
    }

    @Override
    public void onDisable() {
        stopping = true;

        final Server server = new Server(this.serverName,this.group,this, true, fakePlayers);

        server.setStatus(ServerStatus.OFFLINE);
        server.getOnlinePlayers().set(0);
        server.cache(Universe.getInstance().getPidginHandler().getPool());

        this.pidginHandler.sendPacket(new ServerUpdatePacket(server.toJsonObject()));
        this.pidginHandler.getPool().close();
    }

}
