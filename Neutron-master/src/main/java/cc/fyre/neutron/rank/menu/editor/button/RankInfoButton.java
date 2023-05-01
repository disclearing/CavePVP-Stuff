package cc.fyre.neutron.rank.menu.editor.button;

import cc.fyre.neutron.NeutronConstants;
import cc.fyre.neutron.rank.Rank;
import cc.fyre.neutron.rank.menu.editor.menu.RankDeleteMenu;
import cc.fyre.neutron.rank.menu.editor.menu.RankModifyAttributesMenu;
import cc.fyre.neutron.util.ColorUtil;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.util.UnicodeUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xanderume@gmail (JavaProject)
 */
@AllArgsConstructor
public class RankInfoButton extends Button {

    @Getter private Rank rank;
    @Getter private Player player;

    @Override
    public String getName(Player player) {
        return this.rank.getFancyName();
    }

    @Override
    public List<String> getDescription(Player player) {

        final List<String> toReturn = new ArrayList<>();

        toReturn.add(ChatColor.GRAY + NeutronConstants.MENU_BAR);
        toReturn.add(ChatColor.GOLD + "Metadata:");
        toReturn.add(ChatColor.BLUE + " " + UnicodeUtils.ARROW_RIGHT + ChatColor.WHITE + " Weight: " + ChatColor.GOLD + this.rank.getWeight());
        toReturn.add(ChatColor.BLUE + " " + UnicodeUtils.ARROW_RIGHT + ChatColor.WHITE + " Inherits: " + ChatColor.GOLD + this.rank.getEffectiveInherits().size());
        toReturn.add(ChatColor.BLUE + " " + UnicodeUtils.ARROW_RIGHT + ChatColor.WHITE + " Permissions: " + ChatColor.GOLD + this.rank.getEffectivePermissions().size());
        toReturn.add("");
        toReturn.add(ChatColor.GOLD + "Example Prefix:");
        toReturn.add(ChatColor.WHITE + this.rank.getPrefix() + this.player.getName());
        toReturn.add("");
        toReturn.add(ChatColor.WHITE.toString() + ChatColor.GOLD + "Example Player List Prefix:");
        toReturn.add(this.rank.getColor() + this.player.getName());
        toReturn.add("");
        toReturn.add(ChatColor.GREEN.toString() + ChatColor.BOLD + "LEFT-CLICK to edit attributes");
        toReturn.add(ChatColor.AQUA.toString() + ChatColor.BOLD + "RIGHT-CLICK to show detailed info");
        toReturn.add(ChatColor.RED.toString() + ChatColor.BOLD + "SHIFT-CLICK to delete rank");
        toReturn.add(ChatColor.GRAY + NeutronConstants.MENU_BAR);

        return toReturn;
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.INK_SACK;
    }

    @Override
    public byte getDamageValue(Player player) {
        return ColorUtil.COLOR_MAP.get(this.rank.getColor()).getDyeData();
    }

    @Override
    public void clicked(Player player,int slot,ClickType clickType) {

        if (clickType.isLeftClick() && !clickType.isShiftClick()) {
            new RankModifyAttributesMenu(this.rank).openMenu(player);
        } else if (clickType.isShiftClick()) {
            new RankDeleteMenu(this.rank,player).openMenu(player);
        }

    }
}
