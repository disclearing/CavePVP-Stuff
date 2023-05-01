package org.cavepvp.suge.kit.command;

import cc.fyre.proton.command.Command;
import org.bukkit.entity.Player;
import org.cavepvp.suge.kit.menu.KitStatsMenu;

public class KitUsesCommand {
    @Command(names = {"kit uses", "kits uses", "gkit uses"}, permission = "op")
    public static void execute(Player player) {
        new KitStatsMenu().openMenu(player);
    }
}
