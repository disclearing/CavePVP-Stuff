package cc.fyre.piston.client.listener;

import cc.fyre.piston.Piston;
import cc.fyre.piston.client.data.Client;
import cc.fyre.universe.Universe;
import cc.fyre.universe.UniverseAPI;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.UnsupportedEncodingException;

public class ClientBrandListener implements PluginMessageListener {
    @Override
    public void onPluginMessageReceived(String channel, Player p, byte[] msg) {
        try {
            String brand=new String(msg, "UTF-8").substring(0);


            for(Client client : Client.values()) {

                if(client.getBrand() != null && client.getBrand().equalsIgnoreCase(brand)) {
                    Piston.getInstance().getClientHandler().getClientMap().put(p.getUniqueId(), client);
                    return;
                }
            }
        } catch (UnsupportedEncodingException e) {
           //
        }
    }
}