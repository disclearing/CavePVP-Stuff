package cc.fyre.neutron.profile.menu;

import cc.fyre.neutron.NeutronConstants;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.neutron.profile.attributes.punishment.comparator.PunishmentDateComparator;
import cc.fyre.neutron.profile.attributes.punishment.impl.Punishment;
import cc.fyre.proton.Proton;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.pagination.PaginatedMenu;
import cc.fyre.proton.util.TimeUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.*;

@AllArgsConstructor
public class WarnsMenu extends PaginatedMenu {

    @Getter
    private Profile profile;
    @Getter
    private List<Punishment> warns;

    @Override
    public boolean isAutoUpdate() {
        return true;
    }

    @Override
    public String getPrePaginatedTitle(Player player) {
        return this.profile.getFancyName();
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {

        final Map<Integer, Button> toReturn = new HashMap<>();

        this.warns.stream().sorted(new PunishmentDateComparator().reversed()).forEach(punishment -> toReturn.put(toReturn.size(), new Button() {

            @Override
            public String getName(Player player) {
                return ChatColor.RED + TimeUtils.formatIntoCalendarString(new Date(punishment.getExecutedAt()));
            }

            @Override
            public List<String> getDescription(Player player) {
                final List<String> toReturn = new ArrayList<>();

                toReturn.add(ChatColor.GRAY + NeutronConstants.MENU_BAR);
                toReturn.add(ChatColor.YELLOW + "By: " + Proton.getInstance().getUuidCache().name(punishment.getExecutor()));
                toReturn.add(ChatColor.YELLOW + "Silent: " + ChatColor.RED + (punishment.getExecutedSilent() ? "Yes" : "No"));
                toReturn.add(ChatColor.YELLOW + "Reason: " + ChatColor.RED + punishment.getExecutedReason());
                toReturn.add(ChatColor.GRAY + NeutronConstants.MENU_BAR);

                return toReturn;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.WOOL;
            }

            @Override
            public byte getDamageValue(Player player) {
                return DyeColor.LIME.getWoolData();
            }

            @Override
            public void clicked(Player player, int i, ClickType clickType) {
            }
        }));

        return toReturn;
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        return null;
    }
}
