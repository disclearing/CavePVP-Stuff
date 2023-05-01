package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.cavepvp.suge.kit.event.KitUseEvent;

public class SugeListener implements Listener {


    @EventHandler(priority = EventPriority.LOWEST)
    private void onKitUse(KitUseEvent event) {
        if (!canUseKit(event.getPlayer(), true)) {
            event.setCancelled(true);
        }
    }

    public static boolean canUseKit(Player player, boolean checkSpawnTag) {
        final Location location = player.getLocation();

        if (player.getGameMode() == GameMode.CREATIVE && player.isOp()) {
            return true;
        }

        // let kids with sotw timer use it whenever
        if (CustomTimerCreateCommand.isSOTWTimer() && !CustomTimerCreateCommand.hasSOTWEnabled(player.getUniqueId())) {
            return true;
        }

        if (location.getWorld().getEnvironment() == World.Environment.NETHER && !CustomTimerCreateCommand.isSOTWTimer()) {
            player.sendMessage(ChatColor.RED + "You can't use a kit at all in Nether!");
            return false;
        }

        final Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);

        if (team != null && AntiCleanListener.CACHE.containsKey(team) && DTRBitmask.SAFE_ZONE.appliesAt(location)) {
            player.sendMessage(ChatColor.RED + "You may not use a kit in Spawn while your faction is anti-clean combat tagged!");
            return false;
        }

        // let kids with pvp timer or in spawn use it whenever
        if (Foxtrot.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId()) || DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) {
            return true;
        }

        if (Foxtrot.getInstance().getInDuelPredicate().test(player) || Foxtrot.getInstance().getInEventPredicate().test(player)) {
            player.sendMessage(ChatColor.RED + "You may not use a kit whilst in a Duel or in an Event!");
            return false;
        }

        if (location.getWorld().getEnvironment() != World.Environment.NORMAL) {
            player.sendMessage(ChatColor.RED + "You can only use a kit in The Overworld!");
            return false;
        }

        if (DTRBitmask.CONQUEST.appliesAt(location) || DTRBitmask.KOTH.appliesAt(location) || DTRBitmask.CITADEL.appliesAt(location)) {
            player.sendMessage(ChatColor.RED + "You may not use a kit in " + LandBoard.getInstance().getClaim(location).getName() + ChatColor.RED + ".");
            return false;
        }

        if (Foxtrot.getInstance().getServerHandler().isWarzone(location)) {
            player.sendMessage(ChatColor.RED + "You may not use a kit in WarZone! You must be 1000 blocks out of Spawn to use one!");
            return false;
        }

        if (SpawnTagHandler.isTagged(player) && checkSpawnTag) {
            player.sendMessage(ChatColor.RED + "You may not use a kit while you are spawn tagged!");
            return false;
        }

        return true;
    }
}
