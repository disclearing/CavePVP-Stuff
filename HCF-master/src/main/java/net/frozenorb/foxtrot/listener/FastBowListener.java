package net.frozenorb.foxtrot.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FastBowListener implements Listener {
    private Map<UUID, Long> lastFire = new HashMap<>();

    @EventHandler(priority = EventPriority.MONITOR)
    public void onProjectileLaunch(final EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }
        final Player shooter = (Player)event.getEntity();
        final Long lastFired = this.lastFire.get(shooter.getUniqueId());
        if (lastFired != null && System.currentTimeMillis() - lastFired < 500L) {
            event.setCancelled(true);
            this.lastFire.put(shooter.getUniqueId(), System.currentTimeMillis());
            return;
        }
        this.lastFire.put(shooter.getUniqueId(), System.currentTimeMillis());
    }

    @EventHandler
    public void onQuit(final PlayerQuitEvent event) {
        this.lastFire.remove(event.getPlayer().getUniqueId());
    }
}
