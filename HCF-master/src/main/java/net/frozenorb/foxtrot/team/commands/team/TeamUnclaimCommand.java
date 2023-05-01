package net.frozenorb.foxtrot.team.commands.team;

import net.minecraft.util.com.google.common.collect.ImmutableMap;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.Claim;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.claims.Subclaim;
import net.frozenorb.foxtrot.team.track.TeamActionTracker;
import net.frozenorb.foxtrot.team.track.TeamActionType;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class TeamUnclaimCommand {

    @Command(names={ "team unclaim", "t unclaim", "f unclaim", "faction unclaim", "fac unclaim" }, permission="")
    public static void teamUnclaim(Player sender, @Parameter(name="all?", defaultValue="not_all?") String all) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not in a faction!");
            return;
        }

        if (!(team.isOwner(sender.getUniqueId()) || team.isCoLeader(sender.getUniqueId()))) {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only faction co-leaders can do this.");
            return;
        }

        if (team.isRaidable()) {
            sender.sendMessage(ChatColor.RED + "You may not unclaim land while your faction is raidable!");
            return;
        }

        if (all.equalsIgnoreCase("all")) {
            int claims = team.getClaims().size();
            int refund = 0;

            for (Claim claim : team.getClaims()) {
                refund += Claim.getPrice(claim, team, false);

                Location minLoc = claim.getMinimumPoint();
                Location maxLoc = claim.getMaximumPoint();

                TeamActionTracker.logActionAsync(team, TeamActionType.PLAYER_UNCLAIM_LAND, ImmutableMap.of(
                        "playerId", sender.getUniqueId(),
                        "playerName", sender.getName(),
                        "refund", Claim.getPrice(claim, team, false),
                        "point1", minLoc.getBlockX() + ", " + minLoc.getBlockY() + ", " + minLoc.getBlockZ(),
                        "point2", maxLoc.getBlockX() + ", " + maxLoc.getBlockY() + ", " + maxLoc.getBlockZ()
                ));
            }

            team.setBalance(team.getBalance() + refund);
            LandBoard.getInstance().clear(team);
            team.getClaims().clear();

            for (Subclaim subclaim : team.getSubclaims()) {
                LandBoard.getInstance().updateSubclaim(subclaim);
            }

            team.getSubclaims().clear();
            team.setHQ(null);
            team.flagForSave();

            for (Player player : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
                if (team.isMember(player.getUniqueId())) {
                    player.sendMessage(ChatColor.YELLOW + sender.getName() + " has unclaimed all of your faction's claims. (" + ChatColor.LIGHT_PURPLE + claims + " total" + ChatColor.YELLOW + ")");
                }
            }

            return;
        }

        if (LandBoard.getInstance().getClaim(sender.getLocation()) != null && team.ownsLocation(sender.getLocation())) {
            Claim claim = LandBoard.getInstance().getClaim(sender.getLocation());
            int refund = Claim.getPrice(claim, team, false);

            team.setBalance(team.getBalance() + refund);
            team.getClaims().remove(claim);

            for (Subclaim subclaim : new ArrayList<>(team.getSubclaims())) {
                if (claim.contains(subclaim.getLoc1()) || claim.contains(subclaim.getLoc2())) {
                    team.getSubclaims().remove(subclaim);
                    LandBoard.getInstance().updateSubclaim(subclaim);
                }
            }

            team.sendMessage(ChatColor.YELLOW + sender.getName() + " has unclaimed " + ChatColor.LIGHT_PURPLE + claim.getFriendlyName() + ChatColor.YELLOW + ".");
            team.flagForSave();

            LandBoard.getInstance().setTeamAt(claim, null);

            Location minLoc = claim.getMinimumPoint();
            Location maxLoc = claim.getMaximumPoint();

            TeamActionTracker.logActionAsync(team, TeamActionType.PLAYER_UNCLAIM_LAND, ImmutableMap.of(
                    "playerId", sender.getUniqueId(),
                    "playerName", sender.getName(),
                    "refund", Claim.getPrice(claim, team, false),
                    "point1", minLoc.getBlockX() + ", " + minLoc.getBlockY() + ", " + minLoc.getBlockZ(),
                    "point2", maxLoc.getBlockX() + ", " + maxLoc.getBlockY() + ", " + maxLoc.getBlockZ()
            ));

            if (team.getHQ() != null && claim.contains(team.getHQ())) {
                team.setHQ(null);
                sender.sendMessage(ChatColor.RED + "Your HQ was in this claim, so it has been unset.");
            }

            return;
        }

        sender.sendMessage(ChatColor.RED + "You do not own this claim.");
        sender.sendMessage(ChatColor.RED + "To unclaim all claims, type " + ChatColor.YELLOW + "/f unclaim all" + ChatColor.RED + ".");
    }

}