package org.spigotmc;

import net.minecraft.server.MinecraftServer;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scheduler.BukkitWorker;

import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;

public class TicksPerSecondCommand extends Command {

    public TicksPerSecondCommand(String name) {
        super(name);
        this.description = "Gets the current ticks per second for the server";
        this.usageMessage = "/tps";
        this.setPermission("bukkit.command.tps");
    }

    @Override
    public boolean execute(CommandSender sender, String currentAlias, String[] args) {
        if (!testPermission(sender)) {
            return true;
        }

        if (sender.hasPermission("bukkit.command.tps.advanced")) {
            double[] tps = Bukkit.spigot().getTPS();
            String[] tpsAvg = new String[tps.length];

            for (int i = 0; i < tps.length; i++) {
                tpsAvg[i] = formatAdvancedTps(tps[i]);
            }

            int entities = MinecraftServer.getServer().entities;
            int activeEntities = MinecraftServer.getServer().activeEntities;
            double activePercent = Math.round(10000.0 * activeEntities / entities) / 100.0;

            Map<String, Integer> workers = new HashMap<>(), tasks = new HashMap<>();

            for (BukkitWorker worker : Bukkit.getScheduler().getActiveWorkers()) {
                workers.merge(worker.getOwner().getName(), 1, Integer::sum);
            }

            for (BukkitTask task : Bukkit.getScheduler().getPendingTasks()) {
                tasks.merge(task.getOwner().getName(), 1, Integer::sum);
            }

            sender.sendMessage(ChatColor.GOLD + "TPS from last 1m, 5m, 15m: " + StringUtils.join(tpsAvg, ", "));
            sender.sendMessage(ChatColor.GOLD + "Full tick: " + formatTickTime(MinecraftServer.getServer().lastTickTime) + " ms");
            sender.sendMessage(ChatColor.GOLD + "Active entities: " + ChatColor.GREEN + activeEntities + "/" + entities + " (" + activePercent + "%)");
            sender.sendMessage(ChatColor.GOLD + "Online players: " + ChatColor.GREEN + Bukkit.getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers());
            sender.sendMessage(ChatColor.GOLD + "Thread count: " + ChatColor.GREEN + ManagementFactory.getThreadMXBean().getThreadCount());
            sender.sendMessage("");
            sender.sendMessage(ChatColor.GOLD + "Active Bukkit workers: " + ChatColor.GREEN + Bukkit.getScheduler().getActiveWorkers().size());

            workers.entrySet()
                    .stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .forEach(e -> sender.sendMessage(ChatColor.YELLOW + e.getKey() + ": " + ChatColor.GREEN + e.getValue()));

            sender.sendMessage("");
            sender.sendMessage(ChatColor.GOLD + "Pending Bukkit tasks: " + ChatColor.GREEN + Bukkit.getScheduler().getPendingTasks().size());

            tasks.entrySet()
                    .stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .forEach(e -> sender.sendMessage(ChatColor.YELLOW + e.getKey() + ": " + ChatColor.GREEN + e.getValue()));
        } else {
            double tps = Bukkit.spigot().getTPS()[1];
            StringBuilder tpsBuilder = new StringBuilder();

            tpsBuilder.append(ChatColor.GOLD).append("Server performance: ");
            tpsBuilder.append(formatBasicTps(tps)).append(ChatColor.GOLD).append("/20.0");
            tpsBuilder.append(" [").append(tps > 18.0 ? ChatColor.GREEN : tps > 16.0 ? ChatColor.YELLOW : ChatColor.RED);

            int i = 0;

            for (; i < Math.round(tps); i++) {
                tpsBuilder.append("|");
            }

            tpsBuilder.append(ChatColor.DARK_GRAY);

            for (; i < 20; i++) {
                tpsBuilder.append("|");
            }

            tpsBuilder.append(ChatColor.GOLD).append("]");
            sender.sendMessage(tpsBuilder.toString());
        }

        return true;
    }

    private static String formatTickTime(double time) {
        return (time < 40.0D ? ChatColor.GREEN : time < 60.0D ? ChatColor.YELLOW : ChatColor.RED).toString() + Math.round(time * 10.0D) / 10.0D;
    }

    private static String formatAdvancedTps(double tps) {
        return (tps > 18.0 ? ChatColor.GREEN : tps > 16.0 ? ChatColor.YELLOW : ChatColor.RED).toString() + Math.min(Math.round(tps * 100.0D) / 100.0, 20.0);
    }

    private String formatBasicTps(double tps) {
        return (tps > 18.0 ? ChatColor.GREEN : tps > 16.0 ? ChatColor.YELLOW : ChatColor.RED).toString() + Math.min(Math.round(tps * 10.0D) / 10.0D, 20.0D);
    }
}
