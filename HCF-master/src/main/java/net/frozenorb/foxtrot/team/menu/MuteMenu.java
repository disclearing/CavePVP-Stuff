package net.frozenorb.foxtrot.team.menu;

import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.team.menu.button.MuteButton;
import net.frozenorb.foxtrot.team.Team;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class MuteMenu extends Menu {
    private Team team;


    @Override
    public Map<Integer, Button> getButtons(Player player) {
        HashMap<Integer, Button> buttons = new HashMap<>();

        for (int i = 0; i < 9; i++) {
            if (i == 1) {
                buttons.put(i, new MuteButton(5, team));

            } else if (i == 3) {
                buttons.put(i, new MuteButton(15, team));

            } else if (i == 5) {
                buttons.put(i, new MuteButton(30, team));

            } else if (i == 7) {
                buttons.put(i, new MuteButton(60, team));

            } else {
                buttons.put(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 14));

            }
        }

        return buttons;
    }

    @Override
    public String getTitle(Player player) {
        return "Mute " + team.getName();
    }
}
