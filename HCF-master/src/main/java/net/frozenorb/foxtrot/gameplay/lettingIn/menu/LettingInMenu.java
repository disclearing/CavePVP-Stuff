package net.frozenorb.foxtrot.gameplay.lettingIn.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.pagination.PaginatedMenu;
import cc.fyre.proton.util.TimeUtils;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.dtr.DTRHandler;
import org.bson.types.ObjectId;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.*;

public class LettingInMenu extends PaginatedMenu {

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "Factions Letting In";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        final Map<Integer, Button> toReturn = new HashMap<>();

        for (Map.Entry<Team, Integer> teamIntegerEntry : getSortedList().entrySet()) {
            final Team team = teamIntegerEntry.getKey();

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

                    lore.add(ChatColor.translate("&4&l┃ &fHQ: " + ChatColor.RED + team.getHQ().getBlockX() + ", " + team.getHQ().getBlockZ()));
                    lore.add(ChatColor.translate("&4&l┃ &fBase Height: " + ChatColor.RED + teamIntegerEntry.getValue() + " blocks"));
                    lore.add(ChatColor.translate("&4&l┃ &fMembers Online: " + ChatColor.RED + team.getOnlineMembers().size() + "/" + team.getMembers().size()));
                    lore.add(ChatColor.translate("&4&l┃ &fDeaths Until Raidable: " + team.formatDTR()));
                    if (DTRHandler.isOnCooldown(team)) {
                        lore.add(ChatColor.translate("&4&l┃ &fRegen: &c" + TimeUtils.formatIntoDetailedString(((int) (team.getDTRCooldown() - System.currentTimeMillis()) / 1000))));
                    }
                    lore.add("");
                    lore.add(ChatColor.RED + "Click to focus this faction");
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

    public Map<Team, Integer> getSortedList() {
        final Map<Team, Double> firstTeams = new HashMap<>();

        Foxtrot.getInstance().getLettingInHandler().getCache().keySet().stream().map(it -> Foxtrot.getInstance().getTeamHandler().getTeam(it)).filter(it -> it != null && !it.isRaidable() && it.getOnlineMembers().size() != 0).forEach(it -> firstTeams.put(it, it.getDTR()));

        LinkedList<Map.Entry<Team, Double>> list = new LinkedList<>(firstTeams.entrySet());

        list.sort((o1, o2) -> (o2.getValue().compareTo(o1.getValue())));

        Collections.reverse(list);

        LinkedHashMap<Team, Integer> sortedHashMap = new LinkedHashMap<>();

        for (Map.Entry<Team, Double> entry : list) {
            sortedHashMap.put(entry.getKey(), Foxtrot.getInstance().getLettingInHandler().getCache().get(entry.getKey().getUniqueId()));
        }

        return sortedHashMap;
    }

    @Override
    public boolean isAutoUpdate() {
        return true;
    }

    @Override
    public int getMaxItemsPerPage(Player player) {
        return 27;
    }
}
