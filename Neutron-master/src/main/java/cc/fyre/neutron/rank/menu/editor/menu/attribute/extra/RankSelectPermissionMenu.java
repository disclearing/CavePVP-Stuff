package cc.fyre.neutron.rank.menu.editor.menu.attribute.extra;

import cc.fyre.neutron.rank.Rank;
import cc.fyre.neutron.rank.menu.editor.menu.RankModifyAttributesMenu;
import cc.fyre.neutron.util.ColorUtil;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import cc.fyre.proton.menu.buttons.BackButton;
import cc.fyre.proton.menu.pagination.PaginatedMenu;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author xanderume@gmail (JavaProject)
 */
@AllArgsConstructor
public class RankSelectPermissionMenu extends PaginatedMenu {

    @Getter private Rank rank;

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "Permissions";
    }

    @Override
    public boolean isUpdateAfterClick() {
        return true;
    }

    @Override
    public Map<Integer,Button> getGlobalButtons(Player player) {

        final Map<Integer,Button> toReturn = new HashMap<>();

        toReturn.put(4,new BackButton(new RankModifyAttributesMenu(this.rank)));

        return toReturn;
    }

    @Override
    public Map<Integer,Button> getAllPagesButtons(Player player) {

        final Map<Integer,Button> toReturn = new HashMap<>();

        for (String permission : this.rank.getPermissions().stream().sorted(String::compareTo).collect(Collectors.toList())) {

            toReturn.put(toReturn.size(),new Button() {

                @Override
                public String getName(Player player) {
                    return ChatColor.GREEN + permission;
                }

                @Override
                public List<String> getDescription(Player player) {
                    return new ArrayList<>();
                }

                @Override
                public Material getMaterial(Player player) {
                    return Material.MAP;
                }

                @Override
                public void clicked(Player player,int slot,ClickType clickType) {
                    rank.getPermissions().remove(permission);
                    rank.save();
                }

            });

        }

        return toReturn;
    }

    @Override
    public void onClose(Player player) {
        this.rank.save();
    }

}
