package cc.fyre.piston.command.staff;

import cc.fyre.piston.Piston;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ToggleStaffCommand {
    @Command(names = {"togglestaff"}, permission = "neutron.staff")
    public static void execute(Player player) {

        if (Piston.getInstance().getToggleStaff().contains(player.getUniqueId())) {
            Piston.getInstance().getToggleStaff().remove(player.getUniqueId());
            player.sendMessage(ChatColor.GREEN + "You have enabled staff-chat, requests and reports.");
            return;
        }

        Piston.getInstance().getToggleStaff().add(player.getUniqueId());
        player.sendMessage(ChatColor.RED + "You have disabled staffchat, requests and reports.");
        if(player.hasMetadata("piston:staffchat"))
            player.removeMetadata("piston:staffchat", Piston.getInstance());
    }
}
