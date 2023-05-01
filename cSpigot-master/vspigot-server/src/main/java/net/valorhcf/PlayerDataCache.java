package net.valorhcf;

import net.minecraft.server.MinecraftServer;

import java.util.LinkedHashMap;
import java.util.Map;

public class PlayerDataCache<K, V> extends LinkedHashMap<K, V>{

    private static final long serialVersionUID = 5272337123874421616L;

    public PlayerDataCache() {
        super(100, 0.75F, true);
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K,V> eldest) {
        return this.size() > MinecraftServer.getServer().getPlayerList().getPlayerCount();
    }
}
