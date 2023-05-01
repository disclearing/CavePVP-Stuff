package net.frozenorb.foxtrot.gameplay.events.outposts;

import cc.fyre.proton.event.HourEvent;
import lombok.RequiredArgsConstructor;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.events.outposts.data.Outpost;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

@RequiredArgsConstructor
public class OutpostListener implements Listener {
    private final OutpostHandler outpostHandler;

    @EventHandler
    private void onHour(HourEvent event) {
        if (Foxtrot.getInstance().getMapHandler().isKitMap()) {
            return;
        }

        this.outpostHandler.findRoadOutpost().respawnOutpostChests();
        Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
            for (Player onlinePlayer : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
                onlinePlayer.sendMessage("");
                onlinePlayer.sendMessage(ChatColor.translate("&6&lRoad Outpost"));
                onlinePlayer.sendMessage(ChatColor.translate("&eAll road outpost chests have been reset!"));
                onlinePlayer.sendMessage(ChatColor.RED + "You must capture Outpost before looting it! Type /outpost to view who's captured it!");
                onlinePlayer.sendMessage("");
            }
        }, 20*40);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onMove(PlayerMoveEvent event) {
        final Location to = event.getTo(), from = event.getFrom();

        if (to.getBlockX() == from.getBlockX() && to.getBlockY() == from.getBlockY() && to.getBlockZ() == from.getBlockZ()) {
            return;
        }

        final Player player = event.getPlayer();
        final Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);

        if (team == null) {
            return;
        }

        Outpost outpostTo = this.outpostHandler.findOutpost(to), outpostFrom = outpostHandler.findOutpost(from);

        if((outpostFrom != null && outpostFrom != outpostTo) && outpostFrom.removePlayer(player)) {
            outpostFrom.updateAttackers();
            return;
        }

        if (outpostHandler.findControllingOutposts(team).contains(outpostTo)) {
            return;
        }

        if (outpostTo == null || !outpostTo.addPlayer(player)) {
            return;
        }

        outpostTo.updateAttackers();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onTeleport(PlayerTeleportEvent event) {
        Outpost outpostTo = outpostHandler.findOutpost(event.getTo()), outpostFrom = outpostHandler.findOutpost(event.getFrom());

        if(outpostFrom == null || outpostFrom == outpostTo) {
            return;
        }

        final Player player = event.getPlayer();
        if(outpostFrom.removePlayer(player)) {
            outpostFrom.updateAttackers();
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        final Outpost outpost = this.outpostHandler.findOutpost(player.getLocation());

        if (outpost == null) {
            return;
        }

        if(outpost.removePlayer(player)) {
            outpost.updateAttackers();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDeath(PlayerDeathEvent event) {
        final Player player = event.getEntity();
        final Outpost outpostZone = this.outpostHandler.findOutpost(player.getLocation());

        if(outpostZone != null && outpostZone.removePlayer(player)) {
            outpostZone.updateAttackers();
        }
    }
}
