package cc.fyre.piston.command.admin;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.entity.Player;


public class ShowPlayerCommand {

    @Command(
            names = {"showplayer"},
            permission = "command.showplayer"
    )
    public static void execute(Player player,@Parameter(name = "player") Player target) {
        player.showPlayer(target);
    }

}
