package net.frozenorb.foxtrot.gameplay.loot.partnercrate.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import cc.fyre.proton.util.ItemBuilder;
import net.frozenorb.foxtrot.gameplay.loot.partnercrate.PartnerType;
import net.frozenorb.foxtrot.gameplay.loot.partnercrate.menu.element.PartnerCrateElement;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class PartnerCrateMenu extends Menu {
    @Override
    public Map<Integer, Button> getButtons(Player player) {
        final Map<Integer, Button> toReturn = new HashMap<>();

        final Button white = Button.fromItem(ItemBuilder.of(Material.STAINED_GLASS_PANE, 1).data((byte)0).build());
        final Button pink = Button.fromItem(ItemBuilder.of(Material.STAINED_GLASS_PANE, 1).data((byte)6).build());
        final Button gray = Button.fromItem(ItemBuilder.of(Material.STAINED_GLASS_PANE, 1).data((byte)7).build());
        
        toReturn.put(0, white);
        toReturn.put(1, pink);
        toReturn.put(2, gray);
        toReturn.put(3, white);
        toReturn.put(4, pink);
        toReturn.put(5, white);
        toReturn.put(6, gray);
        toReturn.put(7, pink);
        toReturn.put(8, white);
        toReturn.put(9, pink);
        toReturn.put(10, pink);
        toReturn.put(11, white);
        toReturn.put(15, white);
        toReturn.put(16, pink);
        toReturn.put(17, pink);
        toReturn.put(18, gray);
        toReturn.put(19, white);
        toReturn.put(25, white);
        toReturn.put(26, gray);
        toReturn.put(27, pink);
        toReturn.put(28, pink);
        toReturn.put(29, white);
        toReturn.put(33, white);
        toReturn.put(34, pink);
        toReturn.put(35, pink);
        toReturn.put(36, white);
        toReturn.put(37, pink);
        toReturn.put(38, gray);
        toReturn.put(39, white);
        toReturn.put(40, pink);
        toReturn.put(41, white);
        toReturn.put(42, gray);
        toReturn.put(43, pink);
        toReturn.put(44, white);

        Arrays.stream(PartnerType.values()).forEach(it -> toReturn.put(it.getSlot(), new PartnerCrateElement(it)));

        return toReturn;
    }

    @Override
    public int size(Player player) {
        return 45;
    }

    @Override
    public boolean isPlaceholder() {
        return true;
    }

    @Override
    public String getTitle(Player player) {
        return "Partner Crates";
    }
}
