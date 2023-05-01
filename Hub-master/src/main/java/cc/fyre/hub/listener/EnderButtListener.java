package cc.fyre.hub.listener;

import org.bukkit.Material;
import org.bukkit.Sound;

import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import org.spigotmc.event.entity.EntityDismountEvent;


public class EnderButtListener implements Listener {

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteract(PlayerInteractEvent event) {

        if (!event.hasItem()) {
            return;
        }

        if (!event.getAction().name().contains("RIGHT")) {
            return;
        }

        if (event.getItem().getType() != Material.ENDER_PEARL) {
            return;
        }

        final Player player = event.getPlayer();

        event.setCancelled(true);

        if (player.getVehicle() != null) {
            if (player.getVehicle() instanceof EnderPearl) player.getVehicle().remove();
            if (player.getVehicle().getVehicle() instanceof EnderPearl) player.getVehicle().remove();
        }

        final EnderPearl enderPearl = player.launchProjectile(EnderPearl.class);

        enderPearl.setPassenger(player);
        enderPearl.setVelocity(player.getLocation().getDirection().multiply(2));

        player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1, 1);
        player.spigot().setCollidesWithEntities(false);
        player.updateInventory();
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onProjectileHit(ProjectileHitEvent event) {

        if (!(event.getEntity() instanceof EnderPearl)) {
            return;
        }

        if (!(event.getEntity().getShooter() instanceof Player)) {
            return;
        }

        ((Player) event.getEntity().getShooter()).getLocation().add(0.0F, 1.0F, 0.0F);
        ((Player) event.getEntity().getShooter()).spigot().setCollidesWithEntities(true);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDismount(EntityDismountEvent event) {

        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        if (!(event.getDismounted() instanceof EnderPearl)) {
            return;
        }

        event.getDismounted().remove();

        event.getEntity().getLocation().add(0.0F, 1.0F, 0.0F);
        ((Player) event.getEntity()).spigot().setCollidesWithEntities(true);

        DoubleJumpListener.getAllowSneakJump().put(event.getEntity().getUniqueId(), false);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(PlayerQuitEvent event) {

        if (!event.getPlayer().isInsideVehicle()) {
            return;
        }

        event.getPlayer().getVehicle().remove();
        event.getPlayer().spigot().setCollidesWithEntities(true);
    }


}
