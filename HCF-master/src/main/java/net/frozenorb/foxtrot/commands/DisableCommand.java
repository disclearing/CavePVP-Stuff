package net.frozenorb.foxtrot.commands;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.github.paperspigot.PaperSpigotConfig;

public class DisableCommand {

    public static boolean deaths = false;
    public static boolean teamView = false;
    public static boolean antiClean = false;
    public static boolean dailyKit = false;
    public static boolean crapplelimit = true;
    public static boolean damage = false;
    public static boolean backPack = false;
    public static boolean bounty = true;
    public static boolean outpost = false;
    public static boolean trapActivator = true;


    @Command(names = {"setstrength"}, permission = "op")
    public static void execute(Player player, @Parameter(name = "value")double value) {
        double old = PaperSpigotConfig.strengthEffectModifier;
        PaperSpigotConfig.strengthEffectModifier = value;
        player.sendMessage(ChatColor.GOLD + "Changed to " + value + " was " + old);
    }

    @Command(names = {"settime"}, permission = "op")
    public static void execute(Player player, @Parameter(name = "time")long time, @Parameter(name = "relative")boolean he) {
        player.setPlayerTime(time, he);
        player.sendMessage("Done");
    }

    @Command(names = {"disable outpost"}, permission = "op")
    public static void disableOutpost(Player player) {
        if (outpost) {
            outpost = false;
            player.sendMessage(ChatColor.GREEN + "Enabled outpost");
        } else {
            outpost = true;
            player.sendMessage(ChatColor.RED + "Disabled outpost");
        }
    }

    @Command(names = {"disable dailykit"}, permission = "op")
    public static void disableDailyKit(Player player) {
        if (dailyKit) {
            dailyKit = false;
            player.sendMessage(ChatColor.GREEN + "Enabled dailyKit");
        } else {
            dailyKit = true;
            player.sendMessage(ChatColor.RED + "Disabled dailyKit");
        }
    }

    @Command(names = {"disable deaths"}, permission = "op")
    public static void disableDeaths(Player player) {
        if (deaths) {
            deaths = false;
            player.sendMessage(ChatColor.GREEN + "Enabled deaths GUI");
        } else {
            deaths = true;
            player.sendMessage(ChatColor.RED + "Disabled deaths GUI");
        }
    }

    @Command(names = {"disable bounty"}, permission = "op")
    public static void disablebounties(Player player) {
        if (!bounty) {
            bounty = true;
            player.sendMessage(ChatColor.GREEN + "Enabled bounty GUI");
        } else {
            bounty = false;
            player.sendMessage(ChatColor.RED + "Disabled bounty GUI");
        }
    }

    @Command(names = {"disable backpack"}, permission = "op")
    public static void backpack(Player player) {
        if (!backPack) {
            backPack = true;
            player.sendMessage(ChatColor.GREEN + "Enabled backPack GUI");
        } else {
            backPack = false;
            player.sendMessage(ChatColor.RED + "Disabled backPack GUI");
        }
    }

    @Command(names = {"disable trapActivator"}, permission = "op")
    public static void disabletrapActivator(Player player) {
        if (!trapActivator) {
            trapActivator = true;
            player.sendMessage(ChatColor.GREEN + "Enabled trapActivator GUI");
        } else {
            trapActivator = false;
            player.sendMessage(ChatColor.RED + "Disabled trapActivator GUI");
        }
    }

    @Command(names = {"disable crapplelimit"}, permission = "op")
    public static void crapplelimit(Player player) {
        if (crapplelimit) {
            crapplelimit = false;
            player.sendMessage(ChatColor.RED + "Disabled crapple limit");
        } else {
            crapplelimit = true;
            player.sendMessage(ChatColor.GREEN + "Enabled crapple limit");
        }
    }

    @Command(names = {"disable damage"}, permission = "op")
    public static void disableDamage(Player player) {
        if (deaths) {
            deaths = false;
            player.sendMessage(ChatColor.RED + "Disabled bonus damage");
        } else {
            deaths = true;
            player.sendMessage(ChatColor.GREEN + "Enabled bonus damage");
        }
    }

    @Command(names = {"disable anticlean"}, permission = "command.disable.anticlean")
    public static void disableAntiClean(Player player) {
        if (antiClean) {
            antiClean = false;
            player.sendMessage(ChatColor.RED + "Disabled anti-clean");
        } else {
            antiClean = true;
            player.sendMessage(ChatColor.GREEN + "Enabled anti-clean");
        }
    }

    @Command(names = {"disable teamview"}, permission = "op")
    public static void disableTeamView(Player player) {
        if (deaths) {
            teamView = false;
            player.sendMessage(ChatColor.GREEN + "Enabled team view");
        } else {
            teamView = true;
            player.sendMessage(ChatColor.RED + "Disabled team view");
        }
    }
}
