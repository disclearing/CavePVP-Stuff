package cc.fyre.piston.command.admin;

import cc.fyre.piston.Piston;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.util.ReflectionUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class SetSlotsCommand {

    @Command(
            names = {"setslots", "setmaxslots", "setservercap", "ssc"},
            permission = "command.setslots"
    )
    public static void execute(CommandSender sender,@Parameter(name = "slots") int slots) {

        if (slots < 0) {
            sender.sendMessage(ChatColor.RED + "The number of slots must be greater or equal to zero.");
            return;
        }

        ReflectionUtil.setMaxPlayers(Piston.getInstance().getServer(),slots);

        Piston.getInstance().getConfig().set("server.slots",slots);
        Piston.getInstance().saveConfig();

        sender.sendMessage(ChatColor.GOLD + "Slots set to " + ChatColor.WHITE + slots + ChatColor.GOLD + ".");
    }

}
