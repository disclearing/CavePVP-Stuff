package cc.fyre.proton.menu.menus;

import cc.fyre.proton.menu.buttons.BooleanButton;
import lombok.AllArgsConstructor;
import lombok.Getter;
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
    @Getter private Callback<Boolean> response;

    public Map<Integer, Button> getButtons(Player player) {
        HashMap<Integer, Button> buttons = new HashMap();

        for(int i = 0; i < 9; ++i) {
            if (i == 3) {
                buttons.put(i, new BooleanButton(true, this.response));
            } else if (i == 5) {
                buttons.put(i, new BooleanButton(false, this.response));
            } else {
                buttons.put(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte)14, new String[0]));
            }
        }

        return buttons;
    }

    @Override
    public String getTitle(Player player) {
        return this.title;
    }

}
