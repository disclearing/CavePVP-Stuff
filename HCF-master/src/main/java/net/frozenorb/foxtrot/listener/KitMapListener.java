package net.frozenorb.foxtrot.listener;

import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.Ability;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.Players;
import cc.fyre.proton.Proton;
import org.bukkit.*;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

public class KitMapListener implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    private void onMove(PlayerInteractEvent event) {
        if (event.getAction() != Action.PHYSICAL) {
            return;
        }

        final Player player = event.getPlayer();
        final Block clickedBlock = event.getClickedBlock();

        if (clickedBlock == null || !clickedBlock.getType().name().contains("PLATE")) {
            return;
        }

        final Block belowBlock = clickedBlock.getRelative(BlockFace.DOWN);

        if (DTRBitmask.SAFE_ZONE.appliesAt(clickedBlock.getLocation()) && clickedBlock.getType() == Material.GOLD_PLATE && belowBlock.getType().equals(Material.GOLD_BLOCK)) {
            player.teleport(new Location(clickedBlock.getWorld(), 0.5, 71, 65.5));
            return;
        }

        if (!Foxtrot.getInstance().getServerHandler().isWarzone(clickedBlock.getLocation())) {
            return;
        }

        if (clickedBlock.getType() == Material.IRON_PLATE && belowBlock.getType().equals(Material.GOLD_BLOCK)) {
            player.setVelocity(player.getLocation().getDirection().multiply(2));
            player.setMetadata("NO_FALL", new FixedMetadataValue(Foxtrot.getInstance(), true));
            player.playSound(player.getLocation(), Sound.EXPLODE, 1, 4);
            player.playEffect(player.getLocation(), Effect.MOBSPAWNER_FLAMES, 3);
        }

        if (!belowBlock.getType().equals(Material.WOOL)) {
            return;
        }

        if (clickedBlock.getType().equals(Material.IRON_PLATE)) {
            player.teleport(new Location(clickedBlock.getWorld(), 40.5, 161.0, 143.5));
        }

        if (clickedBlock.getType().equals(Material.STONE_PLATE)) {
            player.teleport(new Location(clickedBlock.getWorld(), -34.5, 161.0, 142.5));
        }
    }

    @EventHandler
    private void onClick(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final Block clickedBlock = event.getClickedBlock();

        if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }

        if (clickedBlock == null) {
            return;
        }

        if (!clickedBlock.getType().name().contains("SIGN")) {
            return;
        }

        if (!Foxtrot.getInstance().getServerHandler().isWarzone(player.getLocation())) {
            return;
        }

        final Sign sign = (Sign) clickedBlock.getState();
        String line1 = sign.getLine(0);

        if (line1 == null || line1.contains("Elevator")) {
            return;
        }

        if (line1.contains("Refill")) {
            return;
        }

        sign.setLine(0, CC.translate("&9[Elevator]"));
        sign.setLine(1, "Up");
        sign.setLine(2, "");
        sign.setLine(3, "");
        sign.update();
        player.sendMessage(ChatColor.GREEN + "Fixed a sign!");
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onDamage(EntityDamageEvent event) {

        if (event.getCause() == EntityDamageEvent.DamageCause.FALL && event.getEntity().hasMetadata("NO_FALL")) {
            event.setCancelled(true);
            event.getEntity().removeMetadata("NO_FALL", Foxtrot.getInstance());
        }

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (Foxtrot.getInstance().getDeathbanArenaHandler().isDeathbanArena(event.getEntity())) {
            return;
        }

        if (!Foxtrot.getInstance().getMapHandler().isKitMap()) {
            return;
        }

        final Player victim = event.getEntity();
        final Player killer = victim.getKiller();

        for (Ability value : Foxtrot.getInstance().getMapHandler().getAbilityHandler().getAbilities().values()) {
            if (value.hasCooldown(victim)) {
                value.removeCooldown(victim);
            }
        }

        GoldenAppleListener.getCrappleCooldown().remove(victim.getUniqueId());
        Foxtrot.getInstance().getOppleMap().resetCooldown(victim.getUniqueId());

        if (killer != null && !victim.getUniqueId().equals(killer.getUniqueId()) && !Players.isNaked(victim)) {
            Foxtrot.getInstance().getEconomyHandler().deposit(killer.getUniqueId(), 100 + getAdditional(killer));
            killer.sendMessage(ChatColor.RED + "You received a reward for killing " + ChatColor.GREEN + victim.getName() + ChatColor.RED + ".");
        }
    }

    private int getAdditional(Player killer) {
        if (killer.hasPermission("hcteams.killreward.ghoul")) {
            return 5;
        } else if (killer.hasPermission("hcteams.killreward.poltergeist")) {
            return 5;
        } else if (killer.hasPermission("hcteams.killreward.sorcerer")) {
            return 10;
        } else if (killer.hasPermission("hcteams.killreward.suprive")) {
            return 25;
        } else if (killer.hasPermission("hcteams.killreward.juggernaut")) {
            return 50;
        } else if (killer.hasPermission("hcteams.killreward.myth")) {
            return 75;
        } else if (killer.hasPermission("hcteams.killreward.sapphire")) {
            return 100;
        } else if (killer.hasPermission("hcteams.killreward.pearl")) {
            return 125;
        } else if (killer.hasPermission("hcteams.killreward.ruby")) {
            return 150;
        } else if (killer.hasPermission("hcteams.killreward.velt")) {
            return 175;
        } else if (killer.hasPermission("hcteams.killreward.velt-plus")) {
            return 200;
        } else {
            return 0;
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onProjectileHit(ProjectileHitEvent event) {
        Bukkit.getScheduler().runTaskLater(Foxtrot.getInstance(), event.getEntity()::remove, 1L);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        Team team = LandBoard.getInstance().getTeam(event.getEntity().getLocation());
        if (team != null && event.getEntity() instanceof Arrow && team.hasDTRBitmask(DTRBitmask.SAFE_ZONE)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPortal(PlayerPortalEvent event) {
        if (event.getCause() != TeleportCause.NETHER_PORTAL) {
            return;
        }

        if (event.getTo().getWorld().getEnvironment() != Environment.NETHER) {
            return;
        }

        event.setTo(event.getTo().getWorld().getSpawnLocation().clone());
    }

}
