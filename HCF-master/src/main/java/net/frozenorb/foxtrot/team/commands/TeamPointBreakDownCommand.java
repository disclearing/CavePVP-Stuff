package net.frozenorb.foxtrot.team.commands;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.menu.PointBreakDownMenu;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamPointBreakDownCommand {

	@Command(names = { "f pbr", "faction pbr", "fac pbr", "t pbr"}, permission = "op")
	public static void teamPointBreakDown(Player player, @Parameter(name="team", defaultValue="self") final Team team) {
		player.sendMessage(ChatColor.GOLD + "Point Breakdown of " + team.getName());

		int points = team.getPoints();

		team.recalculatePoints();

		if (points != team.getPoints()) {
			team.flagForSave();
		}

		new PointBreakDownMenu(team, team.getKills(), team.getDeaths(), team.getKothCaptures()*10, team.getCitadelsCapped()*60, team.getConquestsCapped()*125, team.getDoublePoints(), team.getAddedPoints(), team.getRemovedPoints()).openMenu(player);
	}

}