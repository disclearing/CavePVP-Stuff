package net.frozenorb.foxtrot.gameplay.events.fury.command;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.scoreboard.construct.ScoreFunction;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.events.Event;
import net.frozenorb.foxtrot.gameplay.events.conquest.ConquestHandler;
import net.frozenorb.foxtrot.gameplay.events.fury.FuryHandler;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockVector;

import java.util.concurrent.TimeUnit;

public class FuryCommand {
    @Command(names = {"fury start"}, permission = "op")
    public static void execute(Player player) {
        if (Foxtrot.getInstance().getFuryHandler().isActive()) {
            player.sendMessage(ChatColor.RED + "Fury is already active!");
            return;
        }

        Foxtrot.getInstance().getFuryHandler().start();
    }

//    @Command(names = {"fury show", "f show fury", "fury"}, permission = "")
//    public static void show(Player player) {
//        if (!Foxtrot.getInstance().getFuryHandler().isActive()) {
//            player.sendMessage(ChatColor.RED + "Fury isn't currently active!");
//            return;
//        }
//
//        final FuryHandler furyHandler = Foxtrot.getInstance().getFuryHandler();
//
//        player.sendMessage(Team.GRAY_LINE);
//        player.sendMessage(furyHandler.getFuryCapZone().getChatColor() + ChatColor.BOLD.toString() + "Fury " + furyHandler.getFuryCapZone().getDisplayName());
//
//        final BlockVector location = furyHandler.getActiveCapZone().getCapLocation();
//
//        player.sendMessage(ChatColor.YELLOW + "Location: " + ChatColor.RED + location.getBlockX() + ", " + location.getBlockZ() + " (" + furyHandler.getFuryCapZone().getDisplayName() + ")");
//        final long time = furyHandler.getActiveCapZone().getRemainingCapTime() <= 0 ? ConquestHandler.TIME_TO_CAP : furyHandler.getActiveCapZone().getRemainingCapTime();
//        player.sendMessage(ChatColor.YELLOW + "Remaining: " + ChatColor.RED + time + "s");
//        player.sendMessage(ChatColor.YELLOW + "World Switch in: " + ChatColor.RED + (ScoreFunction.TIME_SIMPLE.apply((float) (furyHandler.getLastSwitchTime() + TimeUnit.MINUTES.toMillis(15) - System.currentTimeMillis()) / 1000)));
//        player.sendMessage(Team.GRAY_LINE);
//    }

    @Command(names = {"fury stop"}, permission = "op")
    public static void stop(Player player) {
        if (!Foxtrot.getInstance().getFuryHandler().isActive()) {
            player.sendMessage(ChatColor.RED + "Fury isn't currently active!");
            return;
        }

        Foxtrot.getInstance().getFuryHandler().endGame(null);
    }

    @Command(names = {"fury setpoints"}, permission = "op")
    public static void setPoints(Player player, @Parameter(name = "team")Team team, @Parameter(name = "amount")int amount) {
        if (!Foxtrot.getInstance().getFuryHandler().isActive()) {
            player.sendMessage(ChatColor.RED + "Fury isn't currently active!");
            return;
        }

        player.sendMessage(ChatColor.translate("&6You have set &f" + team.getName() + "'s points &6to &f" + amount + "&6."));
        Foxtrot.getInstance().getFuryHandler().getTeamPoints().replace(team.getUniqueId(), amount);
    }

    @Command(names = {"fury swap"}, permission = "op")
    public static void swap(Player player) {
        if (!Foxtrot.getInstance().getFuryHandler().isActive()) {
            player.sendMessage(ChatColor.RED + "Fury isn't currently active!");
            return;
        }

        Foxtrot.getInstance().getFuryHandler().switchCapZone();
    }
}
