package net.frozenorb.foxtrot.gameplay.loot.battlepass.command;

import cc.fyre.proton.Proton;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.BattlePassProgress;
import net.frozenorb.foxtrot.util.Formats;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class BattlePassSetXPCommand {

    @Command(names = { "battlepass setxp", "bp setxp" }, description = "Set a player's BattlePass progress XP", permission = "battlepass.setxp", async = true)
    public static void execute(CommandSender sender, @Parameter(name = "target") UUID targetUuid, @Parameter(name = "xp") int xp) {
        if (Foxtrot.getInstance().getBattlePassHandler() == null) {
            sender.sendMessage(ChatColor.RED + "BattlePass is not enabled on this server!");
            return;
        }

        BattlePassProgress progress = Foxtrot.getInstance().getBattlePassHandler().getOrLoadProgress(targetUuid);
        progress.setExperience(xp);
        progress.requiresSave();

        Foxtrot.getInstance().getBattlePassHandler().saveProgress(progress);

        sender.sendMessage(ChatColor.GREEN + "Set " + ChatColor.WHITE + Proton.getInstance().getUuidCache().name(targetUuid) + ChatColor.GREEN + "'s BattlePass XP to " + ChatColor.WHITE + Formats.formatNumber(xp) + ChatColor.GREEN + "!");
    }

}
