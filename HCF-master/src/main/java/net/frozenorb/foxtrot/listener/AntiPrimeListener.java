package net.frozenorb.foxtrot.listener;

import org.bukkit.craftbukkit.v1_7_R4.entity.CraftArrow;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;

public class AntiPrimeListener implements Listener {

    @EventHandler
    public void onExplode(CreeperPowerEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true)
    public void onCreeperExplode(EntityExplodeEvent e) {
        if (e.getEntity() instanceof Creeper) {
            e.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityExplode(ExplosionPrimeEvent e) {
        if (e.getEntity() instanceof Creeper) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onEndermanDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Enderman || event.getDamager() instanceof Creeper || event.getDamager() instanceof MagmaCube || event.getDamager() instanceof Slime) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onArrowHit(ProjectileHitEvent event) {

        if (!(event.getEntity() instanceof Arrow)) {
            return;
        }

        final Arrow arrow = (Arrow)event.getEntity();

        if (!(arrow.getShooter() instanceof Player) || ((CraftArrow) arrow).getHandle().fromPlayer == 2) {
            arrow.remove();
        }
    }
}
