package io.kohi.kspigot.potion;

import org.bukkit.potion.PotionType;

import java.util.Map;

public class PotionMatcher
{
    private PotionType type;
    private Integer level;
    private Boolean extended;
    private Boolean splash;

    public PotionMatcher(Map conf)
    {
        if (conf.containsKey("type"))
        {
            try
            {
                type = PotionType.valueOf((String) conf.get("type"));
            }
            catch (IllegalArgumentException ex)
            {
            }
        }
        if (conf.containsKey("level"))
        {
            level = (Integer) conf.get("level");
        }
        if (conf.containsKey("extended"))
        {
            extended = (Boolean) conf.get("extended");
        }
        if (conf.containsKey("splash"))
        {
            splash = (Boolean) conf.get("splash");
        }
    }

    public boolean matches(int damage)
    {
        if (type != null && type.getDamageValue() != (damage & 15))
        {
            return false;
        }
        if (level != null && level != ((damage >> 5) & 1) + 1)
        {
            return false;
        }
        if (extended != null && extended != (((damage >> 6) & 1) == 1))
        {
            return false;
        }
        if (splash != null && splash != (((damage >> 14) & 1) == 1))
        {
            return false;
        }
        return true;
    }
}
