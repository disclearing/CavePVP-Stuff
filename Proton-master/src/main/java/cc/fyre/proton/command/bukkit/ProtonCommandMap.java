package cc.fyre.proton.command.bukkit;

import cc.fyre.proton.command.command.CommandNode;
import org.apache.commons.lang.Validate;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.*;

public class ProtonCommandMap extends SimpleCommandMap {

    public ProtonCommandMap(Server server) {
        super(server);
    }

    public List<String> tabComplete(CommandSender sender,String cmdLine) {
        Validate.notNull(sender, "Sender cannot be null");
        Validate.notNull(cmdLine, "Command line cannot null");

        final int spaceIndex = cmdLine.indexOf(32);

        String prefix;

        if (spaceIndex == -1) {

            final ArrayList<String> completions = new ArrayList();
            final Map<String, Command> knownCommands = this.knownCommands;
            prefix = sender instanceof Player ? "/" : "";

            for (Map.Entry<String,Command> entry : knownCommands.entrySet()) {

                final String name = entry.getKey();

                if (StringUtil.startsWithIgnoreCase(name, cmdLine)) {

                    final Command command = entry.getValue();

                    if (command instanceof ProtonCommand) {

                        CommandNode executionNode = ((ProtonCommand)command).node.getCommand(name);

                        if (executionNode == null) {
                            executionNode = ((ProtonCommand)command).node;
                        }

                        if (!executionNode.hasCommands()) {

                            CommandNode testNode = executionNode.getCommand(name);

                            if (testNode == null) {
                                testNode = ((ProtonCommand)command).node.getCommand(name);
                            }

                            if (testNode.canUse(sender)) {
                                completions.add(prefix + name);
                            }
                        } else if (executionNode.getSubCommands(sender, false).size() != 0) {
                            completions.add(prefix + name);
                        }
                    } else if (command.testPermissionSilent(sender)) {
                        completions.add(prefix + name);
                    }
                }
            }

            Collections.sort(completions, String.CASE_INSENSITIVE_ORDER);
            return completions;
        } else {

            final String commandName = cmdLine.substring(0, spaceIndex);

            final Command target = this.getCommand(commandName);

            if (target == null) {
                return null;
            } else if (!target.testPermissionSilent(sender)) {
                return null;
            } else {
                prefix = cmdLine.substring(spaceIndex + 1);
                final String[] args = prefix.split(" ");

                try {

                    final List<String> completions = target instanceof ProtonCommand ? ((ProtonCommand)target).tabComplete(sender, cmdLine) : target.tabComplete(sender, commandName, args);

                    if (completions != null) {
                        Collections.sort(completions, String.CASE_INSENSITIVE_ORDER);
                    }

                    return completions;
                } catch (CommandException ex) {
                    throw ex;
                } catch (Throwable ex) {
                    throw new CommandException("Unhandled exception executing tab-completer for '" + cmdLine + "' in " + target, ex);
                }
            }
        }
    }
}
