package cc.fyre.piston.command.staff;

import cc.fyre.neutron.NeutronConstants;
import cc.fyre.piston.packet.StaffBroadcastPacket;
import cc.fyre.proton.Proton;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class StaffBroadcastCommand {
    @Command(names = {"staffbroadcast"}, async = true, permission = "op")
    public static void execute(CommandSender commandSender, @Parameter(name = "message", wildcard = true)String message) {
        Proton.getInstance().getPidginHandler().sendPacket(new StaffBroadcastPacket(NeutronConstants.STAFF_PERMISSION,
                ChatColor.translate(ChatColor.translate(message)))
        );

        commandSender.sendMessage(ChatColor.GREEN + "Dispatched staff broadcast packet");
    }
}
