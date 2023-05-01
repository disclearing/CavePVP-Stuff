package cc.fyre.proton.command.param.defaults;

import cc.fyre.proton.command.param.ParameterType;
import cc.fyre.proton.util.EntityUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class EntityTypeParameterType implements ParameterType<EntityType> {

    public EntityType transform(CommandSender commandSender, String source) {

        final EntityType type = EntityUtils.parse(source);

        if (type == null) {
            commandSender.sendMessage(ChatColor.RED + "No such entity type '" + ChatColor.YELLOW + source + ChatColor.RED + "' found.");
            return null;
        }

        return type;
    }

    public List<String> tabComplete(Player sender, Set<String> flags, String source) {
        return Arrays.stream(EntityType.values()).filter(entityType -> StringUtils.startsWithIgnoreCase(EntityUtils.getName(entityType),source)).map(EntityUtils::getName).collect(Collectors.toList());
    }

}