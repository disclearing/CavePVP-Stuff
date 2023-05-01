package net.frozenorb.foxtrot.team.commands.team;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeamSetTrapDoorCommand {
    @Command(names = {"team settrapdoor"}, permission = "team.command.settrapdoor")
    public static void execute(CommandSender sender, @Parameter(name = "target")Team team, @Parameter(name = "amount")int amount) {

        team.setTrapDoors(amount);
        sender.sendMessage(ChatColor.GREEN + "Set trap doors to " + amount);
    }

}
