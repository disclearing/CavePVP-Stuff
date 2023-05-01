package net.frozenorb.foxtrot.gameplay.kitmap.kits.command;

import cc.fyre.proton.command.Command;
import net.frozenorb.foxtrot.gameplay.kitmap.gem.menu.upgrades.GemShopUpgradesMenu;
import org.bukkit.entity.Player;

public class KitUpgradesCommand {

    @Command(names = {"kit upgrades", "upgradekits", "kit upgrade", "upgrade"}, permission = "")
    public static void execute(Player player) {
        new GemShopUpgradesMenu().openMenu(player);
    }

}
