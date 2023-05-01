package net.frozenorb.foxtrot.deathmessage.trackers;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.deathmessage.event.CustomPlayerDamageEvent;
import net.frozenorb.foxtrot.deathmessage.objects.PlayerDamage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.ExplosiveMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.UUID;

public class MinecartTracker implements Listener {

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onCustomPlayerDamage(CustomPlayerDamageEvent event) {
        if (event.getCause() instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent entityDamageByEntityEvent = (EntityDamageByEntityEvent) event.getCause();

            if (entityDamageByEntityEvent.getDamager() instanceof ExplosiveMinecart) {

                final ExplosiveMinecart explosiveMinecart = (ExplosiveMinecart) entityDamageByEntityEvent.getDamager();

                if (!explosiveMinecart.hasMetadata("TNT_MINECART")) {
                    return;
                }

                final UUID uuid = UUID.fromString(explosiveMinecart.getMetadata("TNT_MINECART").get(0).asString());
                final Player damager = Foxtrot.getInstance().getServer().getPlayer(uuid);

                if (damager == null) {
                    return;
                }

                final Player damaged = event.getPlayer();

                if (damager != damaged) {
                    event.setTrackerDamage(new ExplodeByPlayer(damaged.getName(), event.getDamage(), damager.getName()));
                }
            }
        }
    }

    public static class ExplodeByPlayer extends PlayerDamage {

        public ExplodeByPlayer(String damaged, double damage, String damager) {
            super(damaged, damage, damager);
        }

        public String getDeathMessage() {
            return (wrapName(getDamaged()) + " exploded thanks to " + wrapName(getDamager()) + ".");
        }

    }
}
