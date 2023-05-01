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
public class AltsMenu extends PaginatedMenu {

    @Getter private Profile profile;
    @Getter private List<Profile> alts;

    @Override
    public String getPrePaginatedTitle(Player player) {
        return this.profile.getFancyName();
    }

    @Override
    public Map<Integer,Button> getAllPagesButtons(Player player) {

        final Map<Integer,Button> toReturn = new HashMap<>();

        for (Profile alt : this.alts) {

            toReturn.put(toReturn.size(),new Button() {

                @Override
                public String getName(Player player) {
                    return alt.getFancyName();
                }

                @Override
                public List<String> getDescription(Player player) {

                    final List<String> toReturn = new ArrayList<>();

                    toReturn.add(ChatColor.GRAY + NeutronConstants.MENU_BAR);
                    toReturn.add(ChatColor.GOLD + "Siblings: " + ChatColor.WHITE + alt.getSiblings().size());
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

    @Override
    public Map<Integer,Button> getGlobalButtons(Player player) {

        final Map<Integer,Button> buttons = new HashMap<>();

        buttons.put(4,new Button() {

            @Override
            public String getName(Player player) {
                return profile.getFancyName();
            }

            @Override
            public List<String> getDescription(Player player) {

                final List<String> toReturn = new ArrayList<>();

                toReturn.add(ChatColor.GOLD + "Alts: " + ChatColor.WHITE + alts.size());
                toReturn.add(ChatColor.GOLD + "Siblings: " + ChatColor.WHITE + profile.getSiblings().size());

                return toReturn;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.SKULL_ITEM;
            }

            @Override
            public byte getDamageValue(Player player) {
                return 0;
            }

            @Override
            public void clicked(Player player,int i,ClickType clickType) {

            }

        });

        return buttons;
    }

}
