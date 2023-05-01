package cc.fyre.neutron.rank.menu.editor.menu.attribute;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.rank.Rank;
import cc.fyre.neutron.rank.menu.editor.menu.RankModifyAttributesMenu;
import cc.fyre.neutron.rank.menu.editor.menu.attribute.extra.RankSelectColorMenu;
import cc.fyre.neutron.util.ColorUtil;
import cc.fyre.proton.menu.Button;
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
public class RankModifyColorButton extends Button {

    @Getter private Rank rank;

    @Override
    public String getName(Player player) {
        return ChatColor.GOLD + "Color: " + this.rank.getColor() + this.rank.getColor().name();
    }

    @Override
    public List<String> getDescription(Player player) {

        final List<String> toReturn = new ArrayList<>();

        toReturn.add("");
        toReturn.add(ChatColor.GREEN.toString() + ChatColor.BOLD + "CLICK to change color");
        toReturn.add(ChatColor.RED.toString() + ChatColor.BOLD + "RIGHT-CLICK to reset color");

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

        if (clickType.isLeftClick()) {

            RankModifyAttributesMenu.getIgnoreSave().add(this.rank.getUuid());

            new RankSelectColorMenu(this.rank).openMenu(player);
        } else if (clickType.isRightClick()) {
            this.rank.setColor(ChatColor.WHITE);
        }

    }
}
