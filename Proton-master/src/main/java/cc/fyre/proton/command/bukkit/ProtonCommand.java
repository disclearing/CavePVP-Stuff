package cc.fyre.proton.command.bukkit;

import cc.fyre.proton.Proton;
import cc.fyre.proton.command.flag.Flag;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import lombok.Getter;
import cc.fyre.proton.command.argument.ArgumentProcessor;
import cc.fyre.proton.command.argument.Arguments;
import cc.fyre.proton.command.command.CommandNode;
import cc.fyre.proton.command.param.ParameterData;
import cc.fyre.proton.command.param.ParameterType;
import net.minecraft.util.org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import org.spigotmc.SpigotConfig;

import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class ProtonCommand extends Command implements PluginIdentifiableCommand {

    @Getter protected CommandNode node;
    private JavaPlugin owningPlugin;

    public ProtonCommand(CommandNode node,JavaPlugin plugin) {
        super(node.getName(), "", "/", Lists.newArrayList(node.getRealAliases()));
        this.node = node;
        this.owningPlugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {

        label = label.replace(this.owningPlugin.getName().toLowerCase() + ":", "");

        String[] newArgs = this.concat(label, args);
        Arguments arguments = (new ArgumentProcessor()).process(newArgs);

        CommandNode executionNode = this.node.findCommand(arguments);
        String realLabel = this.getFullLabel(executionNode);

        if (executionNode.canUse(sender)) {

            if (executionNode.isAsync()) {

                this.owningPlugin.getServer().getScheduler().runTaskAsynchronously(this.owningPlugin,() -> {

                    try {

                        if (!executionNode.invoke(sender,arguments)) {
                            executionNode.getUsage(realLabel).send(sender);
                        }

                    } catch (CommandException ex) {

                        executionNode.getUsage(realLabel).send(sender);
                        sender.sendMessage(ChatColor.RED + "An error occurred while processing your commands.");

                        if (sender.isOp()) {
                            this.sendStackTrace(sender,ex);
                        }

                    }

                });

            } else {

                try {

                    if (!executionNode.invoke(sender, arguments)) {
                        executionNode.getUsage(realLabel).send(sender);
                    }
                } catch (CommandException ex) {
                    executionNode.getUsage(realLabel).send(sender);
                    sender.sendMessage(ChatColor.RED + "An error occurred while processing your commands.");

                    if (sender.isOp()) {
                        this.sendStackTrace(sender,ex);
                    }

                }

            }

        } else if (executionNode.isHidden()) {
            sender.sendMessage(SpigotConfig.unknownCommandMessage);
        } else {
            sender.sendMessage(Proton.getInstance().getCommandHandler().getCommandConfiguration().getNoPermissionMessage());
        }

        return true;
    }

    public List<String> tabComplete(CommandSender sender, String cmdLine) {

        if (!(sender instanceof Player)) {
            return ImmutableList.of();
        }

        final String[] rawArgs = cmdLine.replace(this.owningPlugin.getName().toLowerCase() + ":", "").split(" ");

        if (rawArgs.length < 1) {

            if (!this.node.canUse(sender)) {
                return ImmutableList.of();
            }

            return ImmutableList.of();
        }

        final Arguments arguments = new ArgumentProcessor().process(rawArgs);
        final CommandNode realNode = this.node.findCommand(arguments);

        if (!realNode.canUse(sender)) {
            return ImmutableList.of();
        }

        final List<String> realArgs = arguments.getArguments();
        int currentIndex = realArgs.size() - 1;

        if (currentIndex < 0) {
            currentIndex = 0;
        }

        if (cmdLine.endsWith(" ") && realArgs.size() >= 1) {
            ++currentIndex;
        }

        if (currentIndex < 0) {
            return ImmutableList.of();
        }

        final List<String> completions = new ArrayList<>();

        if (realNode.hasCommands()) {

            final String name = (realArgs.size() == 0) ? "" : realArgs.get(realArgs.size() - 1);

            completions.addAll(realNode.getChildren().values().stream().filter(node -> node.canUse(sender) && (StringUtils.startsWithIgnoreCase(node.getName(),name) || StringUtils.isEmpty(name))).map(CommandNode::getName).collect((Collectors.toList())));

            if (completions.size() > 0) {
                return completions;
            }

        }

        if (rawArgs[rawArgs.length - 1].equalsIgnoreCase(realNode.getName()) && !cmdLine.endsWith(" ")) {
            return ImmutableList.of();
        }

        if (realNode.getValidFlags() != null && !realNode.getValidFlags().isEmpty()) {

            for (String flags : realNode.getValidFlags()) {

                final String arg = rawArgs[rawArgs.length - 1];

                if ((Flag.FLAG_PATTERN.matcher(arg).matches() || arg.equals("-")) && (StringUtils.startsWithIgnoreCase(flags, arg.substring(1)) || arg.equals("-"))) {
                    completions.add("-" + flags);
                }

            }

            if (completions.size() > 0) {
                return completions;
            }

        }

        try {
            ParameterType<?> parameterType = null;
            ParameterData data = null;

            if (realNode.getParameters() != null) {

                final List<ParameterData> params = realNode.getParameters().stream().filter(filter -> filter instanceof ParameterData).map(ParameterData.class::cast).collect(Collectors.toList());

                final int fixed = Math.max(0, currentIndex - 1);

                data = params.get(fixed);

                parameterType = Proton.getInstance().getCommandHandler().getParameterType(data.getType());

                if (data.getParameterType() != null) {

                    try {
                        parameterType = data.getParameterType().newInstance();
                    } catch (InstantiationException | IllegalAccessException ex) {
                        ex.printStackTrace();
                    }

                }
            }

            if (parameterType != null) {

                if (currentIndex < realArgs.size() && realArgs.get(currentIndex).equalsIgnoreCase(realNode.getName())) {
                    realArgs.add("");
                    ++currentIndex;
                }

                final String argumentBeingCompleted = (currentIndex >= realArgs.size() || realArgs.size() == 0) ? "" : realArgs.get(currentIndex);
                final List<String> suggested = parameterType.tabComplete((Player)sender, data.getTabCompleteFlags(), argumentBeingCompleted);

                completions.addAll(suggested.stream().filter(s -> StringUtils.startsWithIgnoreCase(s,argumentBeingCompleted)).collect(Collectors.toList()));
            }
        }

        catch (Exception ignored) {}

        return completions;
    }

    public Plugin getPlugin() {
        return this.owningPlugin;
    }

    private String[] concat(String label, String[] args) {

        final String[] labelAsArray = new String[]{label};
        final String[] newArgs = new String[args.length + 1];

        System.arraycopy(labelAsArray, 0, newArgs, 0, 1);
        System.arraycopy(args, 0, newArgs, 1, args.length);

        return newArgs;
    }

    private String getFullLabel(CommandNode node) {

        ArrayList labels;

        for (labels = new ArrayList(); node != null; node = node.getParent()) {

            String name = node.getName();

            if (name != null) {
                labels.add(name);
            }
        }

        Collections.reverse(labels);
        labels.remove(0);

        final StringBuilder builder = new StringBuilder();

        labels.forEach((s) -> builder.append(s).append(' '));

        return builder.toString();
    }

    public void sendStackTrace(CommandSender sender,Exception exception) {

        final String rootCauseMessage = ExceptionUtils.getRootCauseMessage(exception);

        sender.sendMessage(ChatColor.RED + "Message: " + rootCauseMessage);

        final String cause = ExceptionUtils.getStackTrace(exception);
        final StringTokenizer tokenizer = new StringTokenizer(cause);

        String exceptionType = "";
        String details = "";

        boolean parsingNeeded = false;

        while(tokenizer.hasMoreTokens()) {

            String token = tokenizer.nextToken();

            if (token.equalsIgnoreCase("Caused")) {
                tokenizer.nextToken();
                parsingNeeded = true;
                exceptionType = tokenizer.nextToken();
            } else if (token.equalsIgnoreCase("at") && parsingNeeded) {
                details = tokenizer.nextToken();
                break;
            }
        }

        sender.sendMessage(ChatColor.RED + "Exception: " + exceptionType.replace(":", ""));
        sender.sendMessage(ChatColor.RED + "Details:");
        sender.sendMessage(ChatColor.RED + details);
    }

}