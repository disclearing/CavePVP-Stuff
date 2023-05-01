package net.frozenorb.foxtrot.server.pearl;

//import com.lunarclient.bukkitapi.LunarClientAPI;
//import com.lunarclient.bukkitapi.object.LCCooldown;
import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.listener.LunarClientListener;
import net.frozenorb.foxtrot.listener.event.EnderpearlCooldownAppliedEvent;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class EnderpearlCooldownHandler implements Listener {

	@Getter private static Map<String, Long> enderpearlCooldown = new ConcurrentHashMap<>();

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onProjectileLaunch(ProjectileLaunchEvent event) {
		if (!(event.getEntity().getShooter() instanceof Player)) {
			return;
		}

		Player shooter = (Player) event.getEntity().getShooter();

		if (event.getEntity() instanceof EnderPearl) {
			// Store the player's enderpearl in-case we need to remove it prematurely
			shooter.setMetadata("LastEnderPearl", new FixedMetadataValue(Foxtrot.getInstance(), event.getEntity()));

			// Get the default time to apply (in MS)
			long timeToApply = DTRBitmask.THIRTY_SECOND_ENDERPEARL_COOLDOWN.appliesAt(event.getEntity().getLocation()) ? 30_000L : shooter.hasMetadata("DEBUG") ? 1_000L : 16_000L;

			if (shooter.getWorld().getEnvironment() == World.Environment.NORMAL && !Foxtrot.getInstance().getServerHandler().isWarzone(shooter.getLocation())) {
				if (Foxtrot.getInstance().getNetworkBoosterHandler() != null && Foxtrot.getInstance().getNetworkBoosterHandler().isReducedEnderpearl()) {
					timeToApply = 10_000L;
				}
			}

			if (!shooter.getWorld().getEnvironment().equals(World.Environment.NETHER) && shooter.hasMetadata("ENDER_MAN")) {
				timeToApply = 10_000L;
			}

			if (shooter.getWorld().getEnvironment().equals(World.Environment.THE_END) && shooter.hasMetadata("ENDERLOCK")) {
				timeToApply = 10_000L;
			}

			if (ThreadLocalRandom.current().nextInt(0, 200) <= 20 && shooter.hasMetadata("ENDER_MAN")) {
				timeToApply = 1_000L;
			}

			// Call our custom event (time to apply needs to be modifiable)
			EnderpearlCooldownAppliedEvent appliedEvent = new EnderpearlCooldownAppliedEvent(shooter, timeToApply);
			Foxtrot.getInstance().getServer().getPluginManager().callEvent(appliedEvent);

			// Get the final time
			long finalTime = appliedEvent.getTimeToApply();

			// Send LC Cooldown
//			Foxtrot.getInstance().getLunarHandler().sendCooldown(shooter, "EnderPearl", (int) (finalTime / 1000), Material.ENDER_PEARL);

			// Put the player into the cooldown map
			enderpearlCooldown.put(shooter.getName(), System.currentTimeMillis() + finalTime);

			if (shooter.getWorld().getName().equalsIgnoreCase("sg")) {
				Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
					if (enderpearlCooldown.containsKey(shooter.getName()) && enderpearlCooldown.get(shooter.getName()) > System.currentTimeMillis()) {
						shooter.getInventory().addItem(new ItemStack(Material.ENDER_PEARL, 1));
					}
				}, 20*8);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerInteract(ProjectileLaunchEvent event) {
		if (!(event.getEntity() instanceof EnderPearl)) {
			return;
		}

		if (!(event.getEntity().getShooter() instanceof Player)) {
			return;
		}

		Player thrower = (Player) event.getEntity().getShooter();

		if (enderpearlCooldown.containsKey(thrower.getName()) && enderpearlCooldown.get(thrower.getName()) > System.currentTimeMillis()) {
			long millisLeft = enderpearlCooldown.get(thrower.getName()) - System.currentTimeMillis();

			double value = (millisLeft / 1000D);
			double sec = value > 0.1 ? Math.round(10.0 * value) / 10.0 : 0.1; // don't tell user 0.0

			event.setCancelled(true);
			thrower.sendMessage(ChatColor.RED + "You cannot use this for another " + ChatColor.BOLD + sec + ChatColor.RED + " seconds!");
			thrower.updateInventory();
		}
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		if (event.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
			return;
		} else if (!enderpearlCooldown.containsKey(event.getPlayer().getName())) {
			event.setCancelled(true); // only reason for this would be player died before pearl landed, so cancel it!
			return;
		}

		Location target = event.getTo();
		Location from = event.getFrom();

		if (DTRBitmask.SAFE_ZONE.appliesAt(target)) {
			if (!DTRBitmask.SAFE_ZONE.appliesAt(from)) {
				event.setCancelled(true);
				removeCooldown(event.getPlayer());
				event.getPlayer().sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Invalid Pearl! " + ChatColor.YELLOW + "You cannot Enderpearl into spawn!");
				return;
			}
		}

		if (DTRBitmask.NO_ENDERPEARL.appliesAt(target) || DTRBitmask.NO_ENDERPEARL.appliesAt(from)) {
			event.setCancelled(true);
			removeCooldown(event.getPlayer());
			event.getPlayer().sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Invalid Pearl! " + ChatColor.YELLOW + "You cannot Enderpearl into this region!");
			return;
		}

		Team ownerTo = LandBoard.getInstance().getTeam(event.getTo());

		if (Foxtrot.getInstance().getPvPTimerMap().hasTimer(event.getPlayer().getUniqueId()) && ownerTo != null) {
			if (ownerTo.isMember(event.getPlayer().getUniqueId())) {
				event.getPlayer().removeMetadata("PVP_TIMER_BYPASS", Foxtrot.getInstance());
				Foxtrot.getInstance().getPvPTimerMap().removeTimer(event.getPlayer().getUniqueId());
				LunarClientListener.updateNametag(event.getPlayer());
			} else if (ownerTo.getOwner() != null || (DTRBitmask.KOTH.appliesAt(event.getTo()) || DTRBitmask.CITADEL.appliesAt(event.getTo()))) {
				event.setCancelled(true);
				removeCooldown(event.getPlayer());
				event.getPlayer().sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "Invalid Pearl! " + ChatColor.YELLOW + "You cannot Enderpearl into claims while having a PvP Timer!");
			}
		}
	}

	public boolean clippingThrough(Location target, Location from, double thickness) {
		return ((from.getX() > target.getX() && (from.getX() - target.getX() < thickness)) || (target.getX() > from.getX() && (target.getX() - from.getX() < thickness)) ||
		        (from.getZ() > target.getZ() && (from.getZ() - target.getZ() < thickness)) || (target.getZ() > from.getZ() && (target.getZ() - from.getZ() < thickness)));
	}

	public static void clearEnderpearlTimer(Player player) {
//		LunarClientAPI.getInstance().sendCooldown(player, new LCCooldown("Enderpearl", 0, TimeUnit.SECONDS, Material.ENDER_PEARL));
		enderpearlCooldown.remove(player.getName());
	}

	public static void resetEnderpearlTimer(Player player) {

		if (DTRBitmask.THIRTY_SECOND_ENDERPEARL_COOLDOWN.appliesAt(player.getLocation())) {
//			LunarClientAPI.getInstance().sendCooldown(player, new LCCooldown("Enderpearl", 30_000L, TimeUnit.SECONDS, Material.ENDER_PEARL));
			enderpearlCooldown.put(player.getName(), System.currentTimeMillis() + 30_000L);
		} else {
//			LunarClientAPI.getInstance().sendCooldown(player, new LCCooldown("Enderpearl", 16_000L, TimeUnit.SECONDS, Material.ENDER_PEARL));
			enderpearlCooldown.put(player.getName(), System.currentTimeMillis() + (Foxtrot.getInstance().getMapHandler().getScoreboardTitle().contains("Staging") ? 1_000L : 16_000L));
		}
	}

	public static void removeCooldown(Player player) {
//		LunarClientAPI.getInstance().sendCooldown(player, new LCCooldown("Enderpearl", 0, TimeUnit.SECONDS, Material.ENDER_PEARL));

		enderpearlCooldown.remove(player.getName());
	}
}