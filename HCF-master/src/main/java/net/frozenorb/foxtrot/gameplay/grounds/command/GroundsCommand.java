package net.frozenorb.foxtrot.gameplay.grounds.command;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.grounds.GroundsArea;
import net.frozenorb.foxtrot.gameplay.grounds.SpawnerGroundsHandler;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class GroundsCommand {

    @Command(names = {"spawnergrounds", "grounds", "spawners"}, permission = "")
    public static void execute(Player player) {
        final SpawnerGroundsHandler spawnerGroundsHandler = Foxtrot.getInstance().getSpawnerGroundsHandler();

        player.sendMessage("");
        player.sendMessage(ChatColor.translate("&4&lSpawner Locations"));
        for (GroundsArea event : spawnerGroundsHandler.getAreas()) {
            final Team team = event.findTeam();

            if (team == null) {
                continue;
            }

            player.sendMessage(ChatColor.translate("&4&l┃ &f" + ChatColor.stripColor(event.getGroundsDisplayName()) + "s: &c" + team.getHQ().getBlockX() + ", " + team.getHQ().getBlockY() + ", " + team.getHQ().getBlockZ()));
        }
        player.sendMessage("");
    }

    @Command(names = {"grounds reset", "spawnergrounds reset"}, permission = "op")
    public static void start(Player player, @Parameter(name = "groundsType")String groundsType) {
        final SpawnerGroundsHandler spawnerGroundsHandler = Foxtrot.getInstance().getSpawnerGroundsHandler();
        final GroundsArea groundsArea = spawnerGroundsHandler.findArea(groundsType);

        if (groundsArea == null) {
            player.sendMessage(ChatColor.RED + "That spawner grounds area does not exist!");
            return;
        }

        groundsArea.reset();
        player.sendMessage(ChatColor.GREEN + "Reset that grounds area!");
    }

    @Command(names = {"grounds resetall", "spawnergrounds resetall"}, permission = "op")
    public static void resetAll(Player player, @Parameter(name = "groundsType", defaultValue = "R_A_N_D_O_M")String groundsType) {
        final SpawnerGroundsHandler spawnerGroundsHandler = Foxtrot.getInstance().getSpawnerGroundsHandler();

        spawnerGroundsHandler.resetAll();

        for (GroundsArea groundsArea : spawnerGroundsHandler.getAreas()) {
            player.sendMessage(ChatColor.GREEN + "Reset " + groundsArea.getGroundsID() + " grounds area!");
        }
    }

    @Command(names = {"grounds addspawn", "spawnergrounds addspawn"}, permission = "op")
    public static void addSpawn(Player player, @Parameter(name = "groundsType" )String groundsType) {
        final SpawnerGroundsHandler spawnerGroundsHandler = Foxtrot.getInstance().getSpawnerGroundsHandler();
        final GroundsArea groundsArea = spawnerGroundsHandler.findArea(groundsType);

        if (groundsArea == null) {
            player.sendMessage(ChatColor.RED + "That spawner grounds area does not exist!");
            return;
        }

        player.sendMessage(ChatColor.GREEN + "Added a spawn location!");
        groundsArea.getGuardLocations().add(player.getLocation().clone());
    }
}
