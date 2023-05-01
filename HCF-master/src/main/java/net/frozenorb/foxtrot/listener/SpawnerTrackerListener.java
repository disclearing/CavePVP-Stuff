package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.listener.event.CrowbarSpawnerBreakEvent;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.Claim;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class SpawnerTrackerListener implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void onBlockPlaceEvent(BlockPlaceEvent event) {
		if (event.getBlockPlaced().getType() == Material.MOB_SPAWNER) {
			Team team = Foxtrot.getInstance().getTeamHandler().getTeam(event.getPlayer());

			if (team != null) {
				Claim claim = LandBoard.getInstance().getClaim(event.getBlockPlaced().getLocation());

				if (team.getSpawnersInClaim() >= 5) {
					event.getPlayer().sendMessage(ChatColor.RED + "You can only have 5 spawners in your claim!");
					event.setCancelled(true);
					return;
				}

				if (claim != null && team.getClaims().contains(claim)) {
					team.addSpawnersInClaim(1);
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onBlockBreakEvent(BlockBreakEvent event) {
		if (event.getBlock().getType() == Material.MOB_SPAWNER) {
			Team team = Foxtrot.getInstance().getTeamHandler().getTeam(event.getPlayer());

			if (team != null) {
				Claim claim = LandBoard.getInstance().getClaim(event.getBlock().getLocation());

				if (claim != null && team.getClaims().contains(claim)) {
					team.removeSpawnersInClaim(1);
				}
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onSpawnerBreakEvent(CrowbarSpawnerBreakEvent event) {
		Team team = Foxtrot.getInstance().getTeamHandler().getTeam(event.getPlayer());

		if (team != null) {
			Claim claim = LandBoard.getInstance().getClaim(event.getBlock().getLocation());

			if (claim != null && team.getClaims().contains(claim)) {
				team.removeSpawnersInClaim(1);
			}
		}
	}

}
