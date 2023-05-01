package cc.fyre.neutron.rank.menu.editor.menu.attribute;

import cc.fyre.neutron.rank.Rank;
import cc.fyre.neutron.rank.menu.editor.menu.attribute.extra.RankSelectInheritsMenu;
import cc.fyre.neutron.rank.menu.editor.menu.attribute.extra.RankSelectPermissionMenu;
import cc.fyre.proton.menu.Button;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.*;

/**
 * @author xanderume@gmail (JavaProject)
 */
@AllArgsConstructor
public class RankModifyInheritsButton extends Button {

    @Getter private Rank rank;

    @Override
    public String getName(Player player) {
        return ChatColor.GOLD + "Inherits: " + ChatColor.WHITE + this.rank.getInherits().size() + " - (" + (this.rank.getEffectiveInherits().size() - this.rank.getInherits().size()) + ")";
    }

    @Override
    public List<String> getDescription(Player player) {

        final List<String> toReturn = new ArrayList<>();

        toReturn.add("");
        toReturn.add(ChatColor.GREEN.toString() + ChatColor.BOLD + "CLICK to add inherits");
        toReturn.add(ChatColor.RED.toString() + ChatColor.BOLD + "RIGHT-CLICK to remove inherits");

        return toReturn;
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.MAP;
    }

    @Override
    public void clicked(Player player,int slot,ClickType clickType) {
        //new RankSelectInheritsMenu(this.rank,!clickType.isLeftClick()).openMenu(player);
    }

}