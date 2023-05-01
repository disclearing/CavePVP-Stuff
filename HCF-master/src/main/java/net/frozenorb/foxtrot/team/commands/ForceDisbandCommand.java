package net.frozenorb.foxtrot.team.commands;

import net.frozenorb.foxtrot.team.Team;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ForceDisbandCommand {

    @Command(names={ "forcedisband" }, permission="op")
    public static void forceDisband(Player sender, @Parameter(name="team") Team team) {
        if (!sender.getName().equalsIgnoreCase("SimplyTrash")) {
            sender.sendMessage(ChatColor.RED + "No can do buckaroo!");
            return;
        }

        team.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + sender.getName() + " has force disbanded the team.");
        team.disband();
        sender.sendMessage(ChatColor.YELLOW + "Force disbanded the team " + ChatColor.LIGHT_PURPLE + team.getName() + ChatColor.YELLOW + ".");
    }

}