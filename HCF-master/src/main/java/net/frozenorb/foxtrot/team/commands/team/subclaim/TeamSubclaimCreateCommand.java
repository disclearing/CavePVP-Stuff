package net.frozenorb.foxtrot.team.commands.team.subclaim;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.claims.Subclaim;
import net.frozenorb.foxtrot.util.CuboidRegion;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TeamSubclaimCreateCommand {

    @Command(names={ "team subclaim create", "t subclaim create", "f subclaim create", "faction subclaim create", "fac subclaim create", "team sub create", "t sub create", "f sub create", "faction sub create", "fac sub create" }, permission="")
    public static void teamSubclaimCreate(Player sender, @Parameter(name="subclaim") String subclaim) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.RED + "You must be on a team to execute this command!");
            return;
        }

        if (!StringUtils.isAlphanumeric(subclaim)) {
            sender.sendMessage(ChatColor.RED + "Subclaim names must be alphanumeric!");
            return;
        }

        if (!team.isOwner(sender.getUniqueId()) && !team.isCoLeader(sender.getUniqueId()) && !team.isCaptain(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "Only team captains can create subclaims!");
            return;
        }

        if (team.getSubclaim(subclaim) != null) {
            sender.sendMessage(ChatColor.RED + "Your team already has a subclaim with that name!");
            return;
        }

        if (!TeamSubclaimCommand.getSelections().containsKey(sender.getName()) || !TeamSubclaimCommand.getSelections().get(sender.getName()).isComplete()) {
            sender.sendMessage(ChatColor.RED + "You do not have a region fully selected!");
            return;
        }

        TeamSubclaimCommand.Selection selection = TeamSubclaimCommand.getSelections().get(sender.getName());
        int x = Math.abs(selection.getLoc1().getBlockX() - selection.getLoc2().getBlockX());
        int z = Math.abs(selection.getLoc1().getBlockZ() - selection.getLoc2().getBlockZ());

        if (x < 3 || z < 3) {
            sender.sendMessage(ChatColor.RED + "Subclaims must be at least 3x3.");
            return;
        }

        for (Location loc : new CuboidRegion("Subclaim", selection.getLoc1(), selection.getLoc2())) {
            if (LandBoard.getInstance().getTeam(loc) != team) {
                sender.sendMessage(ChatColor.RED + "This subclaim would conflict with the claims of team §e" + LandBoard.getInstance().getTeam(loc).getName() + "§c!");
                return;
            }

            Subclaim subclaimAtLoc = team.getSubclaim(loc);

            if (subclaimAtLoc != null) {
                sender.sendMessage(ChatColor.RED + "This subclaim would conflict with " + ChatColor.YELLOW + subclaimAtLoc.getName() + ChatColor.RED + "!");
                return;
            }
        }

        Subclaim sc = new Subclaim(selection.getLoc1(), selection.getLoc2(), subclaim);

        team.getSubclaims().add(sc);
        LandBoard.getInstance().updateSubclaim(sc);
        team.flagForSave();

        sender.sendMessage(ChatColor.GREEN + "You have created the subclaim " + ChatColor.YELLOW + sc.getName() + ChatColor.GREEN + "!");
        sender.getInventory().remove(TeamSubclaimCommand.SELECTION_WAND);
    }

}