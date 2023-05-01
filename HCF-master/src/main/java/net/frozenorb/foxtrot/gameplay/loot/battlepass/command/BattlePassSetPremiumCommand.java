package net.frozenorb.foxtrot.gameplay.loot.battlepass.command;

import cc.fyre.proton.Proton;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.BattlePassProgress;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BattlePassSetPremiumCommand {

    @Command(names = { "battlepass setpremium", "bp setpremium" }, description = "Grant premium BattlePass status", permission = "battlepass.setpremium", async = true)
    public static void execute(CommandSender sender, @Parameter(name = "target") UUID targetUuid) {
        if (Foxtrot.getInstance().getBattlePassHandler() == null) {
            sender.sendMessage(ChatColor.RED + "BattlePass is not enabled on this server!");
            return;
        }

        BattlePassProgress progress = Foxtrot.getInstance().getBattlePassHandler().getOrLoadProgress(targetUuid);
        progress.setPremium(true);
        progress.requiresSave();

        Foxtrot.getInstance().getBattlePassHandler().saveProgress(progress);

        Player player = Bukkit.getPlayer(targetUuid);
        if (player != null) {
            player.sendMessage(ChatColor.GOLD + "You've been give the Premium BattlePass! Type /bp to get started!");
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
        }

        sender.sendMessage(ChatColor.GREEN + "Granted premium BattlePass to " + ChatColor.WHITE + Proton.getInstance().getUuidCache().name(targetUuid) + ChatColor.GREEN + "!");
    }

}
