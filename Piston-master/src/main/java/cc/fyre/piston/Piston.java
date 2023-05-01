package cc.fyre.piston;

import cc.fyre.neutron.NeutronConstants;
import cc.fyre.piston.chat.ChatHandler;
import cc.fyre.piston.client.ClientHandler;
import cc.fyre.piston.client.listener.ClientBrandListener;
import cc.fyre.piston.listener.FreezeListener;
import cc.fyre.piston.listener.GriefListener;
import cc.fyre.piston.listener.PistonListener;
import cc.fyre.piston.packet.FrozenLogoutPacket;
import cc.fyre.piston.packet.StaffBroadcastPacket;
import cc.fyre.piston.packet.listener.PistonPacketListener;
import cc.fyre.piston.server.ServerHandler;
import cc.fyre.piston.sync.SyncHandler;
import cc.fyre.piston.util.Cooldown;
import cc.fyre.proton.Proton;
import cc.fyre.proton.pidgin.packet.Packet;
import cc.fyre.proton.util.ReflectionUtil;
import cc.fyre.universe.UniverseAPI;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class Piston extends JavaPlugin {

    @Getter
    private static Piston instance;

    @Getter
    private long startupTime;

    @Getter @Setter private boolean maintenance;

    @Getter
    private ChatHandler chatHandler;
    @Getter
    private ServerHandler serverHandler;
    @Getter
    private ClientHandler clientHandler;
    @Getter private SyncHandler syncHandler;

    @Getter
    private final Map<UUID, Cooldown> reportCooldownCache = new HashMap<>();
    @Getter
    private final Map<UUID, Cooldown> requestCooldownCache = new HashMap<>();
    @Getter
    private final List<UUID> toggleStaff = new ArrayList<>();
    @Getter
    private final Map<UUID, Location> backCache = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;

        this.startupTime = System.currentTimeMillis();

        this.getServer().getPluginManager().registerEvents(new FreezeListener(), this);
        this.getServer().getPluginManager().registerEvents(new PistonListener(), this);
        this.getServer().getPluginManager().registerEvents(new GriefListener(), this);

        Proton.getInstance().getCommandHandler().registerAll(this);

        ReflectionUtil.setMaxPlayers(this.getServer(), this.getConfig().getInt("server.slots", 100));
        getServer().getMessenger().registerIncomingPluginChannel(this, "MC|Brand", new ClientBrandListener());
        this.chatHandler = new ChatHandler(this);
        this.serverHandler = new ServerHandler(this);
        this.clientHandler = new ClientHandler();
        this.syncHandler = new SyncHandler(this);

        Proton.getInstance().getPidginHandler().registerPacket(StaffBroadcastPacket.class);
        Proton.getInstance().getPidginHandler().registerPacket(FrozenLogoutPacket.class);
        Proton.getInstance().getPidginHandler().registerListener(new PistonPacketListener());

        this.getServer().getScheduler().runTaskLaterAsynchronously(this, () -> Proton.getInstance().getPidginHandler().sendPacket(new StaffBroadcastPacket(
                NeutronConstants.MANAGER_PERMISSION,
                ChatColor.translate("&9[Universe] &f" + UniverseAPI.getServerName() + " &7is now &aonline &7and may be joined.")
        )), 20*3);
    }

    public void sendPacketAsync(Packet packet) {
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> Proton.getInstance().getPidginHandler().sendPacket(packet));
    }

    @Override
    public void onDisable() {
        this.getServer().getScheduler().runTaskLaterAsynchronously(this, () -> Proton.getInstance().getPidginHandler().sendPacket(new StaffBroadcastPacket(
                NeutronConstants.MANAGER_PERMISSION,
                ChatColor.translate("&9[Universe] &f" + UniverseAPI.getServerName() + " &7is now &coffline &7and may no longer be joined.")
        )), 5);

        this.toggleStaff.clear();
    }
}
