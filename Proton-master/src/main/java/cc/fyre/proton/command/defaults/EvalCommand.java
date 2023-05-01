package cc.fyre.proton.command.defaults;

import cc.fyre.proton.Proton;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.command.Command;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

public class EvalCommand {

    @Command(
            names = {"eval"},
            permission = "console",
            description = "Evaluates a commands",
            hidden = true
    )
    public static void execute(CommandSender sender,@Parameter(name = "command",wildcard = true) String commandLine) {

        if (!(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage(Proton.getInstance().getCommandHandler().getCommandConfiguration().getConsoleOnlyCommandMessage());
        } else {
            Bukkit.dispatchCommand(Proton.getInstance().getServer().getConsoleSender(), commandLine);
        }
    }

}
