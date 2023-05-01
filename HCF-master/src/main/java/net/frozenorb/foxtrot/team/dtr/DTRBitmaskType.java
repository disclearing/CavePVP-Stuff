package net.frozenorb.foxtrot.team.dtr;

import cc.fyre.proton.command.param.ParameterType;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DTRBitmaskType implements ParameterType<DTRBitmask> {

    public DTRBitmask transform(CommandSender sender, String source) {
        for (DTRBitmask bitmaskType : DTRBitmask.values()) {
            if (source.equalsIgnoreCase(bitmaskType.getName())) {
                return (bitmaskType);
            }
        }

        sender.sendMessage(ChatColor.RED + "No bitmask type with the name " + source + " found.");
        return (null);
    }

    public List<String> tabComplete(Player sender, Set<String> flags, String source) {
        List<String> completions = new ArrayList<>();

        for (DTRBitmask bitmask : DTRBitmask.values()) {
            if (StringUtils.startsWithIgnoreCase(bitmask.getName(), source)) {
                completions.add(bitmask.getName());
            }
        }

        return (completions);
    }

}