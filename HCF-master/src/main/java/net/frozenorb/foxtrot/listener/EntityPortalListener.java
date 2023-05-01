package net.frozenorb.foxtrot.listener;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEvent;

public class EntityPortalListener implements Listener {

    @EventHandler(ignoreCancelled = true)
    public void onPortal(EntityPortalEvent event) {
        if (event.getEntityType() == EntityType.PLAYER) return;

        event.getEntity().remove();
    }
}
