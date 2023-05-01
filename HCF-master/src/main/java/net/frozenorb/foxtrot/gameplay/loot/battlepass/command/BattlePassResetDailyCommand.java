package net.frozenorb.foxtrot.gameplay.loot.battlepass.command;

import cc.fyre.proton.command.Command;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class BattlePassResetDailyCommand {

    @Command(names = { "battlepass reset-daily", "bp reset-daily" }, description = "Reset the BattlePass Daily Challenges", permission = "battlepass.daily.reset", async = true)
    public static void execute(CommandSender sender) {
        if (Foxtrot.getInstance().getBattlePassHandler() == null) {
            sender.sendMessage(ChatColor.RED + "BattlePass is not enabled on this server!");
            return;
        }

        Foxtrot.getInstance().getBattlePassHandler().generateNewDailyChallenges();
        sender.sendMessage(ChatColor.GREEN + "You have generated a new Daily Challenges set!");
    }

}
