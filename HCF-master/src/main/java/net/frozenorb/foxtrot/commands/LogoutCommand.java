package net.frozenorb.foxtrot.commands;

import cc.fyre.piston.PistonConstants;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.server.ServerHandler;
import cc.fyre.proton.command.Command;

public class LogoutCommand {

    @Command(names={ "Logout" }, permission="")
    public static void logout(Player sender) {
        if (sender.hasMetadata(PistonConstants.FREEZE_METADATA)) {
            sender.sendMessage(ChatColor.RED + "You can't log out while you're frozen!");
            return;
        }

        if(ServerHandler.getTasks().containsKey(sender.getName())) {
            sender.sendMessage(ChatColor.RED + "You are already logging out.");
            return; // dont potato and let them spam logouts
        }

        Foxtrot.getInstance().getServerHandler().startLogoutSequence(sender);
    }

}