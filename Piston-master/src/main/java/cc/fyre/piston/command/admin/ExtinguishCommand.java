package cc.fyre.piston.command.admin;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.entity.Player;

public class ExtinguishCommand {

    @Command(
            names = {"extinguish","ext"},
            permission = "command.extinguish"
    )
    public static void execute(Player player,@Parameter(name = "player") Player target) {
        target.setFireTicks(0);
    }

}
