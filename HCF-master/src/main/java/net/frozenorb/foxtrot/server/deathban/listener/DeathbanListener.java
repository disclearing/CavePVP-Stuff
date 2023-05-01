package net.frozenorb.foxtrot.server.deathban.listener;

import cc.fyre.universe.util.BungeeUtil;
import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.Foxtrot;
import cc.fyre.proton.util.TimeUtils;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.util.InventoryUtils;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.cavepvp.suge.Suge;
import org.cavepvp.suge.kit.data.Kit;

import javax.naming.ldap.PagedResultsControl;
import java.util.concurrent.ThreadLocalRandom;

@AllArgsConstructor
public class DeathbanListener implements Listener {
    private Foxtrot instance;
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(final PlayerDeathEvent event) {
        final Player player = event.getEntity();

        if (this.instance.getMapHandler().isKitMap() || player.hasPermission("foxtrot.staff") || this.instance.getInDuelPredicate().test(event.getEntity()) || player.getWorld().getName().equalsIgnoreCase("Deathban")) {
            return;
        }

        int seconds = (int) this.instance.getServerHandler().getDeathban(event.getEntity());

        if (player.getWorld().getEnvironment() == World.Environment.NETHER) {
            seconds += 300;
        } else if (player.getWorld().getEnvironment() == World.Environment.THE_END || DTRBitmask.CITADEL.appliesAt(player.getLocation()) || DTRBitmask.KOTH.appliesAt(player.getLocation())) {
            seconds += 180;
        }

        this.instance.getDeathbanMap().deathban(player.getUniqueId(), seconds);

        if (this.instance.getServerHandler().isEOTW() && this.instance.getServerHandler().isPreEOTW() || this.instance.getServer().getWorld("Deathban") == null) {
            this.kickPlayer(player);
            return;
        }

        final String time = TimeUtils.formatIntoDetailedString(seconds);
        player.sendMessage(ChatColor.RED + "You have been deathbanned for " + time);

        this.instance.getDeathbanArenaHandler().deathban(player);
    }

    @EventHandler
    private void onRespawn(PlayerRespawnEvent event) {
        final Player player = event.getPlayer();

        if (!this.instance.getDeathbanArenaHandler().isDeathbanArena(player)) {
            return;
        }

        final Kit kit = Suge.getInstance().getKitHandler().findKit("UltimateDiamond").orElse(null);

        InventoryUtils.resetInventoryNow(player);
        if (kit != null) {
            kit.apply(player);
        }

    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        if (this.instance.getMapHandler().isKitMap() || !this.instance.getDeathbanMap().isDeathbanned(player.getUniqueId())) {
            return;
        }

        boolean deathBanned = this.instance.getDeathbanMap().isDeathbanned(player.getUniqueId());

        if (deathBanned) {
            if (player.hasPermission("foxtrot.staff")) {
                Foxtrot.getInstance().getDeathbanArenaHandler().revive(player.getUniqueId());
                return;
            }

            if (this.instance.getServerHandler().isEOTW()) {
                player.kickPlayer(ChatColor.RED + "You are deathbanned for the rest of the map!\nCheck out SOTW information at:\ncavepvp.org/discord");
                return;
            }
        }

        long seconds = (this.instance.getDeathbanMap().getDeathban(player.getUniqueId()) - System.currentTimeMillis()) / 1000;
        player.sendMessage("");
        player.sendMessage(ChatColor.RED + "You are still deathbanned for " + TimeUtils.formatIntoDetailedString((int)seconds) + "!");
    }

    public void kickPlayer(Player player) {
        final String randomHub = "Hub-0" + ThreadLocalRandom.current().nextInt(1,3);

        new BukkitRunnable() {
            public void run() {
                if (player.isOnline()) {
                    player.sendMessage(ChatColor.RED + "You have been deathbanned!");
                    BungeeUtil.sendToServer(player, randomHub);
                }
            }
        }.runTaskLaterAsynchronously(this.instance, 20*2);

        new BukkitRunnable() {
            public void run() {
                if (player.isOnline()) {
                    player.kickPlayer(ChatColor.RED + "You have been deathbanned!");
                }

            }
        }.runTaskLater(this.instance, 20*6);
    }
}