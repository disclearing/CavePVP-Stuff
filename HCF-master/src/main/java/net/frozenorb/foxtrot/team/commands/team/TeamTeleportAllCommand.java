package net.frozenorb.foxtrot.team.commands.team;

import net.frozenorb.foxtrot.team.Team;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamTeleportAllCommand {

    @Command(names={ "team tpall", "t tpall", "f tpall", "faction tpall", "fac tpall" }, permission="foxtrot.command.faction.teleportall")
    public static void teamTP(Player sender, @Parameter(name="team") Team team) {
        for (Player player : team.getOnlineMembers()) {
            player.teleport(sender.getLocation());

            sender.sendMessage(ChatColor.GREEN + "Teleported " + player.getDisplayName() + ".");
        }

        sender.sendMessage(ChatColor.GOLD + "Teleported " + ChatColor.WHITE +  team.getOnlineMembers().size() + ChatColor.GOLD + " to you.");
    }

}