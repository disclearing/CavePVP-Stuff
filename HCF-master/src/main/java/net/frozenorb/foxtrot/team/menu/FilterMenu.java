package net.frozenorb.foxtrot.team.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.pagination.PaginatedMenu;
import cc.fyre.proton.util.TimeUtils;
import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.FilterType;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.dtr.DTRHandler;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
public class FilterMenu extends PaginatedMenu {
    private FilterType filterType;

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "Team Filter";
    }

    @Override
    public Map<Integer, Button> getGlobalButtons(Player player) {
        final Map<Integer, Button> toReturn = new HashMap<>();

        toReturn.put(4, new Button() {
            @Override
            public String getName(Player player) {
                return ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Filter";
            }

            @Override
            public List<String> getDescription(Player player) {
                final List<String> toReturn = new ArrayList<>();

                toReturn.add(ChatColor.GRAY + "Select a filter that you would like");
                toReturn.add("");
                for (FilterType value : FilterType.values()) {
                    String color = "&f";

                    if (filterType == value) {
                        color = "&a";
                    }

                    toReturn.add(ChatColor.translate("&4&l┃ &f" + color + value.getDescription()));
                }
                toReturn.add("");
                toReturn.add(ChatColor.RED + "Click to switch between filters");

                return toReturn;
            }

            @Override
            public Material getMaterial(Player player) {
                return Material.SIGN;
            }

            @Override
            public void clicked(Player player, int slot, ClickType clickType) {
                int nextNumber = filterType.getNumber()+1;

                filterType = Arrays.stream(FilterType.values()).filter(it -> it.getNumber() == nextNumber).findFirst().orElse(FilterType.LOWEST_DTR);

                Foxtrot.getInstance().getFactionFilterMap().setFilterType(player.getUniqueId(), filterType);
            }
        });

        return toReturn;
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        final Map<Integer, Button> toReturn = new HashMap<>();

        for (Team team : getSortedList(filterType)) {

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

                    if (team.getHQ() != null) {
                        lore.add(ChatColor.translate("&4&l┃ &fHQ: &c" + team.getHQ().getBlockX() + ", " + team.getHQ().getBlockZ()));
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

    public List<Team> getSortedList(FilterType filterType) {
        final List<Team> teams = Foxtrot.getInstance().getTeamHandler().getTeams().stream().filter(it -> it.getOwner() != null && it.getOnlineMemberAmount() > 0).collect(Collectors.toList());

        if (filterType == FilterType.HIGH_MEMBERS) {
            teams.sort(Comparator.comparingInt(Team::getOnlineMemberAmount).reversed());
        }

        if (filterType == FilterType.LOW_MEMBERS) {
            teams.sort(Comparator.comparingInt(Team::getOnlineMemberAmount));
        }

        if (filterType == FilterType.HIGHEST_DTR) {
            teams.sort(Comparator.comparingInt(Team::getDeathsTilRaidable).reversed());
        }

        if (filterType == FilterType.LOWEST_DTR) {
            teams.sort(Comparator.comparingInt(Team::getDeathsTilRaidable));
        }

        if (filterType == FilterType.KILLS) {
            teams.sort(Comparator.comparingInt(Team::getKills));
        }

        if (filterType == FilterType.DEATHS) {
            teams.sort(Comparator.comparingInt(Team::getDeaths));
        }

        return teams;
    }
}
