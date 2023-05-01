package cc.fyre.proton.hologram.command;

import cc.fyre.proton.Proton;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.hologram.construct.Hologram;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * @author xanderume@gmail (JavaProject)
 */
public class HologramCreateCommand {

    @Command(
            names = {"hologram create","holo create"},
            permission = "proton.command.hologram.create"
    )
    public static void execute(Player player,@Parameter(name = "id")int id,@Parameter(name = "text",wildcard = true) String text) {

        if (Proton.getInstance().getHologramHandler().fromId(id) != null) {
            player.sendMessage(ChatColor.RED + "Hologram with id " + ChatColor.WHITE + id + ChatColor.RED + " already exists.");
            return;
        }

        final Hologram hologram = Proton.getInstance().getHologramHandler().createHologram().addLines(text).at(player.getLocation()).build();

        hologram.send();

        player.sendMessage(ChatColor.GOLD + "Created new hologram with id " + ChatColor.WHITE + id + ChatColor.GOLD + ".");
    }

}
