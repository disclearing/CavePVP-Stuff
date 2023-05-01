package cc.fyre.neutron.rank.menu.editor.menu.attribute;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.rank.Rank;
import cc.fyre.neutron.rank.menu.editor.menu.RankModifyAttributesMenu;
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
public class RankModifyPermissionsButton extends Button {

    @Getter private Rank rank;

    @Getter private static Map<UUID,Rank> addingPermission = new HashMap<>();

    @Override
    public String getName(Player player) {
        return ChatColor.GOLD + "Permissions: " + ChatColor.WHITE + this.rank.getPermissions().size() + " - (" + (this.rank.getEffectivePermissions().size() - this.rank.getPermissions().size()) + ")";
    }

    @Override
    public List<String> getDescription(Player player) {

        final List<String> toReturn = new ArrayList<>();

        toReturn.add("");
        toReturn.add(ChatColor.RED.toString() + ChatColor.BOLD + "CLICK to remove permissions");

        return toReturn;
    }

    @Override
    public Material getMaterial(Player player) {
        return Material.PAPER;
    }

    @Override
    public void clicked(Player player,int slot,ClickType clickType) {

        RankModifyAttributesMenu.getIgnoreSave().add(this.rank.getUuid());

        new RankSelectPermissionMenu(this.rank).openMenu(player);
    }

}
