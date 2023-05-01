package net.frozenorb.foxtrot.team.commands.team;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamAddPointsCommand {

    @Command(names={ "team addpoints", "t addpoints", "f addpoints", "faction addpoints", "fac addpoints" }, permission="foxtrot.addpoints")
    public static void teamAddPoints(Player sender, @Parameter(name = "team") final Team team, @Parameter(name = "amount") int amount) {
        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "That team doesn't exist!");
            return;
        }

        team.setAddedPoints(team.getAddedPoints() + amount);
        sender.sendMessage(ChatColor.translate("&eYou have successfully added &b" + amount + "&e points to &a" + team.getName() + "&e."));
    }
}