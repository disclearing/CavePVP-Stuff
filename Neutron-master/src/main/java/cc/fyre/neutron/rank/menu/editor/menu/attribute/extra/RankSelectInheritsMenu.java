package cc.fyre.neutron.rank.menu.editor.menu.attribute.extra;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.rank.Rank;
import cc.fyre.neutron.rank.comparator.RankWeightComparator;
import cc.fyre.neutron.rank.menu.editor.menu.RankModifyAttributesMenu;
import cc.fyre.neutron.util.ColorUtil;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import cc.fyre.proton.menu.buttons.BackButton;
import cc.fyre.proton.menu.pagination.PaginatedMenu;
import lombok.AllArgsConstructor;
import lombok.Getter;
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
public class RankSelectInheritsMenu extends PaginatedMenu {

    @Getter private Rank rank;
    @Getter private boolean remove;

    @Override
    public String getPrePaginatedTitle(Player player) {
        return this.remove ? "Select inherits to remove":"Select inherits to add";
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

        final List<Rank> ranks = new ArrayList<>();

        for (Rank rank : Neutron.getInstance().getRankHandler().getCache().values()) {

            if (rank.getUuid().equals(this.rank.getUuid())) {
                continue;
            }

            if (this.remove) {

                if (!rank.getInherits().contains(rank)) {
                    continue;
                }

                ranks.add(rank);
            } else {

                if (rank.getEffectiveInherits().contains(rank)) {
                    continue;
                }

                ranks.add(rank);
            }

        }

        for (Rank rank : ranks.stream().sorted(new RankWeightComparator()).collect(Collectors.toList())) {

            toReturn.put(toReturn.size(),new Button() {

                @Override
                public String getName(Player player) {
                    return rank.getFancyName();
                }

                @Override
                public List<String> getDescription(Player player) {
                    return new ArrayList<>();
                }

                @Override
                public Material getMaterial(Player player) {
                    return Material.INK_SACK;
                }

                @Override
                public byte getDamageValue(Player player) {
                    return ColorUtil.COLOR_MAP.get(rank.getColor()).getDyeData();
                }

                @Override
                public void clicked(Player player,int slot,ClickType clickType) {

                    if (remove) {
                        rank.getInherits().remove(rank);
                    } else {
                        rank.getInherits().add(rank);
                    }


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
