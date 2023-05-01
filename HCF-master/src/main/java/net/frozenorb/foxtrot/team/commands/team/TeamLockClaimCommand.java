package net.frozenorb.foxtrot.team.commands.team;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.Claim;
import cc.fyre.proton.command.Command;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

public class TeamLockClaimCommand {
	@Command(names={ "team lockclaim", "t lockclaim", "f lockclaim", "faction lockclaim", "fac lockclaim"}, permission="")
	public static void teamLockClaim(Player sender) {
		Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

		if (team == null) {
			sender.sendMessage(ChatColor.GRAY + "You are not in a faction!");
			return;
		}

		if (!CustomTimerCreateCommand.isSOTWTimer()) {
			sender.sendMessage(ChatColor.RED + "This command can only be used during SOTW.");
			return;
		}

		if (CustomTimerCreateCommand.remainingSOTWTime() <= TimeUnit.MINUTES.toMillis(10)) {
			sender.sendMessage(ChatColor.RED + "This command cannot be used this late into SOTW.");
			return;
		}

		if (!(team.isOwner(sender.getUniqueId()) || team.isCoLeader(sender.getUniqueId()))) {
			sender.sendMessage(ChatColor.DARK_AQUA + "Only faction co-leaders (and above) can do this.");
			return;
		}

		if (team.isClaimLocked()) {
			sender.sendMessage(ChatColor.RED + "Claim is already locked.");
			return;
		}

		for (Claim claim : team.getClaims()) {
			for (Player player : claim.getPlayers()) {
				if (!team.isMember(player.getUniqueId())) {
					Location safeLocation = TeamStuckCommand.nearestSafeLocation(player.getLocation());
					player.teleport(safeLocation);
					player.sendMessage(ChatColor.RED + "You have been teleported out because that claim was locked!");
				}
			}
		}

		team.setClaimLocked(true);
		team.sendMessage(sender.getDisplayName() + CC.translate(" &clocked &eyour faction's claim! &f(Claims will &aunlock &f10 minutes before SOTW ends)"));
	}

	@Command(names={ "team unlockclaim", "t unlockclaim", "f unlockclaim", "faction unlockclaim", "fac unlockclaim"}, permission="")
	public static void teamUnLockClaim(Player sender) {
		Team team = Foxtrot.getInstance().getTeamHandler().getTeam(sender);

		if (team == null) {
			sender.sendMessage(ChatColor.GRAY + "You are not on a faction!");
			return;
		}

		if (!CustomTimerCreateCommand.isSOTWTimer()) {
			sender.sendMessage(ChatColor.RED + "This command can only be used during SOTW.");
			return;
		}

		if (!(team.isOwner(sender.getUniqueId()) || team.isCoLeader(sender.getUniqueId()))) {
			sender.sendMessage(ChatColor.DARK_AQUA + "Only faction co-leaders (and above) can do this.");
			return;
		}

		if (!team.isClaimLocked()) {
			sender.sendMessage(ChatColor.RED + "Claim is already unlocked.");
			return;
		}

		team.setClaimLocked(false);
		team.sendMessage(sender.getName() + CC.translate(" &aunlocked &eyour faction’s claim!"));
	}
}
