package net.frozenorb.foxtrot.gameplay.kitmap.game.arena;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.kitmap.game.GameType;
import net.frozenorb.foxtrot.gameplay.kitmap.game.arena.select.Selection;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class ArenaCommands {

    @Command(names = { "game arena list" }, description = "Lists the existing arenas", permission = "op", async = true)
    public static void listArenas(Player player) {
        List<String> arenas = new ArrayList<>();

        for (GameArena arena : Foxtrot.getInstance().getMapHandler().getGameHandler().getConfig().getArenas()) {
            if (arena.isSetup()) {
                arenas.add(ChatColor.GREEN.toString() + ChatColor.BOLD + arena.getName());
            } else {
                arenas.add(ChatColor.RED.toString() + ChatColor.BOLD + arena.getName());
            }
        }

        player.sendMessage(ChatColor.YELLOW + "Arenas: " + StringUtils.join(arenas, ChatColor.GRAY.toString() + ", "));
    }

    @Command(names = { "game arena create" }, description = "Creates a new arena", permission = "op", async = true)
    public static void createArena(Player player, @Parameter(name = "name") String arenaName) {
        if (Foxtrot.getInstance().getMapHandler().getGameHandler().getConfig().getArenaByName(arenaName) != null) {
            player.sendMessage(ChatColor.RED + "An arena named `" + arenaName + "` already exists!");
            return;
        }

        GameArena arena = new GameArena(arenaName);
        Foxtrot.getInstance().getMapHandler().getGameHandler().getConfig().trackArena(arena);

        player.sendMessage(ChatColor.GREEN + "Created a new arena named `" + arenaName + "`!");
    }

    @Command(names = { "game arena delete" }, description = "Deletes an existing arena", permission = "op", async = true)
    public static void deleteArena(Player player, @Parameter(name = "arena") GameArena arena) {
        Foxtrot.getInstance().getMapHandler().getGameHandler().getConfig().forgetArena(arena);

        player.sendMessage(ChatColor.GREEN + "Deleted the arena named `" + arena.getName() + "`!");
    }

    @Command(names = { "game arena setpoint" }, description = "Set the A/B point location of an arena", permission = "op", async = true)
    public static void setPointSpawn(Player player, @Parameter(name = "a/b") String point, @Parameter(name = "arena") GameArena arena) {
        if (point.equalsIgnoreCase("a") || point.equalsIgnoreCase("b")) {
            if (point.equalsIgnoreCase("a")) {
                arena.setPointA(player.getLocation());
            } else {
                arena.setPointB(player.getLocation());
            }

            Foxtrot.getInstance().getMapHandler().getGameHandler().saveConfig();

            player.sendMessage(ChatColor.GREEN + "Updated point " + point.toUpperCase() + " of " + arena.getName() + "!");
        } else {
            player.sendMessage(ChatColor.RED + "Point must be either a/b!");
        }
    }

    @Command(names = { "game arena setspec" }, description = "Set the spectator spawn point of an arena", permission = "op", async = true)
    public static void setSpectatorSpawn(Player player, @Parameter(name = "arena") GameArena arena) {
        arena.setSpectatorSpawn(player.getLocation());
        Foxtrot.getInstance().getMapHandler().getGameHandler().saveConfig();

        player.sendMessage(ChatColor.GREEN + "Updated Sumo's spectator spawn location!");
    }

    @Command(names = { "game arena wand" }, description = "Set the bounding region of an arena", permission = "op", async = true)
    public static void wand(Player player) {
        player.getInventory().addItem(Selection.SELECTION_WAND.clone());
        player.sendMessage(ChatColor.GREEN + "Gave you a selection wand.");
        player.sendMessage(ChatColor.GREEN + "Left click to set 1st corner. Right click to set 2nd corner.");
    }

    @Command(names = { "game arena setbounds" }, description = "Set the bounding region of an arena", permission = "op", async = true)
    public static void setBounds(Player player, @Parameter(name = "arena") GameArena arena) {
        Selection selection = Selection.getOrCreateSelection(player);

        if (!selection.isComplete()) {
            player.sendMessage(ChatColor.RED + "You do not have a region fully selected!");
            return;
        }

        arena.setBounds(selection.getCuboid());
        Foxtrot.getInstance().getMapHandler().getGameHandler().saveConfig();

        player.sendMessage(ChatColor.GREEN + "Updated the boundaries of " + arena.getName() + "!");
    }

    @Command(names = { "game arena types add" }, description = "Makes a game type compatible with an arena", permission = "op", async = true)
    public static void addType(Player player, @Parameter(name = "arena") GameArena arena, @Parameter(name = "gameType") GameType gameType) {
        arena.getCompatibleGameTypes().add(gameType.name().toLowerCase());
        Foxtrot.getInstance().getMapHandler().getGameHandler().saveConfig();

        player.sendMessage(ChatColor.GREEN + arena.getName() + " is now compatible with the " + gameType.getDisplayName() + " event!");
    }

    @Command(names = { "game arena types remove" }, description = "Makes a game type incompatible with an arena", permission = "op", async = true)
    public static void removeType(Player player, @Parameter(name = "arena") GameArena arena, @Parameter(name = "gameType") GameType gameType) {
        arena.getCompatibleGameTypes().remove(gameType.name().toLowerCase());
        Foxtrot.getInstance().getMapHandler().getGameHandler().saveConfig();

        player.sendMessage(ChatColor.GREEN + arena.getName() + " is no longer compatible with the " + gameType.getDisplayName() + " event!");
    }

    @Command(names = {"game arena addspawn"}, permission = "op", async = true)
    public static void removeType(Player player, @Parameter(name = "arena") GameArena arena) {
        arena.getAlternateSpawns().add(player.getLocation());
        Foxtrot.getInstance().getMapHandler().getGameHandler().saveConfig();

        player.sendMessage(ChatColor.GREEN + "Added spawn point for arena " + ChatColor.WHITE + arena.getName() + ChatColor.GREEN + ".");
    }
}
