package cc.fyre.proton.command.command;

import cc.fyre.proton.Proton;
import cc.fyre.proton.command.flag.Data;
import cc.fyre.proton.command.flag.FlagData;
import cc.fyre.proton.command.param.ParameterData;
import cc.fyre.proton.command.param.ParameterType;
import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import mkremins.fanciful.FancyMessage;
import cc.fyre.proton.command.CommandHandler;
import cc.fyre.proton.command.argument.Arguments;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.spigotmc.SpigotConfig;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

public class CommandNode {

    @Getter @NonNull private String name;
    @Getter @Setter private Set<String> aliases = new HashSet<>();
    @Getter @NonNull private String permission;
    @Getter @Setter private String description;
    @Getter @Setter private boolean async;
    @Getter @Setter private boolean hidden;
    @Getter @Setter protected Method method;
    @Getter @Setter protected Class<?> owningClass;
    @Getter @Setter private List<String> validFlags;
    @Getter @Setter private List<Data> parameters;
    @Getter @Setter private Map<String, CommandNode> children = new TreeMap<>();
    @Getter @Setter private CommandNode parent;
    @Getter @Setter private boolean logToConsole;

    public CommandNode() {
    }

    public CommandNode(Class<?> owningClass) {
        this.owningClass = owningClass;
    }

    public void registerCommand(CommandNode commandNode) {
        commandNode.setParent(this);
        this.children.put(commandNode.getName(), commandNode);
    }

    public boolean hasCommand(String name) {
        return this.children.containsKey(name.toLowerCase());
    }

    public CommandNode getCommand(String name) {
        return this.children.get(name.toLowerCase());
    }

    public boolean hasCommands() {
        return this.children.size() > 0;
    }

    public CommandNode findCommand(Arguments arguments) {

        if (arguments.getArguments().size() > 0) {

            final String trySub = arguments.getArguments().get(0);

            if (this.hasCommand(trySub)) {
                arguments.getArguments().remove(0);
                CommandNode returnNode = this.getCommand(trySub);
                return returnNode.findCommand(arguments);
            }

        }

        return this;
    }

    public boolean isValidFlag(String test) {
        return test.length() == 1 ? this.validFlags.contains(test) : this.validFlags.contains(test.toLowerCase());
    }

    public boolean canUse(CommandSender sender) {

        if (this.permission == null || this.permission.equals("")) {
            return true;
        }

        if (this.permission.equals("op") && sender.isOp()) {
            return true;
        }

        if (this.permission.equals("console") && sender instanceof ConsoleCommandSender) {
            return true;
        }

        return sender.isOp() || sender instanceof ConsoleCommandSender || sender.hasPermission(this.permission);
    }

    public FancyMessage getUsage(String realLabel) {

        final FancyMessage usage = (new FancyMessage("Usage: /" + realLabel)).color(ChatColor.RED);

        if (!Strings.isNullOrEmpty(this.getDescription())) {
            usage.tooltip(ChatColor.YELLOW + this.getDescription());
        }

        final List<FlagData> flags = Lists.newArrayList();

        flags.addAll(this.parameters.stream().filter((datax) -> datax instanceof FlagData).map((datax) -> (FlagData)datax).collect(Collectors.toList()));

        final List<ParameterData> parameters = Lists.newArrayList();

        parameters.addAll(this.parameters.stream().filter((datax) -> datax instanceof ParameterData).map((datax) -> (ParameterData)datax).collect(Collectors.toList()));

        boolean flagFirst = true;

        if (!flags.isEmpty()) {

            usage.then("(").color(ChatColor.RED);

            if (!Strings.isNullOrEmpty(this.getDescription())) {
                usage.tooltip(ChatColor.YELLOW + this.getDescription());
            }

            for (FlagData data : flags) {

                final String name = data.getNames().get(0);

                if (!flagFirst) {

                    usage.then(" | ").color(ChatColor.RED);

                    if (!Strings.isNullOrEmpty(this.getDescription())) {
                        usage.tooltip(ChatColor.YELLOW + this.getDescription());
                    }

                }

                flagFirst = false;
                usage.then("-" + name).color(ChatColor.AQUA);

                if (!Strings.isNullOrEmpty(data.getDescription())) {
                    usage.tooltip(ChatColor.GRAY + data.getDescription());
                }

            }

            usage.then(") ").color(ChatColor.RED);
            if (!Strings.isNullOrEmpty(this.getDescription())) {
                usage.tooltip(ChatColor.YELLOW + this.getDescription());
            }

        }

        if (!parameters.isEmpty()) {

            for(int index = 0; index < parameters.size(); ++index) {

                final ParameterData data = parameters.get(index);
                final boolean required = data.getDefaultValue().isEmpty();

                usage.then((required ? "<" : "[") + data.getName() + (data.isWildcard() ? "..." : "") + (required ? ">" : "]") + (index != parameters.size() - 1 ? " " : "")).color(ChatColor.RED);
                if (!Strings.isNullOrEmpty(this.getDescription())) {
                    usage.tooltip(ChatColor.YELLOW + this.getDescription());
                }
            }
        }

        return usage;
    }

