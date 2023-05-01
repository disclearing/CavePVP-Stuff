package cc.fyre.neutron.listener;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.net.InetAddress;

public class LanguageListener implements Listener {

    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        final InetAddress ip = player.getAddress().getAddress();

        if (ip == null) {
            return;
        }

        if (ip.isAnyLocalAddress() || ip.isLoopbackAddress()) {
            return;
        }
    }

}
