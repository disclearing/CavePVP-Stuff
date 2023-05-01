package net.frozenorb.foxtrot.team.commands;

import net.frozenorb.foxtrot.team.Team;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class SetTeamBalanceCommand {

    @Command(names={ "setteambalance", "setteambal" }, permission="foxtrot.setteambalance")
    public static void setTeamBalance(Player sender, @Parameter(name="team") Team team,  @Parameter(name="balance") float balance) {
        team.setBalance(balance);
        sender.sendMessage(ChatColor.LIGHT_PURPLE + team.getName() + ChatColor.YELLOW + "'s balance is now " + ChatColor.LIGHT_PURPLE + team.getBalance() + ChatColor.YELLOW + ".");
    }

}