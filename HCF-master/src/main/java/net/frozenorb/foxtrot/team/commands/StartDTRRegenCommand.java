package net.frozenorb.foxtrot.team.commands;

import net.frozenorb.foxtrot.team.Team;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class StartDTRRegenCommand {

    @Command(names={ "startdtrregen" }, permission="foxtrot.startdtrregen")
    public static void startDTRRegen(Player sender, @Parameter(name="team") Team team) {
        team.setDTRCooldown(System.currentTimeMillis());
        sender.sendMessage(ChatColor.LIGHT_PURPLE + team.getName() + ChatColor.YELLOW + " is now regenerating DTR.");
    }

}