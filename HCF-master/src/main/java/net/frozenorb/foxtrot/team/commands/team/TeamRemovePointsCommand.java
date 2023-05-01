package net.frozenorb.foxtrot.team.commands.team;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamRemovePointsCommand {

    @Command(names={ "team removepoints", "t removepoints", "f removepoints", "faction removepoints", "fac removepoints" }, permission="foxtrot.removepoints")
    public static void teamRemovePoints(Player sender, @Parameter(name = "team") final Team team, @Parameter(name = "amount") int amount) {
        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "That team doesn't exist!");
            return;
        }

        team.setRemovedPoints(team.getRemovedPoints() + amount);
        sender.sendMessage(ChatColor.translate("&eYou have successfully &cremoved &b" + amount + "&e points from &a" + team.getName() + "&e."));
    }
}