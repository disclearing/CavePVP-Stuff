package cc.fyre.neutron.rank.menu;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.NeutronConstants;
import cc.fyre.neutron.rank.Rank;
import cc.fyre.neutron.util.ColorUtil;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import org.bukkit.ChatColor;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RankListMenu extends Menu {

    @Override
    public boolean isAutoUpdate() {
        return true;
    }

    @Override
    public String getTitle(Player player) {
        return ChatColor.RED + "Ranks";
    }

    @Override
    public Map<Integer,Button> getButtons(Player player) {

        final Map<Integer,Button> toReturn = new HashMap<>();

        for (Rank rank : Neutron.getInstance().getRankHandler().getSortedValueCache()) {

            toReturn.put(toReturn.size(),new Button() {

                @Override
                public String getName(Player player) {
                    return rank.getFancyName();
                }

                @Override
                public List<String> getDescription(Player player) {

                    final List<String> toReturn = new ArrayList<>();

                    toReturn.add(ChatColor.GRAY + NeutronConstants.MENU_BAR);
                    toReturn.add(ChatColor.GOLD + "Color: " + rank.getColor() + rank.getColor().name());
                    toReturn.add(ChatColor.GOLD + "Weight: " + ChatColor.WHITE + rank.getWeight());
                    toReturn.add(ChatColor.GOLD + "Prefix: " + rank.getPrefix());
                    toReturn.add(ChatColor.GOLD + "Inherits: " + ChatColor.WHITE + rank.getInherits().size() + " " + ChatColor.GRAY + "(" + (rank.getEffectiveInherits().size() - rank.getInherits().size()) + ")");
                    toReturn.add(ChatColor.GOLD + "Permissions: " + ChatColor.WHITE + rank.getPermissions().size() + " " + ChatColor.GRAY + "(" + (rank.getEffectivePermissions().size() - rank.getPermissions().size()) + ")");

                    if (!rank.getEffectivePermissions().isEmpty() || !rank.getEffectiveInherits().isEmpty()) {
                        toReturn.add("");
                    }

                    if (!rank.getEffectivePermissions().isEmpty()) {
                        toReturn.add(ChatColor.GRAY + "Left click " + ChatColor.WHITE + "->" + ChatColor.GRAY + " view permissions.");
                    }

                    if (!rank.getEffectiveInherits().isEmpty()) {
                        toReturn.add(ChatColor.GRAY + "Right click " + ChatColor.WHITE + "->" + ChatColor.GRAY + " view inherits.");
                    }

                    toReturn.add(ChatColor.GRAY + NeutronConstants.MENU_BAR);

                    return toReturn;
                }

                @Override
                public Material getMaterial(Player player) {
                    return Material.WOOL;
                }

                @Override
                public byte getDamageValue(Player player) {
                    return ColorUtil.COLOR_MAP.get(rank.getColor()).getWoolData();
                }

                @Override
                public void clicked(Player player,int i,ClickType clickType) {

                    if (clickType.isLeftClick()) {
                        new RankPermissionsMenu(rank,clickType.isShiftClick()).openMenu(player);
                    } else if (clickType.isRightClick()) {
                        new RankInheritsMenu(rank,clickType.isShiftClick()).openMenu(player);
                    }

                }
            });

        }

        return toReturn;
    }
}
