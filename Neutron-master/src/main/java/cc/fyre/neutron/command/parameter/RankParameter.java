package cc.fyre.neutron.command.parameter;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.rank.Rank;
import com.mysql.jdbc.StringUtils;
import cc.fyre.proton.command.param.ParameterType;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RankParameter implements ParameterType<Rank> {

    @Override
    public Rank transform(CommandSender commandSender,String s) {

        final Rank toReturn = Neutron.getInstance().getRankHandler().fromName(s);

        if (toReturn == null) {
            commandSender.sendMessage(ChatColor.RED + "Rank " + ChatColor.YELLOW + s + ChatColor.RED + " not found.");
            return null;
        }

        return toReturn;
    }

    @Override
    public List<String> tabComplete(Player player,Set<String> set,String s) {
        return Neutron.getInstance().getRankHandler().getCache().values()
                .stream()
                .filter(rank -> StringUtils.startsWithIgnoreCase(rank.getName(),s))
                .map(Rank::getName)
                .collect(Collectors.toList());
    }

}
