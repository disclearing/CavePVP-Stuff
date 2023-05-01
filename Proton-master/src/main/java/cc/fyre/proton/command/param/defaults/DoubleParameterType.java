package cc.fyre.proton.command.param.defaults;

import cc.fyre.proton.command.param.ParameterType;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class DoubleParameterType implements ParameterType<Double> {

    public Double transform(CommandSender sender, String source) {

        if (source.toLowerCase().contains("e")) {
            sender.sendMessage(ChatColor.RED + source + " is not a valid number.");
            return null;
        }

        try {

            final double parsed = Double.parseDouble(source);

            if (Double.isNaN(parsed) || !Double.isFinite(parsed)) {
                sender.sendMessage(ChatColor.RED + source + " is not a valid number.");
                return null;
            }

            return parsed;
        } catch (NumberFormatException exception) {
            sender.sendMessage(ChatColor.RED + source + " is not a valid number.");
            return null;
        }
    }

    public List<String> tabComplete(Player sender, Set<String> flags, String source) {
        return new ArrayList<>();
    }

}