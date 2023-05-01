package net.frozenorb.foxtrot.gameplay.blockshop.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.gameplay.blockshop.ShopItem;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class CategoryMenu extends Menu {

    private final List<ShopItem> items;

    @Override
    public String getTitle(Player player) {
        return "Block Shop";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {
        Map<Integer, Button> buttons = new HashMap<>();

        buttons.put(40, new BackButton((player1, i, clickType) -> new BlockShopMenu().openMenu(player)));

        for (ShopItem item : items) {
            buttons.put(item.getIndex(), new ItemButton(item));
        }

        return buttons;
    }
}
