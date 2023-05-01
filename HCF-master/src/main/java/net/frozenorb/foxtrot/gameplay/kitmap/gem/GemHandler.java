package net.frozenorb.foxtrot.gameplay.kitmap.gem;

import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.gameplay.kitmap.gem.listener.GemRewardListeners;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;

public class GemHandler {

    public static final String DOUBLE_GEM_PREFIX = "&2&lDouble Gem";

    @Getter
    private final Map<Material, Double> oreChances = new HashMap<>();

    public GemHandler() {
        Bukkit.getPluginManager().registerEvents(new GemRewardListeners(), Foxtrot.getInstance());
    }

    public void loadChances() {
        if (Foxtrot.getInstance().getConfig().contains("gems")) {
            for (String key : Foxtrot.getInstance().getConfig().getConfigurationSection("gems").getKeys(false)) {
                oreChances.put(Material.valueOf(key), Foxtrot.getInstance().getConfig().getDouble("gems." + key));
            }
        }

        oreChances.putIfAbsent(Material.STONE, 0.1);
        oreChances.putIfAbsent(Material.COAL_ORE, 0.5);
        oreChances.putIfAbsent(Material.IRON_ORE, 1.0);
        oreChances.putIfAbsent(Material.GOLD_ORE, 2.0);
        oreChances.putIfAbsent(Material.REDSTONE_ORE, 0.5);
        oreChances.putIfAbsent(Material.LAPIS_ORE, 5.0);
        oreChances.putIfAbsent(Material.DIAMOND_ORE, 5.0);
        oreChances.putIfAbsent(Material.EMERALD_ORE, 15.0);
    }

    public static boolean isDoubleGem() {
        return CustomTimerCreateCommand.getCustomTimers().containsKey(DOUBLE_GEM_PREFIX);
    }
}
