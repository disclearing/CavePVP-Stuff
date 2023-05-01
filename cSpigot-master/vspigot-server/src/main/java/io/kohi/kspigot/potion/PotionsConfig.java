package io.kohi.kspigot.potion;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PotionsConfig
{
    private static final YamlConfiguration conf = YamlConfiguration.loadConfiguration(new File("config/server", "potions.yml")); // MineHQ
    private static final List<PotionMatcher> disableBrewing = new ArrayList<PotionMatcher>();
    private static final Map<Integer, Boolean> disableBrewingCache = new HashMap<Integer, Boolean>();

    static
    {
        List<?> disable = conf.getList("disable-brewing");
        if (disable != null)
        {
            for (Object obj : disable)
            {
                if (obj instanceof Map)
                {
                    disableBrewing.add(new PotionMatcher((Map) obj));
                }
            }
        }
    }

    public static boolean isBrewingDisabled(int damage)
    {
        Boolean cached = disableBrewingCache.get(damage);
        if (cached != null)
        {
            return cached;
        }
        for (PotionMatcher potion : disableBrewing)
        {
            if (potion.matches(damage))
            {
                disableBrewingCache.put(damage, true);
                return true;
            }
        }
        disableBrewingCache.put(damage, false);
        return false;
    }
}
