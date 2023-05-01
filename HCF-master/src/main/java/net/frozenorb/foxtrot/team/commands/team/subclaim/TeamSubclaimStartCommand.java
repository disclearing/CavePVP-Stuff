package net.frozenorb.foxtrot.team.commands.team.subclaim;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.VisualClaim;
import net.frozenorb.foxtrot.team.claims.VisualClaimType;
import cc.fyre.proton.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamSubclaimStartCommand {

    @Command(names={ "team subclaim start", "t subclaim start", "f subclaim start", "faction subclaim start", "fac subclaim start", "team sub start", "t sub start", "f sub start", "faction sub start", "fac sub start" }, permission="")
    public static void teamSubclaimStart(Player sender) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.RED + "You must be on a team to execute this command!");
            return;
        }

        if (!team.isCaptain(sender.getUniqueId()) && !team.isCoLeader(sender.getUniqueId()) && !team.isOwner(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.DARK_AQUA + "Only team captains can do this.");
            return;
        }

        int slot = -1;

        for (int i = 0; i < 9; i++) {
            if (sender.getInventory().getItem(i) == null) {
                slot = i;
                break;
            }
        }

        if (slot == -1) {
            sender.sendMessage(ChatColor.RED + "You don't have space in your hotbar for the subclaim wand!");
            return;
        }

        if (!VisualClaim.getCurrentSubclaimMaps().containsKey(sender.getName())) {
            new VisualClaim(sender, VisualClaimType.SUBCLAIM_MAP, true).draw(true);
        }

        sender.getInventory().setItem(slot, TeamSubclaimCommand.SELECTION_WAND.clone());
        sender.sendMessage(ChatColor.GREEN + "Gave you a subclaim wand.");
    }

}