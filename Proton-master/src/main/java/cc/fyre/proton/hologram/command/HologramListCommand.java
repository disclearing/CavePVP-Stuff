package cc.fyre.proton.hologram.command;

import cc.fyre.proton.Proton;
import cc.fyre.proton.command.Command;
import mkremins.fanciful.FancyMessage;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * @author xanderume@gmail (JavaProject)
 */
public class HologramListCommand {

    @Command(
            names = {"hologram list","holo list","holograms","holos"},
            permission = "proton.command.hologram.list"
    )
    public static void execute(CommandSender sender) {
        sender.sendMessage(ChatColor.BLUE + ChatColor.STRIKETHROUGH.toString() + StringUtils.repeat("-",48));

        Proton.getInstance().getHologramHandler().getCache().forEach((key,value) -> new FancyMessage(ChatColor.GRAY + "-> " + ChatColor.RED + key + "" + (!value.getLines().isEmpty() ? ChatColor.GRAY + " - " + value.getLines().get(0) : "")).command("/tppos " + value.getLocation().getX() + " " + value.getLocation().getY() + " " + value.getLocation().getZ()).send(sender));

        sender.sendMessage(ChatColor.BLUE + ChatColor.STRIKETHROUGH.toString() + StringUtils.repeat("-",48));
    }

}
