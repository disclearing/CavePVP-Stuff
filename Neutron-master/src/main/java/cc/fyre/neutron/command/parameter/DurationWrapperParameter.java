package cc.fyre.neutron.command.parameter;

import cc.fyre.neutron.util.DurationWrapper;
import cc.fyre.proton.command.param.ParameterType;
import cc.fyre.proton.util.TimeUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DurationWrapperParameter implements ParameterType<DurationWrapper> {

    @Override
    public DurationWrapper transform(CommandSender sender,String source) {

        try {

            final int toReturn = TimeUtils.parseTime(source);

            if ((toReturn * 1000L) <= 0) {
                return new DurationWrapper(source, (long) Integer.MAX_VALUE);
            }

            return new DurationWrapper(source,toReturn * 1000L);
        } catch (NullPointerException | IllegalArgumentException ex) {
            return new DurationWrapper(source, (long) Integer.MAX_VALUE);
        }

    }

    @Override
    public List<String> tabComplete(Player player,Set<String> set,String s) {
        return new ArrayList<>();
    }
}
