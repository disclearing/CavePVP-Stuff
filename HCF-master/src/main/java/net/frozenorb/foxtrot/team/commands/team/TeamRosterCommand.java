package net.frozenorb.foxtrot.team.commands.team;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.util.UUIDUtils;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Role;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.event.FullTeamBypassEvent;
import net.frozenorb.foxtrot.team.menu.roster.TeamRosterMenu;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TeamRosterCommand {
    @Command(names = {"t roster", "team roster", "f roster", "fac roster", "faction roster"}, permission = "")
    public static void execute(Player player) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);

        if (team == null) {
            player.sendMessage(ChatColor.GRAY + "You are not in a faction!");
            return;
        }

        if (!team.isOwner(player.getUniqueId())) {
            player.sendMessage(ChatColor.DARK_AQUA + "Only faction leaders can do this.");
            return;
        }

        new TeamRosterMenu(team).openMenu(player);
    }



    @Command(names = {"t roster add", "team roster add", "f roster add", "fac roster add", "faction roster add"}, permission = "")
    public static void add(Player sender, @Parameter(name = "player") UUID target) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not in a faction!");
            return;
        }

        if (!(team.isOwner(sender.getUniqueId()))) {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only faction co-leaders can do this.");
            return;
        }

        if (team.getRoster().containsKey(target)) {
            sender.sendMessage(ChatColor.RED + "That player is already in your roster.");
            return;
        }

        final Player targetPlayer = Foxtrot.getInstance().getServer().getPlayer(target);

        if (targetPlayer != null) {
            targetPlayer.sendMessage("");
            targetPlayer.sendMessage(ChatColor.translate("&cYou have been invited to &f" + team.getName() + "'s roster&c!"));
            targetPlayer.sendMessage(ChatColor.GRAY + "You can now join and leave at any time! Type /f join [name]!");
            targetPlayer.sendMessage("");
        }

        team.getRoster().put(target, Role.MEMBER);
        team.flagForSave();

        team.sendMessage(ChatColor.translate("&3" + UUIDUtils.name(target) + " has been added to your faction's roster! They may now join at any time!"));
    }

    @Command(names = {"t roster fill", "team roster fill", "f roster fill", "fac roster fill", "faction roster fill"}, permission = "")
    public static void fill(Player sender) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not in a faction!");
            return;
        }

        if (!(team.isOwner(sender.getUniqueId()))) {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only faction co-leaders can do this.");
            return;
        }

        for (UUID captain : team.getCaptains()) {
            team.sendMessage(ChatColor.translate("&3" + UUIDUtils.name(captain) + " has been added to your faction's roster! They may now join at any time!"));
            team.getRoster().put(captain, Role.CAPTAIN);
        }

        for (UUID member : team.getMembers()) {
            team.sendMessage(ChatColor.translate("&3" + UUIDUtils.name(member) + " has been added to your faction's roster! They may now join at any time!"));
            team.getRoster().put(member, Role.MEMBER);
        }

        team.flagForSave();
    }

    @Command(names = {"t roster remove", "team roster remove", "f roster remove", "fac roster remove", "faction roster remove"}, permission = "")
    public static void remove(Player sender, @Parameter(name = "player") UUID target) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not in a faction!");
            return;
        }

        if (!(team.isOwner(sender.getUniqueId()) || team.isCoLeader(sender.getUniqueId()))) {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only faction co-leaders can do this.");
            return;
        }

        if (!team.getRoster().containsKey(target)) {
            sender.sendMessage(ChatColor.RED + "That player is not in your roster!");
            return;
        }

        final Player targetPlayer = Foxtrot.getInstance().getServer().getPlayer(target);

        if (targetPlayer != null) {
            targetPlayer.sendMessage("");
            targetPlayer.sendMessage(ChatColor.translate("&cYou have been removed from &f" + team.getName() + "'s roster&c!"));
            targetPlayer.sendMessage("");
        }

        team.getRoster().remove(target);
        team.flagForSave();
        sender.sendMessage(ChatColor.RED + "Note that removing a player from your faction's roster doesn't kick them! You must /f kick [name]!");
    }

    @Command(names = {"t roster clear", "team roster clear", "f roster clear", "fac roster clear", "faction roster clear"}, permission = "")
    public static void clear(Player sender) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not in a faction!");
            return;
        }

        if (!(team.isOwner(sender.getUniqueId()) || team.isCoLeader(sender.getUniqueId()))) {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only faction co-leaders can do this.");
            return;
        }
        
        team.sendMessage(sender.getName() + ChatColor.RED + " has cleared the roster list!");
        sender.sendMessage(ChatColor.RED + "Note that removing players from your faction's roster doesn't kick them! You must /f kick [name]!");
        team.getRoster().clear();
        team.flagForSave();
    }
}
