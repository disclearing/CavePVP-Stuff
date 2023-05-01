package net.frozenorb.foxtrot.gameplay.kitmap.gem.gemboost;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static org.bukkit.ChatColor.*;

public class GemBoostCommand {

    @Command(names = {"gemboost"}, permission = "op")
    public static void gemBoost(CommandSender sender,
                                @Parameter(name = "target") Player target,
                                @Parameter(name = "minutes") int minutes) {
        if (!Foxtrot.getInstance().getMapHandler().isKitMap()) {
            sender.sendMessage(ChatColor.RED + "This is a KitMap only command.");
            return;
        }

        Foxtrot.getInstance().getGemBoosterMap().giveGemBooster(target, minutes);

        sender.sendMessage(DARK_GREEN + "[Gem Booster] " + WHITE + "You have given " + LIGHT_PURPLE + target.getName()
                + WHITE + " a Gem Booster of " + LIGHT_PURPLE + minutes + WHITE + " minutes.");

        if (sender != target) {
            target.sendMessage(DARK_GREEN + "[Gem Booster] " + LIGHT_PURPLE + sender.getName()
                    + WHITE + "has given you a Gem Booster of " + LIGHT_PURPLE + minutes + WHITE + " minutes.");
        }
    }
}
