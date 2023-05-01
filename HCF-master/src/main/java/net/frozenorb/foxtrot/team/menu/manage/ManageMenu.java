package net.frozenorb.foxtrot.team.menu.manage;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.menu.manage.button.*;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class ManageMenu extends Menu {
    private Team team;

    @Override
    public String getTitle(Player player) {
        return "Manage Faction";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> toReturn = new HashMap<>();

        for (int i = 0; i < (9*5); i++) {
            toReturn.put(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 15, ""));
        }

        toReturn.put(11, new MemberButton(team));
        toReturn.put(13, new LogsButton(team));
        toReturn.put(15, new PointsButton(team));

        toReturn.put(28, new SettingsButton(team));
        toReturn.put(30, new UpgradesButton(team));
        toReturn.put(32, new LettingInButton(team, this));
        toReturn.put(34, new StatisticsButton(team));

        return toReturn;
    }
}
