package net.frozenorb.foxtrot.commands;

import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import net.frozenorb.foxtrot.persist.RedisSaveTask;
import cc.fyre.proton.command.Command;

public class SaveRedisCommand {

    @Command(names = {"SaveRedis", "Save"}, permission = "op", async = true)
    public static void saveRedis(CommandSender sender) {
        RedisSaveTask.save(sender, false);
    }

    @Command(names = {"savedata"}, permission = "op", async = true)
    public static void saveData(CommandSender sender) {
        long start = System.currentTimeMillis();

        Foxtrot.getInstance().saveData();

        long end = System.currentTimeMillis();

        sender.sendMessage(ChatColor.GREEN + "Saved all data! Took " + (end-start) + " ms.");
    }

    @Command(names = {"SaveRedis ForceAll", "Save ForceAll"}, permission = "op", async = true)
    public static void saveRedisForceAll(CommandSender sender) {
        RedisSaveTask.save(sender, true);
    }

}