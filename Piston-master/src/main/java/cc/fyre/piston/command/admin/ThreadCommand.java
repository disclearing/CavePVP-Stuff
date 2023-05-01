package cc.fyre.piston.command.admin;

import cc.fyre.proton.command.Command;
import com.google.common.collect.Maps;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scheduler.BukkitWorker;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ThreadCommand {

    @Command(names = {"threads"}, permission = "op", hidden = true)
    public static void threads(Player player) {
        final Runtime runtime = Runtime.getRuntime();
        final Set<Thread> threads = Thread.getAllStackTraces().keySet();
        player.sendMessage(ChatColor.GOLD + "Threads: " + ChatColor.YELLOW + "(" + threads.size() + " Active) (Ram " + ChatColor.LIGHT_PURPLE + format(runtime.freeMemory()) + " free out of " + ChatColor.LIGHT_PURPLE + format(runtime.maxMemory()) + " " + ChatColor.LIGHT_PURPLE + format(runtime.maxMemory() - runtime.freeMemory()) + " used" + ChatColor.GOLD + ")");
    }

    @Command(names = {"thread list"}, permission = "op", hidden = true)
    public static void execute(Player player) {
        final Set<Thread> threads = Thread.getAllStackTraces().keySet();
        for (final Thread thread : threads) {
            player.sendMessage(ChatColor.translate(" &6* &e" + thread.getName() + " &d(State: " + thread.getState() + ", Priority: " + thread.getPriority() + ")"));
        }
    }

    @Command(names = {"thread gc"}, permission = "op", hidden = true)
    public static void gc(Player player) {
        final Runtime runtime = Runtime.getRuntime();
        player.sendMessage(ChatColor.YELLOW + "Trying to run Java garbage collector to free up memory.");
        final long before = System.currentTimeMillis();
        runtime.gc();
        final long after = System.currentTimeMillis();
        player.sendMessage(ChatColor.GOLD + "* " + ChatColor.YELLOW + "Finished! Took " + ChatColor.LIGHT_PURPLE + (after - before) + "ms");
    }

    public static String format(final long bytes) {
        final double gb = bytes / 1.0E9;
        final String s = String.format("%.2f", gb) + " GB";
        return s;
    }
}
