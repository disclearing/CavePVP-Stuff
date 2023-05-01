package cc.fyre.universe.listener;

import cc.fyre.universe.Universe;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

/**
 * @author xanderume@gmail (JavaProject)
 */
public class MaintenanceListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPostLogin(PostLoginEvent event) {

        if (!Universe.getInstance().getUniverseHandler().isMaintenanceMode()) {
            return;
        }

        if (Universe.getInstance().getUniverseHandler().getMaintenanceList().contains(event.getPlayer().getUniqueId())) {
            return;
        }

        //TODO: An existing connection was forcibly closed by the remote host

        event.getPlayer().disconnect(ChatColor.RED + "The server is currently in maintenance mode.\nFor more info follow us on twitter.");
    }

}
