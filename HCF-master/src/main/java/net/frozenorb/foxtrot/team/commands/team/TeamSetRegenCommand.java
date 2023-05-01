package net.frozenorb.foxtrot.team.commands.team;

import cc.fyre.neutron.util.DurationWrapper;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TeamSetRegenCommand {
    @Command(names = {"f setdtrregen", "t setregen"}, permission = "op", hidden = true)
    public static void execute(Player player, @Parameter(name = "target")Team team, @Parameter(name = "duration")DurationWrapper durationWrapper) {
        team.setDTRCooldown(System.currentTimeMillis() + durationWrapper.getDuration());
        player.sendMessage(ChatColor.GREEN + "You have set " + ChatColor.WHITE + team.getName() + "'s" + ChatColor.GREEN + " DTR Regen to " + ChatColor.WHITE + durationWrapper.getSource() + ChatColor.GREEN + ".");
    }
}