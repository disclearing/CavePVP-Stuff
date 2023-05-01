package cc.fyre.hub.tab;

import cc.fyre.hub.Hub;
import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.proton.tab.construct.TabLayout;
import cc.fyre.proton.tab.provider.LayoutProvider;
import cc.fyre.universe.Universe;
import cc.fyre.universe.server.Server;
import cc.fyre.universe.server.fetch.ServerStatus;
import com.minexd.quartz.Quartz;
import com.minexd.quartz.queue.Queue;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author xanderume@gmail (JavaProject)
 */
public class HubTabLayout implements LayoutProvider {

    private final AtomicInteger loading = new AtomicInteger(0);
    public static int bunkersPlayers = 0;
    public static int practicePlayers = 0;
    public static int meetupPlayers = 0;

    public HubTabLayout() {
        Hub.getInstance().getServer().getScheduler().runTaskTimer(Hub.getInstance(), () -> {

            if (this.loading.get() == 3) {
                this.loading.set(0);
            } else {
                this.loading.getAndIncrement();
            }

            bunkersPlayers = 0;
            practicePlayers = 0;
            meetupPlayers = 0;

            for (Server value : Universe.getInstance().getUniverseHandler().getServers()) {

                if (value.getName().contains("Bunkers")) {
                    bunkersPlayers += value.getOnlinePlayers().get();
                } else if (value.getName().contains("Practice")) {
                    practicePlayers += value.getOnlinePlayers().get();
                } else if (value.getName().startsWith("Meetups")) {
                    meetupPlayers += value.getOnlinePlayers().get();
                }
            }
        }, 0, 20L);
    }

    @Override
    public TabLayout provide(Player player) {

        final TabLayout toReturn = TabLayout.create(player);
        final int amountOn = Hub.getInstance().getPlayersOnline();

        toReturn.set(1, 0, ChatColor.DARK_RED.toString() + ChatColor.BOLD + "CavePvP");
        toReturn.set(1, 1, amountOn + " &7player" + (amountOn == 1 ? "" : "s") + " online");

        toReturn.set(0, 3, ChatColor.DARK_RED + "Rank:");

        final Profile profile = Neutron.getInstance().getProfileHandler().fromUuid(player.getUniqueId());

        toReturn.set(0, 4, (profile.hasSubscription() ? ChatColor.YELLOW + "✪" + ChatColor.RESET : "") + profile.getActiveGrant().getRank().getFancyName());

        toReturn.set(2, 3, ChatColor.DARK_RED + "Server:");
        toReturn.set(2, 4, ChatColor.WHITE + Universe.getInstance().getServerName());

        final Server kits = Universe.getInstance().getUniverseHandler().serverFromName("Kits");

        toReturn.set(0, 7, ChatColor.DARK_RED + "Kits");
        toReturn.set(0, 8, ChatColor.GRAY + "Online: " + ChatColor.DARK_RED + this.online(kits));
        toReturn.set(0, 9, ChatColor.GRAY + "Status: " + this.status(kits));

        final Server fasts = Universe.getInstance().getUniverseHandler().serverFromName("Fasts");

        toReturn.set(1, 7, ChatColor.DARK_RED + "Fasts");
        toReturn.set(1, 8, ChatColor.GRAY + "Online: " + ChatColor.DARK_RED + this.online(fasts));
        toReturn.set(1, 9, ChatColor.GRAY + "Status: " + this.status(fasts));

        final Server bunkers = Universe.getInstance().getUniverseHandler().serverFromName("Bunkers-Lobby");

        toReturn.set(2, 7, ChatColor.DARK_RED + "Bunkers");
        toReturn.set(2, 8, ChatColor.GRAY + "Online: " + ChatColor.DARK_RED + this.online(bunkers));
        toReturn.set(2, 9, ChatColor.GRAY + "Status: " + this.status(bunkers));

        final Server naPractice = Universe.getInstance().getUniverseHandler().serverFromName("NA-Practice");

        toReturn.set(0, 12, ChatColor.DARK_RED + "NA-Practice");
        toReturn.set(0, 13, ChatColor.GRAY + "Online: " + ChatColor.DARK_RED + this.online(naPractice));
        toReturn.set(0, 14, ChatColor.GRAY + "Status: " + this.status(naPractice));

        final Server auPractice = Universe.getInstance().getUniverseHandler().serverFromName("Skyblock");

        toReturn.set(2, 12, ChatColor.DARK_RED + "Skyblock");
        toReturn.set(2, 13, ChatColor.GRAY + "Online: " + ChatColor.DARK_RED + this.online(auPractice));
        toReturn.set(2, 14, ChatColor.GRAY + "Status: " + this.status(auPractice));

        final Queue queue = Quartz.get().getQuartzData().getQueueByPlayer(player.getUniqueId());

        if (queue != null) {
            toReturn.set(1, 12, ChatColor.DARK_RED + "Queue:");
            toReturn.set(1, 13, ChatColor.WHITE + queue.getName());
            toReturn.set(1, 14, ChatColor.GRAY + "Position: " + ChatColor.DARK_RED + queue.getPosition(player.getUniqueId()));
        }

        return toReturn;
    }

    private String online(Server server) {

        if (server == null) {
            return "0/0";
        }

        int players = server.getOnlinePlayers().get();

        if (server.getName().contains("Bunkers")) {
            return bunkersPlayers + "/" + "1000";
        }

        return players + "/" + server.getMaximumPlayers().get();
    }

    private String status(Server server) {

        if (server == null) {
            return ChatColor.RED + "Loading" + StringUtils.repeat(".", this.loading.get());
        }

        if (server.getStatus() == ServerStatus.OFFLINE) {
            return ChatColor.RED + "Offline";
        } else if (server.getStatus() == ServerStatus.WHITELISTED) {
            return ChatColor.WHITE + "Whitelisted";
        }

        return ChatColor.GREEN + "Online";
    }
}