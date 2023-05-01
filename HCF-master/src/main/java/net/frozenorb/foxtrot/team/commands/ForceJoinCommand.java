package net.frozenorb.foxtrot.team.commands;

import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.object.LCWaypoint;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.listener.LunarClientListener;
import net.frozenorb.foxtrot.team.Team;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.awt.*;

public class ForceJoinCommand {

    @Command(names={ "ForceJoin" }, permission="foxtrot.forcejoin")
    public static void forceJoin(Player sender, @Parameter(name="team") Team team, @Parameter(name="player", defaultValue="self") Player player) {
        if (Foxtrot.getInstance().getTeamHandler().getTeam(player) != null) {
            if (player == sender) {
                sender.sendMessage(ChatColor.RED + "Leave your current team before attempting to forcejoin.");
            } else {
                sender.sendMessage(ChatColor.RED + "That player needs to leave their current team first!");
            }

            return;
        }

        team.addMember(player.getUniqueId());
        Foxtrot.getInstance().getTeamHandler().setTeam(player.getUniqueId(), team);
        player.sendMessage(ChatColor.YELLOW + "You are now a member of " + ChatColor.LIGHT_PURPLE + team.getName() + ChatColor.YELLOW + "!");

        if (team.getHQ() != null) {
            if (team.getHomeWaypoint() == null) {
                team.setHomeWaypoint(new LCWaypoint(ChatColor.BLUE + "HQ" + ChatColor.WHITE, team.getHQ(), java.awt.Color.BLUE.getRGB(), true));
            }

            LunarClientAPI.getInstance().sendWaypoint(player, team.getHomeWaypoint());
        }

        if (team.getRallyPoint() != null) {
            if (team.getRallyWaypoint() == null) {
                team.setRallyWaypoint(new LCWaypoint(ChatColor.GOLD + "Rally" + ChatColor.WHITE, team.getRallyPoint(), java.awt.Color.ORANGE.getRGB(), true));
            }

            LunarClientAPI.getInstance().sendWaypoint(player, team.getRallyWaypoint());
        }

        if (team.getFocusedTeam() != null && team.getFocusedTeam().getHQ() != null) {
            final Location location = team.getFocusedTeam().getHQ();

            if (team.getFocusWaypoint() == null) {
                team.setFocusWaypoint(new LCWaypoint(ChatColor.RED + team.getFocusedTeam().getName() + "'s HQ" + ChatColor.WHITE, location, Color.RED.getRGB(), true));
            }

            LunarClientAPI.getInstance().sendWaypoint(player, team.getFocusWaypoint());
        }

        LunarClientListener.updateNametag(player);

        if (player != sender) {
            sender.sendMessage(ChatColor.LIGHT_PURPLE + player.getName() + ChatColor.YELLOW + " added to " + ChatColor.LIGHT_PURPLE + team.getName() + ChatColor.YELLOW + "!");
        }
    }

}