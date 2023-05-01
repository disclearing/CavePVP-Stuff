package cc.fyre.proton.command.defaults;

import cc.fyre.proton.Proton;
import cc.fyre.proton.command.argument.ArgumentProcessor;
import cc.fyre.proton.command.argument.Arguments;
import cc.fyre.proton.command.command.CommandNode;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandInfoCommand {

    @Command(
            names = {"cmdinfo"},
            permission = "proton.command.commandinfo",
            hidden = true
    )
    public static void execute(CommandSender sender,@Parameter(name = "command",wildcard = true) String command) {

        final String[] args = command.split(" ");
        final ArgumentProcessor processor = new ArgumentProcessor();
        final Arguments arguments = processor.process(args);
        final CommandNode node = Proton.getInstance().getCommandHandler().ROOT_NODE.getCommand(arguments.getArguments().get(0));

        if (node != null) {

            final CommandNode realNode = node.findCommand(arguments);

            if (realNode != null) {

                final JavaPlugin plugin = JavaPlugin.getProvidingPlugin(realNode.getOwningClass());

                sender.sendMessage(ChatColor.WHITE + realNode.getFullLabel() + ChatColor.GOLD + ":");
                sender.sendMessage(ChatColor.GRAY + "-> " + ChatColor.GOLD + "Plugin: " + ChatColor.WHITE + plugin.getName());
                sender.sendMessage(ChatColor.GRAY + "-> " + ChatColor.GOLD + "Sub commands:");

                for (CommandNode value : realNode.getChildren().values()) {
                    sender.sendMessage("  " + ChatColor.GRAY + "-> " + ChatColor.AQUA + value.getSubCommands(sender,false));
                }

                return;
            }

        }

        sender.sendMessage(ChatColor.RED + "Command not found.");
    }

}
