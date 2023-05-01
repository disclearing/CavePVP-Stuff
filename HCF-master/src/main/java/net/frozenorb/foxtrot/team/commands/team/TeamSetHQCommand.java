package net.frozenorb.foxtrot.team.commands.team;

import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.object.LCWaypoint;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import cc.fyre.proton.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.awt.*;

@SuppressWarnings("deprecation")
public class TeamSetHQCommand {

    @Command(names = {"team sethq", "t sethq", "f sethq", "faction sethq", "fac sethq", "team sethome", "t sethome", "f sethome", "faction sethome", "fac sethome", "sethome", "sethq"}, permission = "")
    public static void teamSetHQ(Player sender) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not in a faction!");
            return;
        }

        if (team.isOwner(sender.getUniqueId()) || team.isCoLeader(sender.getUniqueId()) || team.isCaptain(sender.getUniqueId())) {
            if (LandBoard.getInstance().getTeam(sender.getLocation()) != team) {
                if (!sender.isOp()) {
                    sender.sendMessage(ChatColor.RED + "You can only set HQ in your faction's territory.");
                    return;
                } else {
                    sender.sendMessage(ChatColor.RED.toString() + ChatColor.ITALIC + "Setting HQ outside of your faction's territory would normally be disallowed, but this check is being bypassed due to your rank.");
                }
            }


            team.setHQ(sender.getLocation());
            team.sendMessage(ChatColor.DARK_AQUA + sender.getName() + " has updated the team's HQ point!");

            if (team.getHomeWaypoint() == null) {
                team.setHomeWaypoint(new LCWaypoint(ChatColor.BLUE + "HQ" + ChatColor.WHITE, team.getHQ(), java.awt.Color.BLUE.getRGB(), true));
            } else {
                LunarClientAPI.getInstance().removeWaypoint(sender, team.getHomeWaypoint());
            }

            LunarClientAPI.getInstance().sendWaypoint(sender, team.getHomeWaypoint());
        } else {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only faction captains can do this.");
        }
    }
}