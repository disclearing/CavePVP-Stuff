package net.frozenorb.foxtrot.gameplay.loot.battlepass.command;

import cc.fyre.proton.Proton;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class BattlePassResetCommand {

    @Command(names = { "battlepass reset", "bp reset" }, description = "Resets a player's BattlePass progress", permission = "battlepass.reset", async = true)
    public static void execute(CommandSender player, @Parameter(name = "player") UUID targetUuid) {
        if (Foxtrot.getInstance().getBattlePassHandler() == null) {
            player.sendMessage(ChatColor.RED + "BattlePass is not enabled on this server!");
            return;
        }

        Foxtrot.getInstance().getBattlePassHandler().clearProgress(targetUuid);
        player.sendMessage(ChatColor.GREEN + "Cleared BattlePass progress of " + ChatColor.WHITE + Proton.getInstance().getUuidCache().name(targetUuid) + ChatColor.GREEN + "!");
    }

}
