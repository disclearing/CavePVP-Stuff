package net.frozenorb.foxtrot.listener;


import cc.fyre.modsuite.mod.ModHandler;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StaffUtilsListener implements Listener {

    public static Location lastDamageLocation;
    public static Map<UUID, UUID> lastPlayerHit = new HashMap<>();

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) {
            return;
        }

        final Location location = event.getEntity().getLocation().clone();

        if (location.getWorld().getName().equalsIgnoreCase("Deathban")) {
            return;
        }

        final Player player = (Player) event.getEntity();
        final Player damager = (Player) event.getEntity();

        lastDamageLocation = event.getEntity().getLocation();

        final Team targetTeam = Foxtrot.getInstance().getTeamHandler().getTeam(player);

        if (targetTeam != null) {
            lastPlayerHit.put(damager.getUniqueId(), player.getUniqueId());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (lastDamageLocation != null
                && event.getItem() != null
                && event.getItem().getType() == Material.DIAMOND
                && ModHandler.INSTANCE.isInModMode(event.getPlayer().getUniqueId())) {
            event.getPlayer().teleport(lastDamageLocation);
        }
    }

    @EventHandler
    public void onPlayerInteract2(PlayerInteractEvent event) {
        if (event.getAction() == Action.PHYSICAL && ModHandler.INSTANCE.isInVanish(event.getPlayer().getUniqueId()) && (event.getClickedBlock().getType() == Material.CROPS || event.getClickedBlock().getType() == Material.SOIL)) {
            event.setCancelled(true);
        }
    }

}