package cc.fyre.piston.command.admin;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.entity.Player;

public class EnderChestCommand {

    @Command(
            names = {"endercherst","echest"},
            permission = "command.enderchest"
    )
    public static void execute(Player player,@Parameter(name = "player",defaultValue = "self")Player target) {
        player.openInventory(target.getEnderChest());
    }

}
