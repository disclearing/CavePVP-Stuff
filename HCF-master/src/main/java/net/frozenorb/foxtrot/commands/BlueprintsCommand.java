package net.frozenorb.foxtrot.commands;

import cc.fyre.proton.command.Command;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.menu.BlueprintMenu;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class BlueprintsCommand {
    @Command(names = {"blueprint", "blueprints"}, permission = "")
    public static void execute(Player player) {
        final Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);

        if (team == null) {
            player.sendMessage(ChatColor.GRAY + "You are not on a team!");
            return;
        }

        if (!team.getOwner().equals(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You must be the leader of the team!");
            return;
        }

        new BlueprintMenu().openMenu(player);
    }

}
