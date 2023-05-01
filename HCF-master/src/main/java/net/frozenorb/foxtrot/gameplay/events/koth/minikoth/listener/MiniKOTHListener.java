package net.frozenorb.foxtrot.gameplay.events.koth.minikoth.listener;

import cc.fyre.piston.command.admin.ScreenShareCommand;
import com.sun.org.apache.regexp.internal.RE;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.DisableCommand;
import net.frozenorb.foxtrot.gameplay.events.koth.KOTH;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class MiniKOTHListener implements Listener {
    @EventHandler(priority = EventPriority.MONITOR)
    private void onMove(PlayerMoveEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final Player player = event.getPlayer();
        final Location from = event.getFrom();
        final Location to = event.getTo();

        if (to.getBlockX() == from.getBlockX() && to.getBlockZ() == from.getBlockZ() && to.getBlockY() == from.getBlockY()) {
            return;
        }

        if (!DTRBitmask.SAFE_ZONE.appliesAt(to)) {
            return;
        }

        if (!to.getBlock().getType().name().contains("WATER")) {
            return;
        }

        final Block downBlock = to.getBlock().getRelative(BlockFace.DOWN);

        if (!downBlock.getType().equals(Material.GLOWSTONE)) {
            final Block downBlockTwo = downBlock.getRelative(BlockFace.DOWN);

            if (!downBlockTwo.getType().equals(Material.GLOWSTONE)) {
                return;
            }
        }

        final KOTH koth = Foxtrot.getInstance().getEventHandler().getActiveKOTH();

        if (koth == null || !koth.isMini()) {
            player.sendMessage(ChatColor.RED + "There is no active Mini KOTH! Type /minikoth schedule to view a schedule!");
            return;
        }

        final Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);

        if (team == null) {
            player.sendMessage(ChatColor.RED + "You must be in a team to warp to a Mini KOTH!");
            return;
        }

        if (team.getOnlineMemberAmount() > 4) {
            player.sendMessage(ChatColor.RED + "You must have less than 4 faction members online!");
            return;
        }

        if (Foxtrot.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You must enable your PvP Timer to warp!");
            return;
        }

        final World world = Foxtrot.getInstance().getServer().getWorld(koth.getWorld());

        if (world == null) {
            player.sendMessage(ChatColor.RED + "Invalid world! Contact an administrator!");
            return;
        }

        player.playSound(player.getLocation(), Sound.PORTAL_TRAVEL, 1, 1);
        player.sendMessage(ChatColor.GREEN + "Warping...");
        player.teleport(world.getSpawnLocation().clone());
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        final Player player = (Player) event.getEntity();

        if (!player.getWorld().getName().contains("MiniKOTH")) {
            return;
        }

        final Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);

        if (team == null) {
            player.sendMessage(ChatColor.RED + "You must be in a team to play in the Mini KOTH!");
            return;
        }

        if (team.getOnlineMemberAmount() > 4) {
            ScreenShareCommand.sendDangerSign(player, "",
                    ChatColor.DARK_RED + ChatColor.BOLD.toString() + "WARNING!!",
                    ChatColor.YELLOW + "You can only have a maximum",
                    ChatColor.YELLOW + "of 3 members online to play MiniKOTH!",
                    ChatColor.RED + "Enemies will now deal double the damage to your faction!", "");
            event.setDamage(event.getDamage()*2);
        }
    }

}
