package cc.fyre.proton.command.param.defaults;

import cc.fyre.proton.util.UUIDUtils;
import cc.fyre.proton.command.param.ParameterType;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class UUIDParameterType implements ParameterType<UUID> {

    public UUID transform(CommandSender sender, String source) {

        if (sender instanceof Player && (source.equalsIgnoreCase("self") || source.equals(""))) {
            return ((Player) sender).getUniqueId();
        }

        final UUID uuid = UUIDUtils.uuid(source);

        if (uuid == null) {
            sender.sendMessage(ChatColor.RED + source + " has never joined the server.");
            return null;
        }

        return uuid;
    }

}