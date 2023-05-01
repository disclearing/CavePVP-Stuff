package net.frozenorb.foxtrot.team.commands;

import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.TeamHandler;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PowerFactionCommand {

    @Command(names={ "powerfaction add", "team powerfaction add", "pf add", "powerfac add" }, permission="foxtrot.powerfactions")
    public static void powerFactionAdd(Player sender, @Parameter(name="team") Team team) {
        team.setPowerFaction(true);
        sender.sendMessage(ChatColor.LIGHT_PURPLE + team.getName() + ChatColor.YELLOW + " is now a power faction!");
    }

    @Command(names={ "powerfaction remove", "team powerfaction remove", "pf remove", "powerfac remove" }, permission="foxtrot.powerfactions")
    public static void powerFactionRemove(Player sender, @Parameter(name="team") Team team) {
        team.setPowerFaction(false);
        sender.sendMessage(ChatColor.LIGHT_PURPLE + team.getName() + ChatColor.YELLOW + " is no longer a power faction!");
    }

    @Command(names={ "powerfaction list", "team powerfaction list", "pf list", "powerfac list" }, permission="foxtrot.powerfactions")
    public static void powerFactionList(Player sender) {
        sender.sendMessage(ChatColor.YELLOW + "Found " + ChatColor.RED + TeamHandler.getPowerFactions().size() + ChatColor.YELLOW + " Power Factions.");
        int i = 1;
        for( Team t : TeamHandler.getPowerFactions() ) {
            sender.sendMessage(ChatColor.YELLOW + "" + i + ". " + ChatColor.RED + t.getName());
            i++;
        }
    }
}