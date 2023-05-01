package cc.fyre.piston.command.admin;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class WorldCommand {

    @Command(
            names = {"world"},
            permission = "command.world"
    )
    public static void execute(Player player,@Parameter(name = "world") World world,@Parameter(name = "player",defaultValue= "self")Player target) {
        target.teleport(world.getSpawnLocation().clone().add(0.5D, 0.0D, 0.5D));
    }

}
