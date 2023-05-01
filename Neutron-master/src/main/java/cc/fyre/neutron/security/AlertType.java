package cc.fyre.neutron.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;

@AllArgsConstructor
public enum AlertType {
    DROPPED_ITEMS("&eDropped Items", Material.FEATHER),
    PICKED_UP_ITEMS("&6Picked Up Items", Material.HOPPER),
    UNPUNISHMENTS("&cUnpunishments", Material.REDSTONE_BLOCK),
    PUNISHMENTS("&aPunishments", Material.EMERALD_BLOCK),
    TELEPORT_HERE("&5Teleport", Material.EYE_OF_ENDER),
    GRANTS("&a&lGrants", Material.EMERALD),
    INVENTORY_SEE("&dInventory See", Material.ENDER_CHEST),
    CHESTS("&bChest Actions", Material.CHEST),
    REVIVES_ROLLBACKS("&2Revives/Rollbacks", Material.EMPTY_MAP),
    HEAD_STAFF_CORE_PROTECT("&4Viewing Head-Staff Logs", Material.PAPER),
    IP_CHANGE("&4&lIP Change", Material.COMMAND_MINECART),
    MIDDLE_CLICK("&9&lMiddle Click", Material.STONE_BUTTON),
    FACTION_FORCED("&3Forced Faction Actions", Material.DIAMOND_SWORD);

    @Getter @Setter String displayName;
    @Getter @Setter Material material;
}
