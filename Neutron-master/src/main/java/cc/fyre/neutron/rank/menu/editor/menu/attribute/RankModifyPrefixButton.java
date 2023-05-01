package cc.fyre.neutron.rank.menu.editor.menu.attribute;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.rank.Rank;
import cc.fyre.neutron.rank.menu.editor.menu.RankModifyAttributesMenu;
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
public class RankModifyPrefixButton extends Button {

    @Getter private static Map<UUID,Rank> modifyingPrefix = new HashMap<>();

    @Getter private Rank rank;

    @Override
    public String getName(Player player) {
        return ChatColor.GOLD + "Prefix: " + ((this.rank.getPrefix() == null || this.rank.getPrefix().equals("")) ? ChatColor.WHITE + "None":this.rank.getPrefix());
    }

    @Override
    public List<String> getDescription(Player player) {

        final List<String> toReturn = new ArrayList<>();

        toReturn.add("");
        toReturn.add(ChatColor.GREEN.toString() + ChatColor.BOLD + "CLICK to change prefix");
        toReturn.add(ChatColor.RED.toString() + ChatColor.BOLD + "RIGHT-CLICK to reset prefix");

        return toReturn;
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.SIGN;
    }

    @Override
    public void clicked(Player player,int slot,ClickType clickType) {

        if (clickType.isLeftClick()) {

            RankModifyAttributesMenu.getIgnoreSave().add(this.rank.getUuid());

            modifyingPrefix.put(player.getUniqueId(),this.rank);

            player.closeInventory();
            player.sendMessage(ChatColor.GREEN + "Provide a prefix:");
            player.sendMessage(" ");

        } else if (clickType.isRightClick()) {
            this.rank.setPrefix("");
        }

    }
}
