package net.frozenorb.foxtrot.commands;

import net.minecraft.util.com.google.common.collect.Sets;
import lombok.Getter;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.listener.LunarClientListener;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class CustomTimerCreateCommand {

    @Getter private static Map<String, Long> customTimers = new HashMap<>();
    @Getter private static Set<UUID> sotwEnabled = Sets.newHashSet();

    @Command(
            names = {"customtimer create"},
            permission = "foxtrot.command.customtimer",
            hidden = true
    )
    public static void customTimerCreate(CommandSender sender,@Parameter(name="time")int time,@Parameter(name="title", wildcard=true)String title) {

        if (time == 0) {
            customTimers.remove(title);
        } else {
            customTimers.put(title, System.currentTimeMillis() + (time * 1000));
        }

    }

    @Command(names = {"sotw enable"}, permission = "")
    public static void sotwEnable(Player sender) {
        if (!isSOTWTimer()) {
            sender.sendMessage(ChatColor.RED + "You can't /sotw enable when there is no SOTW timer...");
            return;
        }

        if (!Foxtrot.getInstance().getMapHandler().isKitMap()) {
            if (customTimers.get("&a&lSOTW") - System.currentTimeMillis() > TimeUnit.MINUTES.toMillis(30L)) {
                sender.sendMessage(ChatColor.RED + "You may not SOTW enable until there are 30 minutes left in the map.");
                return;
            }
        }

        if (sotwEnabled.add(sender.getUniqueId())) {
            LunarClientListener.updateNametag(sender);
            sender.sendMessage(ChatColor.GREEN + "Successfully disabled your SOTW timer.");
        } else {
            sender.sendMessage(ChatColor.RED + "Your SOTW timer was already disabled...");
        }

    }

    @Command(names = {"sotw cancel", "sotw stop"}, permission = "foxtrot.command.sotw.cancel")
    public static void sotwCancel(CommandSender sender) {

        final Long removed = customTimers.remove("&a&lSOTW");

        if (removed != null && System.currentTimeMillis() < removed) {
            sender.sendMessage(ChatColor.GREEN + "Deactivated the SOTW timer.");

            for (Player loopPlayer : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
                LunarClientListener.updateNametag(loopPlayer);
            }

            return;
        }

        sender.sendMessage(ChatColor.RED + "SOTW timer is not active.");
    }

    @Command(names = "sotw start", permission = "foxtrot.command.sotw.start")
    public static void sotwStart(CommandSender sender, @Parameter(name = "time")long time) {
        if (time < 0) {
            sender.sendMessage(ChatColor.RED + "Invalid time!");
            return;
        }

        customTimers.put("&a&lSOTW", System.currentTimeMillis() + (time));
        sender.sendMessage(ChatColor.GREEN + "Started the SOTW timer for " + time);

        for (Player loopPlayer : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
            LunarClientListener.updateNametag(loopPlayer);
        }
    }

    @Command(names = "sotw extend", permission = "foxtrot.command.sotw.extend")
    public static void sotwExtend(CommandSender sender, @Parameter(name = "time")long time) {

        if (time < 0) {
            sender.sendMessage(ChatColor.RED + "Invalid time!");
            return;
        }

        if (!customTimers.containsKey("&a&lSOTW")) {
            sender.sendMessage(ChatColor.RED + "There is currently no active SOTW timer.");
            return;
        }

        customTimers.put("&a&lSOTW", customTimers.get("&a&lSOTW") + (time));
        sender.sendMessage(ChatColor.GREEN + "Extended the SOTW timer by " + time);
    }

    @Command(names = "sotw set", permission = "foxtrot.command.sotw.set")
    public static void sotwSet(CommandSender sender, @Parameter(name = "time")long time) {

        if (time < 0) {
            sender.sendMessage(ChatColor.RED + "Invalid time!");
            return;
        }

        if (!customTimers.containsKey("&a&lSOTW")) {
            sender.sendMessage(ChatColor.RED + "There is currently no active SOTW timer.");
            return;
        }

        customTimers.put("&a&lSOTW", System.currentTimeMillis() + time);
        sender.sendMessage(ChatColor.GREEN + "Extended the SOTW timer by " + time);
    }

    public static long remainingSOTWTime() {
        if (!isSOTWTimer())
            return -1;

        long endsAt = customTimers.get("&a&lSOTW");

        return endsAt - System.currentTimeMillis();
    }

    public static boolean isSOTWTimer() {
        return customTimers.containsKey("&a&lSOTW");
    }

    public static boolean hasSOTWEnabled(UUID uuid) {
        return sotwEnabled.contains(uuid);
    }

    public static boolean isDoublePoints() {
        return customTimers.containsKey("&d&l2x Points");
    }


}