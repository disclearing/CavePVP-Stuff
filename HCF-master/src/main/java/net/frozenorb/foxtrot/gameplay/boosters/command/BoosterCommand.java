package net.frozenorb.foxtrot.gameplay.boosters.command;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.boosters.Booster;
import net.frozenorb.foxtrot.gameplay.boosters.NetworkBoosterHandler;
import net.frozenorb.foxtrot.gameplay.boosters.menu.BoosterMenu;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class BoosterCommand {

    @Command(names = {"boosters", "booster", "networkbooster"}, permission = "")
    public static void execute(Player player) {
        new BoosterMenu().openMenu(player);
    }

    @Command(
            names = {"booster give", "boosters give"},
            permission = "foxtrot.command.itemboxes"
    )
    public static void execute(CommandSender sender,
                               @Parameter(name = "booster") Booster booster,
                               @Parameter(name = "amount", defaultValue = "1") int amount,
                               @Parameter(name = "player", defaultValue = "self") Player target) {

        final NetworkBoosterHandler networkBoosterHandler = Foxtrot.getInstance().getNetworkBoosterHandler();

        final Map<Booster, Integer> boosterMap = networkBoosterHandler.getBoosterBalances().getOrDefault(target.getUniqueId(), new HashMap<>());
        boosterMap.put(booster, boosterMap.getOrDefault(booster, 0)+amount);

        networkBoosterHandler.getBoosterBalances().put(target.getUniqueId(), boosterMap);

        target.sendMessage("");
        target.sendMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Network Boosters");
        target.sendMessage(ChatColor.translate("&cYou have been given &f1 " + booster.getDisplayName() + "&c booster!"));
        target.sendMessage(ChatColor.GRAY + "Add it to the queue by typing /networkbooster!");
        target.sendMessage("");

        sender.sendMessage(ChatColor.GREEN + "Gave " + booster.getDisplayName() + ChatColor.GREEN + " to " + ChatColor.WHITE + target.getName());
    }
}
