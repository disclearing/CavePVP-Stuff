package net.frozenorb.foxtrot.team.commands.team.subclaim;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.Subclaim;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TeamSubclaimRemovePlayerCommand {

    @Command(names={ "team subclaim removeplayer", "t subclaim removeplayer", "f subclaim removeplayer", "faction subclaim removeplayer", "fac subclaim removeplayer", "team sub removeplayer", "t sub removeplayer", "f sub removeplayer", "faction sub removeplayer", "fac sub removeplayer", "team subclaim revoke", "t subclaim revoke", "f subclaim revoke", "faction subclaim revoke", "fac subclaim revoke", "team sub revoke", "t sub revoke", "f sub revoke", "faction sub revoke", "fac sub revoke" }, permission="")
    public static void teamSubclaimRemovePlayer(Player sender, @Parameter(name="subclaim") Subclaim subclaim, @Parameter(name="player") UUID player) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (!team.isOwner(sender.getUniqueId()) && !team.isCoLeader(sender.getUniqueId()) && !team.isCaptain(sender.getUniqueId())) {
            sender.sendMessage(ChatColor.RED + "Only the team captains can do this.");
            return;
        }

        if (!team.isMember(player)) {
            sender.sendMessage(ChatColor.RED + UUIDUtils.name(player) + " is not on your team!");
            return;
        }

        if (!subclaim.isMember(player)) {
            sender.sendMessage(ChatColor.RED + "The player already does not have access to that subclaim!");
            return;
        }

        sender.sendMessage(ChatColor.GREEN + UUIDUtils.name(player) + ChatColor.YELLOW + " has been removed from the subclaim " + ChatColor.GREEN + subclaim.getName() + ChatColor.YELLOW + ".");
        subclaim.removeMember(player);
        team.flagForSave();
    }

}
