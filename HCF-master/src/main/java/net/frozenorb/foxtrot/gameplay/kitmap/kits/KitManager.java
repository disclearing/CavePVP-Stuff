package net.frozenorb.foxtrot.gameplay.kitmap.kits;

import cc.fyre.proton.serialization.PlayerInventorySerializer;
import net.minecraft.util.com.google.common.base.Charsets;
import net.minecraft.util.com.google.common.collect.Maps;
import net.minecraft.util.com.google.common.io.Files;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.util.SortedList;
import cc.fyre.proton.Proton;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Only gets instantiated if the server is in KitMap mode.
 */
public class KitManager {

    private static File DEFAULT_KITS_FILE = new File(Foxtrot.getInstance().getDataFolder(), "data/default-kits.json");
    private static Type DEFAULT_KITS_TYPE = new TypeToken<List<DefaultKit>>() {
    }.getType();

    private static File USER_KITS_FILE = new File(Foxtrot.getInstance().getDataFolder(), "data/user-kits.json");
    private static Type USER_KITS_TYPE = new TypeToken<Map<UUID, List<Kit>>>() {
    }.getType();

    @Getter
    private List<DefaultKit> defaultKits = new SortedList<>(Comparator.comparingInt(DefaultKit::getOrder));
    private Map<UUID, List<Kit>> userKits = Maps.newHashMap();

    @Getter
    private final Map<UUID, PlayerInventorySerializer.PlayerInventoryWrapper> storedStates = new HashMap<>();

    public KitManager() {
        loadDefaultKits();
        loadUserKits();

        // print info
        Foxtrot.getInstance().getLogger().info("- Kit Manager - Loaded " + defaultKits.size() + " default kits!");
        Foxtrot.getInstance().getLogger().info("- Kit Manager - Loaded " + userKits.size() + " user kits!");

        // We have to do this later to 'steal' priority
        Bukkit.getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
            Proton.getInstance().getCommandHandler().registerPackage(Foxtrot.getInstance(), "net.frozenorb.foxtrot.gameplay.kitmap.kits.command");
            Proton.getInstance().getCommandHandler().registerParameterType(DefaultKit.class, new DefaultKit.Type());
        }, 5L);

        Bukkit.getScheduler().runTaskTimerAsynchronously(Foxtrot.getInstance(), () -> {
            saveDefaultKits();
            saveUserKits();
        }, 20L * 60L * 2L, 20L * 60L * 2L);

        Bukkit.getPluginManager().registerEvents(new KitListener(), Foxtrot.getInstance());
    }

    private void loadDefaultKits() {
        if (DEFAULT_KITS_FILE.exists()) {
            try (Reader reader = Files.newReader(DEFAULT_KITS_FILE, Charsets.UTF_8)) {
                defaultKits = Proton.PLAIN_GSON.fromJson(reader, DEFAULT_KITS_TYPE);
            } catch (IOException e) {
                e.printStackTrace();
                Foxtrot.getInstance().getLogger().severe(ChatColor.RED + "Failed to import default-kits.json!");
            }
        }
    }

    public void saveDefaultKits() {
        try {
            Files.write(Proton.PLAIN_GSON.toJson(defaultKits, DEFAULT_KITS_TYPE), DEFAULT_KITS_FILE, Charsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            Foxtrot.getInstance().getLogger().severe(ChatColor.RED + "Failed to export default-kits.json!");
        }
    }

    private void loadUserKits() {
        if (USER_KITS_FILE.exists()) {
            try (Reader reader = Files.newReader(USER_KITS_FILE, Charsets.UTF_8)) {
                userKits = Proton.PLAIN_GSON.fromJson(reader, USER_KITS_TYPE);
            } catch (IOException e) {
                e.printStackTrace();
                Foxtrot.getInstance().getLogger().severe(ChatColor.RED + "Failed to import user-kits.json!");
            }
        }
    }

    private void saveUserKits() {
        try {
            Files.write(Proton.PLAIN_GSON.toJson(userKits, USER_KITS_TYPE), USER_KITS_FILE, Charsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            Foxtrot.getInstance().getLogger().severe(ChatColor.RED + "Failed to export user-kits.json!");
        }
    }

    public Kit getUserKit(UUID player, Kit kit) {
        if (kit == null) {
            return null;
        }

        if (userKits.containsKey(player)) {
            for (Kit userKit : userKits.get(player)) {
                if (userKit.getOriginal() == kit) {
                    return userKit;
                }
            }
        }

        return null;
    }

    public void trackUserKit(UUID player, Kit kit) {
        if (kit == null) {
            return;
        }

        if (!userKits.containsKey(player)) {
            userKits.put(player, new ArrayList<>());
        }

        userKits.get(player).add(kit);
    }

    public void deleteUserKit(UUID player, Kit kit) {
        if (kit == null) {
            return;
        }

        if (userKits.containsKey(player)) {
            userKits.get(player).remove(kit);
        }
    }

    public DefaultKit getDefaultKit(String name) {
        for (DefaultKit kit : defaultKits) {
            if (kit.getName().equalsIgnoreCase(name)) {
                return kit;
            }
        }

        return null;
    }

    public Kit getOrCreateDefaultKit(String name) {
        for (Kit kit : defaultKits) {
            if (kit.getName().equalsIgnoreCase(name)) {
                return kit;
            }
        }

        DefaultKit kit = new DefaultKit(name);
        defaultKits.add(kit);

        return kit;
    }

    public void deleteDefaultKit(DefaultKit kit) {
        defaultKits.remove(kit);
    }

    public boolean awaitingRestore(Player player) {
        return storedStates.containsKey(player.getUniqueId());
    }

    public void saveState(Player player) {
        storedStates.put(player.getUniqueId(), new PlayerInventorySerializer.PlayerInventoryWrapper(player));
    }

    public void restoreState(Player player) {
        if (storedStates.containsKey(player.getUniqueId())) {
            storedStates.get(player.getUniqueId()).apply(player);
            storedStates.remove(player.getUniqueId());
        }
    }

}
