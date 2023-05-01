package net.frozenorb.foxtrot.team.commands.team;

import net.frozenorb.foxtrot.team.Team;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TeamTPCommand {

    @Command(names={ "team tp", "t tp", "f tp", "faction tp", "fac tp" }, permission="foxtrot.factiontp")
    public static void teamTP(Player sender, @Parameter(name="team", defaultValue="self") Team team) {
        if (team.getHQ() != null) {
            sender.sendMessage(ChatColor.YELLOW + "Teleported to " + ChatColor.LIGHT_PURPLE + team.getName() + ChatColor.YELLOW + "'s HQ.");
            sender.teleport(team.getHQ());
        } else if (team.getClaims().size() != 0) {
            sender.sendMessage(ChatColor.YELLOW + "Teleported to " + ChatColor.LIGHT_PURPLE + team.getName() + ChatColor.YELLOW + "'s claim.");

            final Location location = team.getClaims().get(0).getMaximumPoint().add(0, 100, 0);

            sender.teleport(location.getWorld().getHighestBlockAt(location).getLocation());
        } else {
            sender.sendMessage(ChatColor.LIGHT_PURPLE + team.getName() + ChatColor.YELLOW + " doesn't have a HQ or any claims.");
        }
    }

}
