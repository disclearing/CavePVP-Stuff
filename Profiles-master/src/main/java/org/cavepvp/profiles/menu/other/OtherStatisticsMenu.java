package org.cavepvp.profiles.menu.other;

import cc.fyre.neutron.profile.Profile;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.cavepvp.profiles.menu.MenuType;
import org.cavepvp.profiles.playerProfiles.PlayerProfile;
import org.cavepvp.profiles.playerProfiles.impl.stats.StatisticServer;
import org.cavepvp.profiles.playerProfiles.impl.stats.Statistics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class OtherStatisticsMenu extends Menu {
    private PlayerProfile playerProfile;
    private Profile profile;

    @Override
    public String getTitle(Player player) {
        return "Statistics";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> toReturn = new HashMap<>(ViewingOtherSharedButtons.mainButtons(player, playerProfile, profile, MenuType.STATISTICS));

        boolean update = false;

        for (StatisticServer value : StatisticServer.values()) {
            if (playerProfile.getStatistics().containsKey(value)) {
                continue;
            }

            playerProfile.getStatistics().put(value, new Statistics(value));
            update = true;
        }

        if (update) {
            playerProfile.save();
        }

        for (Statistics statistics : playerProfile.getStatistics().values()) {
            final StatisticServer server = statistics.getStatisticServer();

            toReturn.put(server.getSlot(), new Button() {
                @Override
                public String getName(Player player) {
                    return ChatColor.DARK_RED + ChatColor.BOLD.toString() + server.getDisplayName() + " Statistics";
                }

                @Override
                public List<String> getDescription(Player player) {
                    final List<String> toReturn = new ArrayList<>();

                    toReturn.add(ChatColor.GRAY + "All of their " + server.getDisplayName() + " statistics are shown below");
                    toReturn.add("");
                    toReturn.add(ChatColor.translate("&4&l┃ &fKills: &c" + statistics.getKills()));
                    toReturn.add(ChatColor.translate("&4&l┃ &fDeaths: &c" + statistics.getDeaths()));
                    toReturn.add("");

                    if (server != StatisticServer.BUNKERS) {
                        toReturn.add(ChatColor.translate("&4&l┃ &fMaps Played: &c"  + statistics.getMapsPlayed()));
                        toReturn.add(ChatColor.translate("&4&l┃ &fKOTH Captures: &c"  + statistics.getKothsCaptured()));
                        toReturn.add(ChatColor.translate("&4&l┃ &fCitadel Captures: &c"  + statistics.getCitadelsCaptured()));
                    } else {
                        toReturn.add(ChatColor.translate("&4&l┃ &fWins: &c"  + statistics.getWins()));
                        toReturn.add(ChatColor.translate("&4&l┃ &fLosses: &c"  + statistics.getLosses()));
                        toReturn.add(ChatColor.translate("&4&l┃ &fGames Played: &c"  + statistics.getGamesPlayed()));
                        toReturn.add(ChatColor.translate("&4&l┃ &fKOTH Captures: &c"  + statistics.getKothsCaptured()));
                    }
                    toReturn.add("");
                    toReturn.add(ChatColor.GREEN + "Click to play " + server.getDisplayName());

                    return toReturn;
                }

                @Override
                public Material getMaterial(Player player) {
                    return server.getIcon();
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType) {
                    player.chat("/joinqueue " + server.getDisplayName());
                }
            });

        }

        return toReturn;
    }
}
