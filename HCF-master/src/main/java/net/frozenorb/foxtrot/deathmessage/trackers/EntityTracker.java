package net.frozenorb.foxtrot.deathmessage.trackers;

import cc.fyre.proton.util.EntityUtils;
import net.frozenorb.foxtrot.deathmessage.event.CustomPlayerDamageEvent;
import net.frozenorb.foxtrot.deathmessage.objects.MobDamage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EntityTracker implements Listener {

    @EventHandler(priority=EventPriority.LOW, ignoreCancelled=true)
    public void onCustomPlayerDamage(CustomPlayerDamageEvent event) {
        if (event.getCause() instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event.getCause();

            if (!(e.getDamager() instanceof Player) && !(e.getDamager() instanceof Arrow)) {
                event.setTrackerDamage(new EntityDamage(event.getPlayer().getName(), event.getDamage(), e.getDamager()));
            }
        }
    }

    public static class EntityDamage extends MobDamage {

        public EntityDamage(String damaged, double damage, Entity entity) {
            super(damaged, damage, entity.getType());
        }

        public String getDeathMessage() {
            String entityName = EntityUtils.getName(getMobType());

            if (entityName.equalsIgnoreCase("Wolf")) {
                entityName = "Puppy";
            }

            return (wrapName(getDamaged()) + " was slain by a " + ChatColor.RED + entityName + ChatColor.YELLOW + ".");
        }

    }

}