package cc.fyre.neutron.profile.menu;

import cc.fyre.neutron.NeutronConstants;
import cc.fyre.neutron.profile.Profile;
import lombok.AllArgsConstructor;
import lombok.Getter;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.pagination.PaginatedMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class SiblingsMenu extends PaginatedMenu {

    @Getter private Profile profile;
    @Getter private List<Profile> siblings;

    @Override
    public String getPrePaginatedTitle(Player player) {
        return profile.getFancyName();
    }

    @Override
    public Map<Integer,Button> getAllPagesButtons(Player player) {

        final Map<Integer,Button> toReturn = new HashMap<>();

        for (Profile sibling : this.siblings) {

            toReturn.put(toReturn.size(),new Button() {

                @Override
                public String getName(Player player) {
                    return sibling.getFancyName();
                }

                @Override
                public List<String> getDescription(Player player) {

                    final List<String> toReturn = new ArrayList<>();

                    toReturn.add(ChatColor.GRAY + NeutronConstants.MENU_BAR);
                    toReturn.add(ChatColor.GOLD + "Siblings: " + ChatColor.WHITE + sibling.getSiblings().size());
                    toReturn.add(ChatColor.GRAY + NeutronConstants.MENU_BAR);

                    return toReturn;
                }

                @Override
                public Material getMaterial(Player player) {
                    return Material.SKULL_ITEM;
                }

                @Override
                public byte getDamageValue(Player player) {
                    return 3;
                }

                @Override
                public void clicked(Player player,int i,ClickType clickType) {

                }
            });

        }

        return toReturn;
    }

}
