package cc.fyre.piston.client.listener;

import cc.fyre.piston.Piston;
import cc.fyre.piston.client.data.Client;
import cc.fyre.proton.Proton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRegisterChannelEvent;

public class ClientListener implements Listener {
    @EventHandler(priority = EventPriority.LOWEST)
    private void onRegisterChannel(PlayerRegisterChannelEvent event) {
        String channel = event.getChannel();
        for(Client client : Client.values()) {
            if(client.getBrand() != null && client.getChannel().equalsIgnoreCase(channel)) {
                Piston.getInstance().getClientHandler().getClientMap().put(event.getPlayer().getUniqueId(), client);
                return;
            }
        }
        if(!Piston.getInstance().getClientHandler().getClientMap().containsKey(event.getPlayer().getUniqueId()))
            Piston.getInstance().getClientHandler().getClientMap().put(event.getPlayer().getUniqueId(), Client.VANILLA);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onPlayerQuit(PlayerQuitEvent event) {
        Piston.getInstance().getClientHandler().getClientMap().remove(event.getPlayer().getUniqueId());
    }


}
