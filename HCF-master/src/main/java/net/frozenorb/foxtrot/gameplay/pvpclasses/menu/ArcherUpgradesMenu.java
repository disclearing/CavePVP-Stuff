package net.frozenorb.foxtrot.gameplay.pvpclasses.menu;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import net.frozenorb.foxtrot.gameplay.pvpclasses.archer.ArcherColor;
import net.frozenorb.foxtrot.gameplay.pvpclasses.menu.element.ArcherUpgradeElement;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author xanderume@gmail.com
 */
public class ArcherUpgradesMenu extends Menu {

    @Override
    public int size(Player player) {
        return 3*9;
    }

    @Override
    public String getTitle(Player player) {
        return "Archer Upgrades";
    }

    @Override
    public Map<Integer, Button> getButtons(Player player) {

        final Map<Integer,Button> toReturn = new HashMap<>();

        final AtomicInteger index = new AtomicInteger();

        Arrays.stream(ArcherColor.values()).forEach(it -> toReturn.put(10 + (index.getAndIncrement() * 2),new ArcherUpgradeElement(it)));

        return toReturn;
    }

}
