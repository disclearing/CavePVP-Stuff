package net.frozenorb.foxtrot.team.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import cc.fyre.proton.menu.pagination.PaginatedMenu;
import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.track.TeamActionType;
import net.frozenorb.foxtrot.team.track.TrackCategory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class LogCategoryMenu extends PaginatedMenu {
    private Team team;
    private TrackCategory trackCategory;
    private Map<TeamActionType, Map<String, Object>> logs;

    @Override
    public String getPrePaginatedTitle(Player player) {
        return "Team Logs";
    }

    @Override
    public Map<Integer, Button> getAllPagesButtons(Player player) {
        final Map<Integer, Button> toReturn = new HashMap<>();

        for (Map.Entry<TeamActionType, Map<String, Object>> entry : logs.entrySet()) {
            toReturn.put(toReturn.size(), new Button() {
                @Override
                public String getName(Player player) {
                    return ChatColor.DARK_RED + ChatColor.BOLD.toString() + entry.getKey().getFancyName();
                }

                @Override
                public List<String> getDescription(Player player) {
                    final List<String> toReturn = new ArrayList<>();
                    final Map<String, Object> params = entry.getValue();

                    for (Map.Entry<String, Object> entry2 : entry.getValue().entrySet()) {
                        toReturn.add(entry2.getKey() + " <-- Result: " + entry2.getValue());
                    }

                    return toReturn;
                }

                @Override
                public Material getMaterial(Player player) {
                    return Material.PAPER;
                }
            });
        }

        return toReturn;
    }
}
