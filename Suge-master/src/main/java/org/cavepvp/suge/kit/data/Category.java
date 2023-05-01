package org.cavepvp.suge.kit.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.cavepvp.suge.Suge;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
public enum Category {
    OP("&4&lOP Kits", "OP", Material.REDSTONE_BLOCK, (byte) 0, (byte) 14, ChatColor.DARK_RED, 12),
    ULTIMATE("&c&lUltimate Kits", "Ultimate", Material.DIAMOND_CHESTPLATE, (byte) 0, (byte) 14, ChatColor.RED, 13),
    NORMAL("&5&lNormal Kits", "Normal", Material.DIAMOND_PICKAXE, (byte) 0, (byte) 10, ChatColor.DARK_PURPLE, 14),
    CLASSES("&9&lArmor Classes Kits", "Classes", Material.FIREBALL, (byte) 0, (byte) 11, ChatColor.BLUE, 15),
    FREE("&a&lFree Kits", "Free", Material.GRASS, (byte) 0, (byte) 5, ChatColor.GREEN, 16),
    NONE("", "", Material.AIR, (byte) 0, (byte) 0, ChatColor.WHITE, 0);

    @Getter String displayName;
    @Getter String flatName;
    @Getter Material displayMaterial;
    @Getter byte data;
    @Getter byte paneData;
    @Getter ChatColor primaryColor;
    @Getter int slot;

    public List<Kit> findKits() {
        return Suge.getInstance().getKitHandler().findKitsByCategory(this);
    }

}
