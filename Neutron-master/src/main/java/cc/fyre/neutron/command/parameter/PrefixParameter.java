package cc.fyre.neutron.command.parameter;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.prefix.Prefix;
import com.mysql.jdbc.StringUtils;
import cc.fyre.proton.command.param.ParameterType;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PrefixParameter implements ParameterType<Prefix> {

    @Override
    public Prefix transform(CommandSender commandSender,String s) {

        final Prefix toReturn = Neutron.getInstance().getPrefixHandler().fromName(s);

        if (toReturn == null) {
            commandSender.sendMessage(ChatColor.RED + "Prefix " + ChatColor.YELLOW + s + ChatColor.RED + " not found.");
            return null;
        }

        return toReturn;
    }

    @Override
    public List<String> tabComplete(Player player,Set<String> set,String s) {
        return Neutron.getInstance().getPrefixHandler().getCache().values()
                .stream()
                .filter(prefix -> StringUtils.startsWithIgnoreCase(prefix.getName(),s))
                .map(Prefix::getName)
                .collect(Collectors.toList());
    }

}
