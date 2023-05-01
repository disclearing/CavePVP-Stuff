package cc.fyre.piston.client.listener;

import cc.fyre.piston.Piston;
import cc.fyre.piston.client.data.Version;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class VersionListener implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    private void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        int pVersion = ((CraftPlayer)player).getHandle().playerConnection.networkManager.getVersion();
        for(Version version : Version.values()) {

            if(pVersion == version.getProtocolNumber()) {
                Piston.getInstance().getClientHandler().getVersionMap().put(player.getUniqueId(), version);
            }
        }
        if(!Piston.getInstance().getClientHandler().getVersionMap().containsKey(player.getUniqueId()))
            Piston.getInstance().getClientHandler().getVersionMap().put(player.getUniqueId(), Version.UNKNOWN);

    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onPlayerQuit(PlayerQuitEvent event) {
        Piston.getInstance().getClientHandler().getVersionMap().remove(event.getPlayer().getUniqueId());
    }
}
