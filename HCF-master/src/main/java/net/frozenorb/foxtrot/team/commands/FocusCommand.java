package net.frozenorb.foxtrot.team.commands;

import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.nethandler.shared.LCPacketWaypointRemove;
import com.lunarclient.bukkitapi.object.LCWaypoint;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.listener.LunarClientListener;
import net.frozenorb.foxtrot.listener.StaffUtilsListener;
import net.frozenorb.foxtrot.team.Team;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.Proton;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.UUID;

public class FocusCommand {

    @Command(names = {"focus"}, description = "Focus a player", permission = "")
    public static void focus(Player sender, @Parameter(name = "player") Player target) {
        Team senderTeam = Foxtrot.getInstance().getTeamHandler().getTeam(sender);
        Team targetTeam = Foxtrot.getInstance().getTeamHandler().getTeam(target);

        if (senderTeam == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not in a faction!");
            return;
        }

        // There's a few ways this can go:
        // a. Target's team is null, in which case they can be targeted.
        // b. Target's team is not null, in which case...
        //      1. The teams are equal, where they can't be targeted.
        //      2. They aren't equal, in which case they can be targeted.
        // This if statement really isn't as complex as the above
        // comment made it sound, but it took me a few minutes of
        // thinking through, so this is just to save time.
        if (senderTeam == targetTeam) {
            sender.sendMessage(ChatColor.RED + "You cannot target a player on your faction.");
            return;
        }

        senderTeam.setFocused(target.getUniqueId());
        senderTeam.sendMessage(ChatColor.LIGHT_PURPLE + target.getName() + ChatColor.YELLOW + " has been focused by " + ChatColor.LIGHT_PURPLE + sender.getName() + ChatColor.YELLOW + ".");

        for (Player onlinePlayer : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
            if (senderTeam.isMember(onlinePlayer.getUniqueId())) {
                LunarClientListener.updateNametag(onlinePlayer);
            }
        }
    }

    @Command(names = "unfocus", description = "Remove player focus", permission = "")
    public static void unfocus(Player sender) {
        Team senderTeam = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (senderTeam == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not in a faction!");
            return;
        }

        UUID focused = senderTeam.getFocused();
        if (focused == null) {
            sender.sendMessage(ChatColor.RED + "You are not focusing anyone!");
            return;
        }

        senderTeam.setFocused(null);
        senderTeam.sendMessage(ChatColor.LIGHT_PURPLE + Proton.getInstance().getUuidCache().name(focused) + ChatColor.YELLOW + " has been unfocused by " + ChatColor.LIGHT_PURPLE + sender.getName() + ChatColor.YELLOW + ".");

        for (Player onlinePlayer : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
            if (senderTeam.isMember(onlinePlayer.getUniqueId())) {
                LunarClientListener.updateNametag(onlinePlayer);
            }
        }
    }

    @Command(names = {"team focus", "t focus", "f focus", "faction focus", "fac focus"}, permission = "")
    public static void execute(Player player, @Parameter(name = "team") String focusString) {

        final Player target = Foxtrot.getInstance().getServer().getPlayer(focusString);
        Team focusTeam;

        if (target != null) {
            focusTeam = Foxtrot.getInstance().getTeamHandler().getTeam(target);
        } else {
            focusTeam = Foxtrot.getInstance().getTeamHandler().getTeam(focusString);
        }

        if (focusTeam == null && focusString.equalsIgnoreCase("koth") && Foxtrot.getInstance().getEventHandler().getActiveKOTH() != null) {
            player.chat("/f focus " + Foxtrot.getInstance().getEventHandler().getActiveKOTH().getName());
            return;
        }

        if (focusTeam == null && StaffUtilsListener.lastPlayerHit.containsKey(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "No team or member with the name " + focusString + " found. Attempting to focus the last player you hit...");
            focusTeam = Foxtrot.getInstance().getTeamHandler().getTeam(StaffUtilsListener.lastPlayerHit.get(player.getUniqueId()));
        }

        if (focusTeam == null) {
            player.sendMessage(ChatColor.RED + "No team or member with the name " + focusString + " found.");
            return;
        }

        final Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);

        if (team == null) {
            player.sendMessage(ChatColor.RED + "You are not on a team!");
            return;
        }

        if (focusTeam == team) {
            player.sendMessage(ChatColor.RED + "You can't focus yourself!");
            return;
        }

        if (team.getFocusedTeam() != null && team.getFocusedTeam().getHQ() != null && team.getFocusWaypoint() != null) {
            team.getOnlineMembers().forEach(it -> LunarClientAPI.getInstance().removeWaypoint(player, team.getFocusWaypoint()));
        }

        final Team passFocusedTeam = team.getFocusedTeam();
        team.setFocusedTeam(focusTeam == team.getFocusedTeam() ? null : focusTeam);

        if (team.getFocusedTeam() != null && team.getFocusedTeam().getHQ() != null) {
            final Location location = team.getFocusedTeam().getHQ();
            team.setFocusWaypoint(new LCWaypoint(ChatColor.RED + team.getFocusedTeam().getName() + "'s HQ" + ChatColor.WHITE, location, Color.RED.getRGB(), true));

            team.getOnlineMembers().forEach(it -> LunarClientAPI.getInstance().sendWaypoint(player, team.getFocusWaypoint()));
        }

        if (passFocusedTeam != null) {
            passFocusedTeam.getOnlineMembers().forEach(LunarClientListener::updateNametag);
        }

        focusTeam.getOnlineMembers().forEach(LunarClientListener::updateNametag);

        team.sendMessage(ChatColor.YELLOW + "Team " + ChatColor.LIGHT_PURPLE + focusTeam.getName() + ChatColor.YELLOW + " has been " + (team.getFocusedTeam() == null ? "unfocused" : "focused") + " by " + ChatColor.LIGHT_PURPLE + player.getName() + ChatColor.YELLOW + ".");
    }

    @Command(names = {"team unfocus", "t unfocus", "f unfocus", "faction unfocus", "fac unfocus"}, permission = "")
    public static void unfocusTeam(Player player) {
        final Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);

        if (team == null) {
            player.sendMessage(ChatColor.RED + "You are not on a team!");
            return;
        }

        final Team focusedTeam = team.getFocusedTeam();

        if (focusedTeam == null) {
            player.sendMessage(ChatColor.RED + "You don't have any teams focused!");
            return;
        }

        if (focusedTeam.getHQ() != null && focusedTeam.getFocusWaypoint() != null) {
            team.getOnlineMembers().forEach(it -> LunarClientAPI.getInstance().removeWaypoint(it, focusedTeam.getFocusWaypoint()));
        }

        team.setFocusedTeam(null);

        focusedTeam.getOnlineMembers().forEach(LunarClientListener::updateNametag);

        team.sendMessage(ChatColor.YELLOW + "Team " + ChatColor.LIGHT_PURPLE + focusedTeam.getName() + ChatColor.YELLOW + " has been unfocused by " + ChatColor.LIGHT_PURPLE + player.getName() + ChatColor.YELLOW + ".");
    }
}