package net.frozenorb.foxtrot.team.commands.team;


import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.util.UUIDUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TeamCoLeaderCommand {

    @Command(names={ "team coleader add", "t coleader add", "t co-leader add", "team co-leader add", "f co-leader add", "fac co-leader add", "faction co-leader add", "f coleader add", "fac coleader add", "faction coleader add"}, permission="")
    public static void coleaderAdd(Player sender, @Parameter(name = "player") UUID promote) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender.getUniqueId());
        if( team == null ) {
            sender.sendMessage(ChatColor.RED + "You must be in a team to execute this command.");
            return;
        }

        if(!team.isOwner(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "Only the team owner can execute this command.");
            return;
        }

        if(!team.isMember(promote)) {
            sender.sendMessage(ChatColor.RED + "This player must be a member of your team.");
            return;
        }

        if(team.isOwner(promote) || team.isCoLeader(promote)) {
           sender.sendMessage(ChatColor.RED + "This player is already a co-leader (or above) of your team.");
            return;
        }

        team.addCoLeader(promote);
        team.removeCaptain(promote);
        team.sendMessage(org.bukkit.ChatColor.DARK_AQUA + UUIDUtils.name(promote) + " has been promoted to Co-Leader!");
    }

    @Command(names={ "team coleader remove", "t coleader remove", "t co-leader remove", "team co-leader remove", "f co-leader remove", "fac co-leader remove", "faction co-leader remove", "f coleader remove", "fac coleader remove", "faction coleader remove" }, permission="")
    public static void coleaderRemove(Player sender, @Parameter(name = "player") UUID demote) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender.getUniqueId());
        if( team == null ) {
            sender.sendMessage(ChatColor.RED + "You must be in a team to execute this command.");
            return;
        }

        if(!team.isOwner(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "Only the team owner can execute this command.");
            return;
        }

        if(!team.isMember(demote)) {
            sender.sendMessage(ChatColor.RED + "This player must be a member of your team.");
            return;
        }

        if(!team.isCoLeader(demote)) {
            sender.sendMessage(ChatColor.RED + "This player is not a co-leader of your team.");
            return;
        }

        team.removeCoLeader(demote);
        team.removeCaptain(demote);
        team.sendMessage(org.bukkit.ChatColor.DARK_AQUA + UUIDUtils.name(demote) + " has been demoted to a member!");
    }
}
