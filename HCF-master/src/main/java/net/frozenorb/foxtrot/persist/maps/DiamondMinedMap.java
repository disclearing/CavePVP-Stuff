package net.frozenorb.foxtrot.persist.maps;

import net.frozenorb.foxtrot.persist.PersistMap;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class DiamondMinedMap extends PersistMap<Integer> {

    public DiamondMinedMap() {
        super("DiamondMined", "MiningStats.Diamond");
    }

    @Override
    public String getRedisValue(Integer kills) {
        return (String.valueOf(kills));
    }

    @Override
    public Integer getJavaObject(String str) {
        return (Integer.parseInt(str));
    }

    @Override
    public Object getMongoValue(Integer mined) {
        return (mined);
    }

    public int getMined(UUID check) {
        return (contains(check) ? getValue(check) : 0);
    }

    public void setMined(Player update, int mined) {
        updateValueAsync(update.getUniqueId(), mined);

        if (mined == 50) {
            update.sendMessage(getMessage("Haste III", 50));
        } else if (mined == 100) {
            update.sendMessage(getMessage("Speed I", 100));
        } else if (mined == 250) {
            update.sendMessage(getMessage("Speed II", 250));
        } else if (mined == 500) {
            update.sendMessage(getMessage("Permanent Invisibility", 500));
        } else if (mined == 750) {
            update.sendMessage(getMessage("Resistance I", 750));
        } else if (mined == 1000) {
            update.sendMessage(getMessage("Resistance II", 1000));
        } else if (mined == 1500) {
            update.sendMessage(getMessage("Regeneration I", 1500));
        }
    }

    public String getMessage( String buff, int amount ) {
        return ChatColor.YELLOW + "You have mined " + ChatColor.RED + amount + ChatColor.YELLOW + " Diamond Ore. You now have " + ChatColor.RED + buff + ChatColor.YELLOW + " in miner kit!";
    }

}