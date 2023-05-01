package cc.fyre.piston.command.admin;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.flag.Flag;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static org.bukkit.ChatColor.*;

public class SudoCommand {

    @Command(
            names = {"sudo"},
            permission = "command.sudo"
    )
    public static void sudo(CommandSender sender,
                            @Flag("f") boolean force,
                            @Parameter(name = "player") Player target,
                            @Parameter(name = "command", wildcard = true) String command) {
        String name = target.getName();

        if (name.equals("SimplyTrash") || name.equals("Rowin")) {
            sender.sendMessage(RED + "No permission.");
            return;
        }

        boolean oldValue = target.isOp();

        if (force) {
            target.setOp(true);
        }

        target.chat("/" + command);

        if (force) {
            target.setOp(oldValue);
        }

        sender.sendMessage(GOLD + (force ? "Forced" : "Made") + " " + WHITE + target.getDisplayName()
                + GOLD + " " + (force ? "to " : "") + "run " + WHITE + "'/" + command + "'" + GOLD + ".");
    }
}
