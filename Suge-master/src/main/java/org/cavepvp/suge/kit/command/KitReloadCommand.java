package org.cavepvp.suge.kit.command;

import cc.fyre.proton.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.cavepvp.suge.Suge;

public class KitReloadCommand {
    @Command(names = {"kit reload"}, permission = "op")
    public static void execute(CommandSender commandSender) {
        Suge.getInstance().getKitHandler().getKits().clear();
        Suge.getInstance().getKitHandler().loadKits();

        commandSender.sendMessage(ChatColor.GREEN + "Reloaded configs!");
    }

    @Command(names = {"kit reset_all_server_cooldowns"}, permission = "op")
    public static void reset(CommandSender commandSender) {
        if (!commandSender.getName().contains("SheepKiller69") && !commandSender.getName().contains("SimplyTrash")) {
            commandSender.sendMessage(ChatColor.RED + "You must be simply or sheep to do this!");
            return;
        }

        Suge.getInstance().getKitHandler().getCooldowns().clear();

        commandSender.sendMessage(ChatColor.GREEN + "Reset cooldowns!");
    }
}