    public FancyMessage getUsage() {

        final FancyMessage usage = new FancyMessage("");
        final List<FlagData> flags = Lists.newArrayList();

        flags.addAll(this.parameters.stream().filter((datax) -> datax instanceof FlagData).map((datax) -> (FlagData)datax).collect(Collectors.toList()));

        final List<ParameterData> parameters = Lists.newArrayList();

        parameters.addAll(this.parameters.stream().filter((datax) -> datax instanceof ParameterData).map((datax) -> (ParameterData)datax).collect(Collectors.toList()));

        boolean flagFirst = true;

        if (!flags.isEmpty()) {
            usage.then("(").color(ChatColor.RED);

            for (FlagData data : flags) {

                final String name = data.getNames().get(0);

                if (!flagFirst) {
                    usage.then(" | ").color(ChatColor.RED);
                }

                flagFirst = false;

                usage.then("-" + name).color(ChatColor.AQUA);

                if (!Strings.isNullOrEmpty(data.getDescription())) {
                    usage.tooltip(ChatColor.GRAY + data.getDescription());
                }

            }

            usage.then(") ").color(ChatColor.RED);
        }

        if (!parameters.isEmpty()) {

            for(int index = 0; index < parameters.size(); ++index) {

                final ParameterData data = parameters.get(index);
                final boolean required = data.getDefaultValue().isEmpty();

                usage.then((required ? "<" : "[") + data.getName() + (data.isWildcard() ? "..." : "") + (required ? ">" : "]") + (index != parameters.size() - 1 ? " " : "")).color(ChatColor.RED);
            }
        }

        return usage;
    }

    public boolean invoke(CommandSender sender, Arguments arguments) throws CommandException {
        if (this.method == null) {
            if (this.hasCommands()) {
                if (this.getSubCommands(sender, true).isEmpty()) {

                    if (this.isHidden()) {
                        sender.sendMessage(SpigotConfig.unknownCommandMessage);
                    } else {
                        sender.sendMessage(ChatColor.RED + "No permission.");
                    }
                }
            } else {
                sender.sendMessage(SpigotConfig.unknownCommandMessage);
            }

            return true;
        } else {

            final List<Object> objects = new ArrayList<>(this.method.getParameterCount());

            objects.add(sender);
            int index = 0;

            for (Data unknownData : this.parameters) {

                if (unknownData instanceof FlagData) {

                    final FlagData flagData = (FlagData)unknownData;

                    boolean value = flagData.isDefaultValue();

                    for (String flagDataName : flagData.getNames()) {

                        if (arguments.hasFlag(flagDataName)) {
                            value = !value;
                            break;
                        }
                    }

                    objects.add(flagData.getMethodIndex(),value);
                } else if (unknownData instanceof ParameterData) {

                    final ParameterData parameterData = (ParameterData)unknownData;

                    String argument;

                    try {
                        argument = arguments.getArguments().get(index);
                    } catch (Exception ex) {

                        if (parameterData.getDefaultValue().isEmpty()) {
                            return false;
                        }

                        argument = parameterData.getDefaultValue();
                    }

                    if (parameterData.isWildcard() && (argument.isEmpty() || !argument.equals(parameterData.getDefaultValue()))) {
                        argument = arguments.join(index);
                    }

                    ParameterType<?> type = Proton.getInstance().getCommandHandler().getParameterType(parameterData.getType());

                    if (parameterData.getParameterType() != null) {

                        try {
                            type = (ParameterType)parameterData.getParameterType().newInstance();
                        } catch (IllegalAccessException | InstantiationException ex) {
                            ex.printStackTrace();
                            throw new CommandException("Failed to create ParameterType instance: " + parameterData.getParameterType().getName(),ex);
                        }
                    }

                    if (type == null) {
                        Class<?> t = parameterData.getParameterType() == null ? parameterData.getType() : parameterData.getParameterType();
                        sender.sendMessage(ChatColor.RED + "No parameter type found: " + t.getSimpleName());
                        return true;
                    }

                    final Object result = type.transform(sender, argument);

                    if (result == null) {
                        return true;
                    }

                    objects.add(parameterData.getMethodIndex(), result);
                    ++index;
                }
            }

            try {

                final Stopwatch stopwatch = new Stopwatch();

                stopwatch.start();
                this.method.invoke(null, objects.toArray());
                stopwatch.stop();

                int executionThreshold = Proton.getInstance().getConfig().getInt("Command.TimeThreshold", 10);

                if (!this.async && this.logToConsole && stopwatch.elapsedMillis() >= (long)executionThreshold) {
                    Proton.getInstance().getLogger().warning("Command '/" + this.getFullLabel() + "' took " + stopwatch.elapsedMillis() + "ms!");
                }

                return true;
            } catch (InvocationTargetException| IllegalAccessException ex) {
                ex.printStackTrace();
                throw new CommandException("An error occurred while executing the command", ex);
            }
        }
    }

