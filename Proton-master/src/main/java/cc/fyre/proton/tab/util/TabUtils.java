package cc.fyre.proton.tab.util;

import cc.fyre.proton.Proton;
import lombok.Getter;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class TabUtils {

    @Getter private static Map<String,GameProfile> cache = new ConcurrentHashMap<>();

    public TabUtils() {
    }

    public static boolean is18(Player player) {
        return ((CraftPlayer)player).getHandle().playerConnection.networkManager.getVersion() > 20;
    }

    public static GameProfile getOrCreateProfile(String name, UUID id) {

        GameProfile player = cache.get(name);

        if (player == null) {
            player = new GameProfile(id, name);
            player.getProperties().putAll(Proton.getInstance().getTabHandler().getDefaultPropertyMap());
            cache.put(name, player);
        }

        return player;
    }

    public static GameProfile getOrCreateProfile(String name) {
        return getOrCreateProfile(name, new UUID(ThreadLocalRandom.current().nextLong(),ThreadLocalRandom.current().nextLong()));
    }


}
