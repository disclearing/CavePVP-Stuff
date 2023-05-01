package net.frozenorb.foxtrot.listener;

import java.util.Random;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;

public class ArmorDamageListener implements Listener {

    private static final Random random = new Random();

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onItemDamage(PlayerItemDamageEvent e) {

        if (30 < random.nextInt(100)) {
            e.setCancelled(true);
        }
        
    }
}
