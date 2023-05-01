package net.frozenorb.foxtrot.team.commands.team;

import cc.fyre.proton.command.Command;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.upgrade.UpgradeMenu;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamUpgradesComand {
    @Command(names = {"team upgrades", "t upgrades", "f upgrades", "fac upgrades", "faction upgrades", "t upgrade", "team upgrade", "f upgrade", "fac upgrade", "faction upgrade"}, permission = "")
    public static void execute(Player player) {
        if (Foxtrot.getInstance().getServerHandler().isTeams()) {
            player.sendMessage(ChatColor.RED + "This feature is disabled on " + Foxtrot.getInstance().getServerHandler().getServerName() + ".");
            return;
        }

        final Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);

        if (team == null) {
            player.sendMessage(ChatColor.GRAY + "You are not in a faction!");
            return;
        }

        new UpgradeMenu(team).openMenu(player);
    }
}
