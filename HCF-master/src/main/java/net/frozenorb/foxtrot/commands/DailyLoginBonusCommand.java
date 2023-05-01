package net.frozenorb.foxtrot.commands;

import cc.fyre.proton.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class DailyLoginBonusCommand {

    @Command(names = {"dailylogin", "loginbonus", "bonus", "daily"}, permission = "")
    public static void execute(Player player) {
        player.sendMessage(ChatColor.RED + "Daily Login Bonuses will be enabled once the Advent Calendar season is over! (December 26th)");
    }
}
