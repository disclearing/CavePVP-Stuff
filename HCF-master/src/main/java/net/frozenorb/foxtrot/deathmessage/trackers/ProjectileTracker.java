package net.frozenorb.foxtrot.deathmessage.trackers;

import net.frozenorb.foxtrot.deathmessage.event.CustomPlayerDamageEvent;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class ProjectileTracker implements Listener {

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onCustomPlayerDamage(CustomPlayerDamageEvent event) {
        if (event.getCause() instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent entityDamageByEntityEvent = (EntityDamageByEntityEvent) event.getCause();

            if (entityDamageByEntityEvent.getDamager() instanceof Projectile && !(entityDamageByEntityEvent.getDamager() instanceof Arrow)) {
                final Projectile projectile = (Projectile) entityDamageByEntityEvent.getDamager();

                if (projectile.getShooter() instanceof Player) {
                    final Player shooter = (Player) projectile.getShooter();
                    final Player damaged = event.getPlayer();

                    if (shooter != damaged) {
                        event.setTrackerDamage(new PVPTracker.PVPDamage(damaged.getName(), event.getDamage(), shooter.getName(), shooter.getItemInHand()));
                    }
                }
            }
        }
    }
}
