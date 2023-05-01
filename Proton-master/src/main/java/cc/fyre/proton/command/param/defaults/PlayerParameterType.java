package cc.fyre.proton.command.param.defaults;

import cc.fyre.proton.Proton;
import cc.fyre.proton.command.param.ParameterType;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PlayerParameterType implements ParameterType<Player> {

    public Player transform(CommandSender sender, String source) {
        if (!(sender instanceof Player) || !source.equalsIgnoreCase("self") && !source.equals("")) {

            final Player player = Proton.getInstance().getServer().getPlayer(source);

            if (player != null && (!(sender instanceof Player) || Proton.getInstance().getVisibilityHandler().treatAsOnline(player, (Player)sender))) {
                return player;
            } else {
                sender.sendMessage(ChatColor.RED + "No player with the name \"" + source + "\" found.");
                return null;
            }
        } else {
            return (Player)sender;
        }
    }
}