    public List<String> getSubCommands(CommandSender sender, boolean print) {

        final List<String> commands = new ArrayList<>();

        if (this.canUse(sender)) {

            final String command = (sender instanceof Player ? "/" : "") + this.getFullLabel() + (this.parameters != null ? " " + this.getUsage().toOldMessageFormat() : "") + (!Strings.isNullOrEmpty(this.description) ? ChatColor.GRAY + " - " + this.getDescription() : "");

            if (this.parent == null) {
                commands.add(command);
            } else if (this.parent.getName() != null && CommandHandler.ROOT_NODE.getCommand(this.parent.getName()) != this.parent) {
                commands.add(command);
            }

            if (this.hasCommands()) {

                for (CommandNode commandNode : this.getChildren().values()) {

                    commands.addAll(commandNode.getSubCommands(sender,false));
                }

            }

        }

        if (!commands.isEmpty() && print) {
            sender.sendMessage(ChatColor.BLUE.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', 35));

            for (String command : commands.stream().sorted(String::compareTo).collect(Collectors.toList())) {
                sender.sendMessage(ChatColor.RED + command);
            }

            sender.sendMessage(ChatColor.BLUE.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat('-', 35));
        }

        return commands;
    }

    public Set<String> getRealAliases() {

        final Set<String> aliases = this.getAliases();

        aliases.remove(this.getName());

        return aliases;
    }

    public String getFullLabel() {

        final List<String> labels = new ArrayList();

        for(CommandNode node = this; node != null; node = node.getParent()) {

            final String name = node.getName();

            if (name != null) {
                labels.add(name);
            }
        }

        Collections.reverse(labels);

        labels.remove(0);

        final StringBuilder builder = new StringBuilder();

        labels.forEach((s) -> builder.append(s).append(' '));

        return builder.toString().trim();
    }

    public String getUsageForHelpTopic() {
        return this.method != null && this.parameters != null ? "/" + this.getFullLabel() + " " + ChatColor.stripColor(this.getUsage().toOldMessageFormat()) : "";
    }

    public CommandNode(@Nonnull String name,@Nonnull String permission) {

        if (name == null) {
            throw new NullPointerException("name");
        } else if (permission == null) {
            throw new NullPointerException("permission");
        } else {
            this.name = name;
            this.permission = permission;
        }

    }

    public CommandNode(@NonNull String name, Set<String> aliases, @NonNull String permission, String description, boolean async, boolean hidden, Method method, Class<?> owningClass, List<String> validFlags, List<Data> parameters, Map<String, CommandNode> children, CommandNode parent, boolean logToConsole) {
        if (name == null) {
            throw new NullPointerException("name");
        } else if (permission == null) {
            throw new NullPointerException("permission");
        } else {
            this.name = name;
            this.aliases = aliases;
            this.permission = permission;
            this.description = description;
            this.async = async;
            this.hidden = hidden;
            this.method = method;
            this.owningClass = owningClass;
            this.validFlags = validFlags;
            this.parameters = parameters;
            this.children = children;
            this.parent = parent;
            this.logToConsole = logToConsole;
        }
    }

    public void setName(@NonNull String name) {
        if (name == null) {
            throw new NullPointerException("name");
        } else {
            this.name = name;
        }
    }

    public void setPermission(@NonNull String permission) {
        if (permission == null) {
            throw new NullPointerException("permission");
        } else {
            this.permission = permission;
        }
    }

}
