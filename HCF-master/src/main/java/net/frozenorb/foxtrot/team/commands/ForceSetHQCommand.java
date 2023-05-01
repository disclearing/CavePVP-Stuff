package net.frozenorb.foxtrot.team.commands;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ForceSetHQCommand {

    @Command(names={ "forcesethq", "forcesethome" }, permission="foxtrot.command.forcesethq")
    public static void forceSetHome(Player player, @Parameter(name = "team", defaultValue = "self") Team team) {

        team.setHQ(player.getLocation());
        player.sendMessage(ChatColor.YELLOW + "Forcefully set the team's home.");

    }

}
