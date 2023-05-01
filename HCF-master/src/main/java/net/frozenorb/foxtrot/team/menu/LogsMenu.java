package net.frozenorb.foxtrot.team.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.track.TeamActionType;
import net.frozenorb.foxtrot.team.track.TrackCategory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class LogsMenu extends Menu {
    private Team team;
    private Map<TeamActionType, Map<String, Object>> logs;

    @Override
    public String getTitle(Player player) {
        return "Team Logs";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> toReturn = new HashMap<>();

        for (int i = 0; i < (9*5); i++) {
            toReturn.put(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 15, ""));
        }

        for (TrackCategory value : TrackCategory.values()) {
            if (value == TrackCategory.STAFF) {
                continue;
            }

            toReturn.put(value.getSlot(), new Button() {
                @Override
                public String getName(Player player) {
                    return ChatColor.DARK_RED + ChatColor.BOLD.toString() + value.getDisplayName();
                }

                @Override
                public List<String> getDescription(Player player) {
                    int logCount = logs.values().size();

                    final List<String> toReturn = new ArrayList<>(value.getLore());
                    toReturn.add("");
                    toReturn.add(ChatColor.translate("&4&l┃ &fLogs: &c" + logCount));
                    toReturn.add("");

                    if (value == TrackCategory.ALL) {
                        toReturn.add(ChatColor.RED + "Click to view all logs");
                    } else {
                        toReturn.add(ChatColor.RED + "Click to view all logs in this category");
                    }

                    return toReturn;
                }

                @Override
                public byte getDamageValue(Player player) {
                    return value.getData();
                }

                @Override
                public Material getMaterial(Player player) {
                    return value.getMaterial();
                }

                @Override
                public void clicked(Player player, int slot, ClickType clickType) {
                    final Map<TeamActionType, Map<String, Object>> params = new HashMap<>();

                    logs.entrySet().stream().filter(it -> value == TrackCategory.ALL || it.getKey().getTrackCategory() == value).forEach(it -> params.put(it.getKey(), it.getValue()));
                    new LogCategoryMenu(team, value, params).openMenu(player);
                }
            });

        }

        return toReturn;
    }
}
