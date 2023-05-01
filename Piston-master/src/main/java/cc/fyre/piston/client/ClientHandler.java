package cc.fyre.piston.client;

import cc.fyre.piston.Piston;
import cc.fyre.piston.client.data.Client;
import cc.fyre.piston.client.data.Version;
import cc.fyre.piston.client.listener.ClientListener;
import cc.fyre.piston.client.listener.VersionListener;
import com.google.common.collect.Maps;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

import java.util.Map;
import java.util.UUID;

public class ClientHandler {
    @Getter private Map<UUID, Client> clientMap = Maps.newHashMap();
    @Getter private Map<UUID, Version> versionMap = Maps.newHashMap();


    public ClientHandler() {
       PluginManager pm = Piston.getInstance().getServer().getPluginManager();
       pm.registerEvents(new ClientListener(), Piston.getInstance());
       pm.registerEvents(new VersionListener(), Piston.getInstance());
    }

    public Client getPlayerClient(Player player) {
        return clientMap.get(player.getUniqueId());
    }
    public Version getPlayerVerision(Player player) {
        return versionMap.get(player.getUniqueId());
    }




}
