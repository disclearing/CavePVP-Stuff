package net.frozenorb.foxtrot.gameplay.events.koth.commands.koth;

import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import net.frozenorb.foxtrot.gameplay.events.Event;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;

public class KOTHDeactivateCommand {

    @Command(names={ "KOTH Deactivate", "KOTH Inactive", "event deactivate" }, permission="foxtrot.koth.admin")
    public static void kothDectivate(CommandSender sender, @Parameter(name="koth") Event koth) {
        if (!koth.isActive()) {
            sender.sendMessage(ChatColor.RED + "That event isn't active!");
            return;
        }

        koth.deactivate();

        Foxtrot.getInstance().getServer().broadcastMessage(sender.getName() + ChatColor.GOLD + " has cancelled " + ChatColor.WHITE + koth.getName() + ChatColor.GOLD + ".");
//        Foxtrot.getInstance().getServer().getOnlinePlayers().forEach(it -> LunarClientAPI.Companion.getInstance().getPacketHandler().sendPacket(it, new WayPointRemovePacket(((KOTH)koth).getWaypointChatColor() + koth.getName() + ChatColor.GOLD + " KOTH" + ChatColor.WHITE, it.getWorld())));
    }

}
