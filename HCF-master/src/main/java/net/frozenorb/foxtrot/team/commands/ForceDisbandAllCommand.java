package net.frozenorb.foxtrot.team.commands;

import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.listener.LunarClientListener;
import net.frozenorb.foxtrot.team.Team;
import cc.fyre.proton.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class ForceDisbandAllCommand {

    private static Runnable confirmRunnable;

    @Command(names={ "forcedisbandall" }, permission="op")
    public static void forceDisbandAll(CommandSender sender, @Parameter(name = "factions")int pin) {
        if (pin != 49283) {
            sender.sendMessage(ChatColor.RED + "Invalid Number check Part 5 on the document.");
            return;
        }

        confirmRunnable = () -> {
            List<Team> teams = new ArrayList<>(Foxtrot.getInstance().getTeamHandler().getTeams());

            for (Team team : teams) {
                team.disband();
            }

            Foxtrot.getInstance().getServer().getOnlinePlayers().forEach(LunarClientListener::updateNametag);

            Foxtrot.getInstance().getServer().broadcastMessage(ChatColor.RED.toString() + ChatColor.BOLD + "All teams have been forcibly disbanded!");
        };

        sender.sendMessage(ChatColor.RED + "Are you sure you want to disband all factions? Type " + ChatColor.DARK_RED + "/forcedisbandall confirm" + ChatColor.RED + " to confirm or " + ChatColor.GREEN + "/forcedisbandall cancel" + ChatColor.RED +" to cancel.");
    }

    @Command(names = {"forcedisbandall confirm"}, permission = "op")
    public static void confirm(CommandSender sender) {
        if (confirmRunnable == null) {
            sender.sendMessage(ChatColor.RED + "Nothing to confirm.");
            return;
        }

        if (!sender.getName().equalsIgnoreCase("SimplyTrash")) {
            sender.sendMessage(ChatColor.RED + "No can do buckaroo!");
            return;
        }

        sender.sendMessage(ChatColor.GREEN + "If you're sure...");
        confirmRunnable.run();
    }

    @Command(names = {"forcedisbandall cancel"}, permission = "op")
    public static void cancel(CommandSender sender) {
        if (confirmRunnable == null) {
            sender.sendMessage(ChatColor.RED + "Nothing to cancel.");
            return;
        }

        if (!sender.getName().equalsIgnoreCase("SimplyTrash")) {
            sender.sendMessage(ChatColor.RED + "No can do buckaroo!");
            return;
        }

        sender.sendMessage(ChatColor.GREEN + "Cancelled.");
        confirmRunnable = null;
    }

}