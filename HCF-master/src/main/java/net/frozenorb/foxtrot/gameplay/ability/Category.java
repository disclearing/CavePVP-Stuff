package net.frozenorb.foxtrot.gameplay.ability;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;

@AllArgsConstructor
public enum Category {
    KIT_MAP("", "", Material.AIR, (byte) 0, 0),
    PORTABLE_BARD("", "", Material.AIR, (byte) 0, 0),
    PARTNER_CRATE(ChatColor.translate("&a&lPartner Items"), "Partner Items", Material.EYE_OF_ENDER, (byte) 5, 11),
    AIRDROPS(ChatColor.translate("&b&lAirdrop"), "Airdrop", Material.DROPPER, (byte) 3, 12),
    SEASONAL_CRATE(ChatColor.translate("&4&l3.0 Crate"), "3.0 Crate", Material.TRIPWIRE_HOOK, (byte) 2, 14),
    SEASONAL_LOOTBOX(ChatColor.translate("&c&lIndependence &b&lDay &f&lLootbox"), "Seasonal Lootbox", Material.CHEST, (byte) 2, 15),
    TREASURE_CHEST(ChatColor.translate("&e&lTreasure Chest"), "Treasure Chest", Material.ENDER_CHEST, (byte) 13, 16),
    ALL(ChatColor.translate("&4&lView All"), "View All", Material.NETHER_STAR, (byte) 0, 13);

    @Getter String displayName;
    @Getter String simpleName;
    @Getter Material displayItem;
    @Getter byte glassData;
    @Getter int slot;

    public boolean isCategory(Ability ability, Category category) {

        if (ability.getCategory() == category) {
            return true;
        }

        return category == ALL;
    }

}
