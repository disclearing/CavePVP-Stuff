package net.frozenorb.foxtrot.team.menu.roster;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import cc.fyre.proton.menu.pagination.PaginatedMenu;
import cc.fyre.proton.util.ItemBuilder;
import cc.fyre.proton.util.UUIDUtils;
import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.team.Role;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@AllArgsConstructor
public class TeamRosterMenu extends PaginatedMenu {
    private Team team;

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "Roster";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        final Map<Integer, Button> toReturn = new HashMap<>();

        for (Map.Entry<UUID, Role> entry : team.getRoster().entrySet()) {
            String theName = UUIDUtils.name(entry.getKey());

            toReturn.put(toReturn.size(), new Button() {
                @Override
                public String getName(Player player) {
                    return ChatColor.DARK_RED + ChatColor.BOLD.toString() + theName;
                }

                @Override
                public List<String> getDescription(Player player) {
                    final List<String> toReturn = new ArrayList<>();

                    toReturn.add("");
                    toReturn.add(ChatColor.translate("&4&l┃ &fRole: &c" + entry.getValue().getRoleName()));
                    toReturn.add("");
                    toReturn.add(ChatColor.GREEN + "Click to manage this player's roster data");

                    return toReturn;
                }

                @Override
                public Material getMaterial(Player player) {
                    return Material.SKULL_ITEM;
                }

                @Override
                public byte getDamageValue(Player player) {
                    return (byte) 3;
                }

                @Override
                public ItemStack getButtonItem(Player player) {
                    return ItemBuilder.of(this.getMaterial(player)).name(this.getName(player)).data(this.getDamageValue(player)).setLore(this.getDescription(player)).skull(theName).build();
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType) {
                    new TeamManageRosterMenu(team, entry.getKey()).openMenu(player);
                }
            });
        }

        return toReturn;
    }

    @Override
    public boolean isAutoUpdate() {
        return true;
    }
}
