package net.frozenorb.foxtrot.gameplay.events.cavenite.command;

import cc.fyre.proton.command.Command;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CaveNiteScanCommand {

    @Command(names = {"cavenite see"}, permission = "op")
    public static void see(Player player) {
        for (Player onlinePlayer : Foxtrot.getInstance().getServer().getOnlinePlayers()) {

            if (!onlinePlayer.getWorld().getName().equalsIgnoreCase("sg")) {
                player.sendMessage(onlinePlayer.getName() + " is not IN!");
            }

        }
    }

    @Command(names = {"cavenite scan"}, permission = "op")
    public static void execute(CommandSender sender) {
        final Team team = Foxtrot.getInstance().getTeamHandler().getTeam("CaveNite");

        if (team == null) {
            sender.sendMessage(ChatColor.RED + "The 'CaveNite' faction doesn't exist!");
            return;
        }

        sender.sendMessage(ChatColor.GREEN + "Scanning Cave Nite chests...");

        Foxtrot.getInstance().getCaveNiteHandler().scanLoot();

        sender.sendMessage(ChatColor.GREEN + "Done! Scanned " + Foxtrot.getInstance().getCaveNiteHandler().getChestLocations().size() + " chests!");
    }


    @Command(names = {"cavenite chests"}, permission = "op")
    public static void asd(CommandSender sender) {
        final Team team = Foxtrot.getInstance().getTeamHandler().getTeam("CaveNite");

        if (team == null) {
            sender.sendMessage(ChatColor.RED + "The 'CaveNite' faction doesn't exist!");
            return;
        }

        sender.sendMessage(ChatColor.GREEN + "Resettng Cave Nite chests...");

        Foxtrot.getInstance().getCaveNiteHandler().respawnChests();
    }

}
