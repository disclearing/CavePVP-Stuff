package net.frozenorb.foxtrot.deathmessage.util;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

public class MobUtil {

    public static String getItemName(ItemStack itemStack, boolean stripColor) {
        if (itemStack.getItemMeta().hasDisplayName()) {
            String displayName = itemStack.getItemMeta().getDisplayName();
            return stripColor ? ChatColor.stripColor(displayName) : displayName;
        }

        return (WordUtils.capitalizeFully(itemStack.getType().name().replace('_', ' ')));
    }

}