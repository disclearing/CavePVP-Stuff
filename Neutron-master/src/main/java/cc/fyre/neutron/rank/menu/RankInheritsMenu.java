package cc.fyre.neutron.rank.menu;

import cc.fyre.neutron.NeutronConstants;
import cc.fyre.neutron.rank.Rank;
import cc.fyre.neutron.rank.comparator.RankDateComparator;
import cc.fyre.neutron.rank.comparator.RankWeightComparator;
import cc.fyre.neutron.util.ColorUtil;
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
import java.util.stream.Collectors;

@AllArgsConstructor
public class RankInheritsMenu extends PaginatedMenu {

    @Getter private Rank rank;
    @Getter private boolean effective;

    @Override
    public boolean isAutoUpdate() {
        return true;
    }

    @Override
    public int getMaxItemsPerPage(Player player) {
        return 27;
    }

    @Override
    public String getPrePaginatedTitle(Player player) {
        return this.rank.getFancyName();
    }

    @Override
    public Map<Integer,Button> getAllPagesButtons(Player player) {

        final Map<Integer,Button> toReturn = new HashMap<>();

        for (Rank rank : this.getInherits().stream().sorted(new RankWeightComparator().reversed().thenComparing(new RankDateComparator())).collect(Collectors.toList())) {

            toReturn.put(toReturn.size(),new Button() {

                @Override
                public String getName(Player player) {
                    return ChatColor.RED + rank.getFancyName();
                }

                @Override
                public List<String> getDescription(Player player) {
                    return null;
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

                }

            });
        }

        return toReturn;
    }

    @Override
    public Map<Integer,Button> getGlobalButtons(Player player) {

        final Map<Integer,Button> toReturn = new HashMap<>();

        toReturn.put(4,new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.RED + "Filter";
            }

            @Override
            public List<String> getDescription(Player player) {

                final List<String> toReturn = new ArrayList<>();

                toReturn.add(ChatColor.GRAY + NeutronConstants.MENU_BAR);
                toReturn.add((effective ? ChatColor.GRAY:ChatColor.GREEN) + " » Inherits");
                toReturn.add((!effective ? ChatColor.GRAY:ChatColor.GREEN) + " » Effective Inherits");
                toReturn.add(ChatColor.GRAY + NeutronConstants.MENU_BAR);

                return toReturn;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.HOPPER;
            }

            @Override
            public byte getDamageValue(Player player) {
                return 0;
            }

            @Override
            public void clicked(Player player,int i,ClickType clickType) {
                effective = !effective;
            }
        });

        return toReturn;
    }

    public List<Rank> getInherits() {

        final List<Rank> toReturn = new ArrayList<>();

        if (!this.effective) {
            return this.rank.getInherits();
        }

        for (Rank effectiveInherit : this.rank.getEffectiveInherits()) {

            if (this.rank.getInherits().contains(effectiveInherit)) {
                continue;
            }

            toReturn.add(effectiveInherit);
        }

        return toReturn;
    }
}
