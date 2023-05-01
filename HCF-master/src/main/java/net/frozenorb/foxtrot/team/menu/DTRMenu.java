package net.frozenorb.foxtrot.team.menu;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.team.menu.button.DTRButton;
import net.frozenorb.foxtrot.team.Team;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;

@AllArgsConstructor
public class DTRMenu extends Menu {

    Team team;

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        HashMap<Integer, Button> buttons = new HashMap<>();

        for (int i = 0; i < 9; i++) {
            if (i == 3) {
                buttons.put(i, new DTRButton(team, false));

            } else if (i == 5) {
                buttons.put(i, new DTRButton(team, true));

            } else {
                buttons.put(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 14));

            }
        }

        return buttons;
    }

    @Override
    public String getTitle(Player player) {
        return "Manage DTR";
    }
}