package net.frozenorb.foxtrot.gameplay.extra.runningin;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.pagination.PaginatedMenu;
import cc.fyre.proton.util.TimeUtils;
import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.dtr.DTRHandler;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class RunningInMenu extends PaginatedMenu {
    private List<Team> getSortedList;

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "Running In";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        final Map<Integer, Button> toReturn = new HashMap<>();

        for (Team team : getSortedList) {

            if (team == null) {
                continue;
            }

            toReturn.put(toReturn.size(), new Button() {
                @Override
                public String getName(Player player) {
                    return ChatColor.DARK_RED + ChatColor.BOLD.toString() + team.getName();
                }

                @Override
                public List<String> getDescription(Player player) {
                    final List<String> lore = new ArrayList<>();

                    lore.add(ChatColor.translate("&4&l┃ &fHQ: &c" + team.getHQ().getBlockX() + ", " + team.getHQ().getBlockZ()));
                    if (player.getWorld().getName().equalsIgnoreCase(team.getHQ().getWorld().getName())) {
                        lore.add(ChatColor.translate("&4&l┃ &fDistance: &c" + Math.ceil(team.getHQ().distance(player.getLocation())) + " blocks"));
                    }
                    lore.add(ChatColor.translate("&4&l┃ &fMembers Online: &c" + team.getOnlineMembers().size() + "/" + team.getMembers().size()));

                    String dtrFormat = team.formatDTR();

                    if (Foxtrot.getInstance().getDTRDisplayMap().isHearts(player.getUniqueId())) {
                        int currentHearts = (int) Math.ceil(team.getDTR());

                        dtrFormat = team.getDTRColor().toString() + currentHearts + "❤" + team.getDTRSuffix();
                    }

                    lore.add(ChatColor.translate("&4&l┃ &fDeaths Until Raidable: &c" + dtrFormat));
                    if (DTRHandler.isOnCooldown(team)) {
                        lore.add(ChatColor.translate("&4&l┃ &fRegen: &c" + TimeUtils.formatIntoDetailedString(((int) (team.getDTRCooldown() - System.currentTimeMillis()) / 1000))));
                    }
                    lore.add("");
                    lore.add(ChatColor.RED + "Click to focus this faction.");
                    return lore;
                }

                @Override
                public Material getMaterial(Player player) {
                    return Material.PAPER;
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType) {
                    player.closeInventory();
                    player.chat("/f focus " + team.getName() );
                }
            });
        }

        return toReturn;
    }
}
