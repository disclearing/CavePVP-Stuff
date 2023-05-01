package cc.fyre.proton.hologram.command;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.hologram.construct.Hologram;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * @author xanderume@gmail (JavaProject)
 */
public class HologramMoveHereCommand {

    @Command(
            names = {"hologram movehere","holo movehere","hologram move","holo move"},
            permission = "proton.command.hologram.movehere"
    )
    public static void execute(Player player,@Parameter(name = "hologram")Hologram hologram) {
        hologram.move(player.getLocation());

        player.sendMessage(ChatColor.GOLD + "Moved hologram with id " + ChatColor.WHITE + hologram.id() + ChatColor.GOLD + ".");
    }

    @Command(
            names = {"hologram moveto","holo moveto"},
            permission = "proton.command.hologram.moveto"
    )
    public static void moveTo(Player player,@Parameter(name = "hologram")Hologram hologram, @Parameter(name = "x")double x, @Parameter(name = "y")double y, @Parameter(name = "x")double z) {
        final Location location = player.getLocation().clone();

        location.setX(x);
        location.setY(y);
        location.setZ(z);

        hologram.move(location);

        player.sendMessage(ChatColor.GOLD + "Moved hologram with id " + ChatColor.WHITE + hologram.id() + ChatColor.GOLD + " to " + ChatColor.WHITE + x + ", " + y + ", " + z + ChatColor.GOLD + ".");
    }

}
