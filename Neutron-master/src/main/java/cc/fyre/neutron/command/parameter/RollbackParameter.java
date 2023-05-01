package cc.fyre.neutron.command.parameter;

import cc.fyre.neutron.profile.attributes.rollback.RollbackType;
import cc.fyre.proton.command.param.ParameterType;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RollbackParameter implements ParameterType<RollbackType> {

    @Override
    public RollbackType transform(CommandSender commandSender,String s) {

        try {
            return RollbackType.valueOf(s.toUpperCase());
        } catch (NullPointerException | IllegalArgumentException ex) {
            commandSender.sendMessage(ChatColor.RED + "Rollback type " + ChatColor.YELLOW + s + ChatColor.RED + " not found.");
            return null;
        }

    }

    @Override
    public List<String> tabComplete(Player player,Set<String> set,String s) {
        return Arrays.stream(RollbackType.values())
                .filter(color -> StringUtils.startsWithIgnoreCase(color.name(),s))
                .map(RollbackType::name)
                .collect(Collectors.toList());
    }
}
