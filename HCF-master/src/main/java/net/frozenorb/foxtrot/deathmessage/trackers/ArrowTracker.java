package net.frozenorb.foxtrot.deathmessage.trackers;

import cc.fyre.proton.util.EntityUtils;
import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.deathmessage.event.CustomPlayerDamageEvent;
import net.frozenorb.foxtrot.deathmessage.objects.Damage;
import net.frozenorb.foxtrot.deathmessage.objects.MobDamage;
import net.frozenorb.foxtrot.deathmessage.objects.PlayerDamage;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

public class ArrowTracker implements Listener {

    @EventHandler
    public void onEntityShootBow(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player) {
            event.getProjectile().setMetadata("ShotFromDistance", new FixedMetadataValue(Foxtrot.getInstance(), event.getProjectile().getLocation()));
        }
    }

    @EventHandler(priority=EventPriority.LOW, ignoreCancelled=true)
    public void onCustomPlayerDamage(CustomPlayerDamageEvent event) {
        if (event.getCause() instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent entityDamageByEntityEvent = (EntityDamageByEntityEvent) event.getCause();

            if (entityDamageByEntityEvent.getDamager() instanceof Arrow) {
                Arrow arrow = (Arrow) entityDamageByEntityEvent.getDamager();

                if (arrow.getShooter() instanceof Player) {
                    Player shooter = (Player) arrow.getShooter();

                    for (MetadataValue value : arrow.getMetadata("ShotFromDistance")) {
                        Location shotFrom = (Location) value.value();
                        double distance = shotFrom.distance(event.getPlayer().getLocation());
                        event.setTrackerDamage(new ArrowDamageByPlayer(event.getPlayer().getName(), event.getDamage(), shooter.getName(), shotFrom, distance));
                    }
                } else if (arrow.getShooter() instanceof Entity) {
                    event.setTrackerDamage(new ArrowDamageByMob(event.getPlayer().getName(), event.getDamage(), (Entity) arrow.getShooter()));
                } else {
                    event.setTrackerDamage(new ArrowDamage(event.getPlayer().getName(), event.getDamage()));
                }
            }
        }
    }

    public static class ArrowDamage extends Damage {

        public ArrowDamage(String damaged, double damage) {
            super(damaged, damage);
        }

        public String getDeathMessage() {
            return (wrapName(getDamaged()) + " was shot.");
        }

    }

    public static class ArrowDamageByPlayer extends PlayerDamage {

        @Getter private Location shotFrom;
        @Getter private double distance;

        public ArrowDamageByPlayer(String damaged, double damage, String damager, Location shotFrom, double distance) {
            super(damaged, damage, damager);
            this.shotFrom = shotFrom;
            this.distance = distance;
        }

        public String getDeathMessage() {
            return (wrapName(getDamaged()) + " was shot by " + wrapName(getDamager()) + " from " + ChatColor.BLUE + (int) distance + " blocks" + ChatColor.YELLOW + ".");
        }

    }

    public static class ArrowDamageByMob extends MobDamage {

        public ArrowDamageByMob(String damaged, double damage, Entity damager) {
            super(damaged, damage, damager.getType());
        }

        public String getDeathMessage() {
            return (wrapName(getDamaged()) + " was shot by a " + ChatColor.RED + EntityUtils.getName(getMobType()) + ChatColor.YELLOW + ".");
        }

    }

}