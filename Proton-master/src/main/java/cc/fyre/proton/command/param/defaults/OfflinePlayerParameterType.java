package cc.fyre.proton.command.param.defaults;

import cc.fyre.proton.Proton;
import cc.fyre.proton.command.param.ParameterType;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OfflinePlayerParameterType implements ParameterType<OfflinePlayer> {

    public OfflinePlayer transform(CommandSender sender, String source) {

        if (sender instanceof Player && (source.equalsIgnoreCase("self") || source.equals(""))) {
            return ((Player) sender);
        }

        return Proton.getInstance().getServer().getOfflinePlayer(source);
    }

}