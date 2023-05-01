package net.frozenorb.foxtrot.team.menu;

import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.team.menu.button.BooleanButton;
import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import cc.fyre.proton.util.Callback;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
public class ConfirmMenu extends Menu {

    private String title;
    private Callback<Boolean> response;

    @Override
    public String getTitle(Player player) {
        if (title.length() > 32) {
            return title.substring(0, 31);
        } else {
            return title;
        }
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        HashMap<Integer, Button> buttons = new HashMap<>();

        for (int i = 0; i < 9; i++) {
            if (i == 3) {
                buttons.put(i, new BooleanButton(true, response));

            } else if (i == 5) {
                buttons.put(i, new BooleanButton(false, response));

            } else {
                buttons.put(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 14));
            }
        }

        return buttons;
    }

}
