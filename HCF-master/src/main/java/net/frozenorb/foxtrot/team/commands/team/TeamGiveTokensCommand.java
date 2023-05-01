package net.frozenorb.foxtrot.team.commands.team;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeamGiveTokensCommand {
    @Command(names={ "givebasetokens" }, permission="op")
    public static void basetokens(CommandSender sender, @Parameter(name = "team") final Team team, @Parameter(name = "amount") int amount) {
        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "That team doesn't exist!");
            return;
        }

        team.setBaseTokens(team.getBaseTokens() + amount);
        sender.sendMessage(ChatColor.translate("&eYou have successfully added &b" + amount + "&e base tokens to &a" + team.getName() + "&e."));
    }

    @Command(names={ "givefallertokens" }, permission="op")
    public static void fallertokens(CommandSender sender, @Parameter(name = "team") final Team team, @Parameter(name = "amount") int amount) {
        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "That team doesn't exist!");
            return;
        }

        team.setFallTrapTokens(team.getFallTrapTokens() + amount);
        sender.sendMessage(ChatColor.translate("&eYou have successfully added &b" + amount + "&e faller tokens to &a" + team.getName() + "&e."));
    }
}
