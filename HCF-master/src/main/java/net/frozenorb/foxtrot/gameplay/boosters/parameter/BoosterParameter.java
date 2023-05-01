package net.frozenorb.foxtrot.gameplay.boosters.parameter;

import cc.fyre.proton.command.param.ParameterType;
import com.mysql.jdbc.StringUtils;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.Ability;
import net.frozenorb.foxtrot.gameplay.boosters.Booster;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BoosterParameter implements ParameterType<Booster> {

    @Override
    public Booster transform(CommandSender commandSender, String s) {

        final Booster toReturn = Foxtrot.getInstance().getNetworkBoosterHandler().findBooster(s);

        if (toReturn == null) {
            commandSender.sendMessage(ChatColor.RED + "Booster " + ChatColor.YELLOW + s + ChatColor.RED + " not found.");
            return null;
        }

        return toReturn;
    }
}
