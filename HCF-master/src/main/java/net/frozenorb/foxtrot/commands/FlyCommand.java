package net.frozenorb.foxtrot.commands;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class FlyCommand {

    @Command(names = {"fly"}, permission = "")
    public static void execute(Player player) {

        if (player.hasPermission("command.fly")) {
            player.setAllowFlight(!player.getAllowFlight());
            player.sendMessage(ChatColor.GOLD + "Fly: " + (player.getAllowFlight() ? ChatColor.GREEN + "Enabled":ChatColor.RED + "Disabled"));
            return;
        }

        if (!player.hasPermission("command.customrank.fly")) {
            player.sendMessage(ChatColor.RED + "You must purchase the Custom Rank to use this at https://store.cavepvp.org!");
            return;
        }

        if (!CustomTimerCreateCommand.isSOTWTimer()) {
            player.sendMessage(ChatColor.RED + "You can only use this during SOTW Timer!");
            return;
        }

        if (CustomTimerCreateCommand.hasSOTWEnabled(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You can only use this during SOTW Timer!");
            return;
        }

        player.setAllowFlight(!player.getAllowFlight());
        player.sendMessage(ChatColor.GOLD + "Fly: " + (player.getAllowFlight() ? ChatColor.GREEN + "Enabled":ChatColor.RED + "Disabled"));
    }

    @Command(names = {"flyspeed"}, permission = "")
    public static void execute(Player player, @Parameter(name = "speed")int speed) {

        if (!player.getAllowFlight()) {
            player.sendMessage(ChatColor.RED + "You must already have flight enabled to do this!");
            return;
        }

        if (!player.hasPermission("command.customrank.fly") && !player.hasPermission("command.fly")) {
            player.sendMessage(ChatColor.RED + "You must purchase the Custom Rank to use this at https://store.cavepvp.org!");
            return;
        }

        if (!CustomTimerCreateCommand.isSOTWTimer()) {
            player.sendMessage(ChatColor.RED + "You can only use this during SOTW Timer!");
            return;
        }

        if (CustomTimerCreateCommand.hasSOTWEnabled(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You can only use this during SOTW Timer!");
            return;
        }

        if (speed > 4 || speed <= 0) {
            player.sendMessage(ChatColor.RED + "The speed must be a number between 1-4.");
            return;
        }

        player.setFlySpeed((float) (speed*0.1));
        player.sendMessage(ChatColor.GOLD + "Fly Speed: " + (player.getAllowFlight() ? ChatColor.GREEN + "Enabled":ChatColor.RED + "Disabled"));
    }

    @Command(names = {"setfly"}, permission = "op")
    public static void execute(Player player, @Parameter(name = "target")Player target) {
        player.setAllowFlight(!player.getAllowFlight());
        player.sendMessage(ChatColor.GOLD + "Fly for " + target.getName() + ": " + (player.getAllowFlight() ? ChatColor.GREEN + "Enabled":ChatColor.RED + "Disabled"));
    }

}
