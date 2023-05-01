package net.frozenorb.foxtrot.team.commands.team;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.VisualClaim;
import net.frozenorb.foxtrot.team.claims.VisualClaimType;
import cc.fyre.proton.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class TeamOpClaimCommand {

    @Command(names={ "team opclaim", "t opclaim", "f opclaim", "faction opclaim", "fac opclaim" }, permission="worldedit.*")
    public static void teamOpClaim(final Player sender) {
        Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

        if (team == null) {
            sender.sendMessage(ChatColor.GRAY + "You are not in a faction!");
            return;
        }

        sender.getInventory().remove(TeamClaimCommand.SELECTION_WAND);

        new BukkitRunnable() {

            public void run() {
                sender.getInventory().addItem(TeamClaimCommand.SELECTION_WAND.clone());
            }

        }.runTaskLater(Foxtrot.getInstance(), 1L);

        new VisualClaim(sender, VisualClaimType.CREATE, true).draw(false);

        if (!VisualClaim.getCurrentMaps().containsKey(sender.getName())) {
            new VisualClaim(sender, VisualClaimType.MAP, true).draw(true);
        }
    }

}