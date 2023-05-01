package org.cavepvp.suge.enchant.command;

import cc.fyre.proton.command.Command;
import org.bukkit.entity.Player;
import org.cavepvp.suge.enchant.menu.CEMenu;

public class CECommand {
    @Command(names = {"ce", "customenchants"}, permission = "")
    public static void execute(Player player) {
        new CEMenu().openMenu(player);
    }

}
