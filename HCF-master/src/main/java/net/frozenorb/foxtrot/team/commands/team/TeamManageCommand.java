package net.frozenorb.foxtrot.team.commands.team;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.util.UUIDUtils;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.menu.manage.ManageMenu;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamManageCommand {
    @Command(names = {"team manage", "t manage", "f manage", "fac manage", "faction manage"}, permission = "")
    public static void execute(Player player) {

        final Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);

        if (team == null) {
            player.sendMessage(ChatColor.GRAY + "You are not in a faction!");
            return;
        }

        new ManageMenu(team).openMenu(player);
    }

}
