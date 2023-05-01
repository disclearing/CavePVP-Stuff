package net.frozenorb.foxtrot.gameplay.events.cavenite.command;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import mkremins.fanciful.FancyMessage;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.events.cavenite.CaveNiteHandler;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class CaveNiteAddSpawnCommand {

    @Command(names = {"cavenite addspawn"}, permission = "op")
    public static void execute(Player player) {
        final CaveNiteHandler caveNiteHandler = Foxtrot.getInstance().getCaveNiteHandler();

        final Location location = player.getLocation().clone();

        if (caveNiteHandler.getLocations().contains(location)) {
            player.sendMessage(ChatColor.RED + "There is already a Spawn at that location!");
            return;
        }

        player.sendMessage(ChatColor.translate("&6Added a spawn at &f" + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ() + "&6! &e[" + caveNiteHandler.getLocations().size() + "]"));

        caveNiteHandler.getLocations().add(location);
    }

    @Command(names = {"cavenite listspawn"}, permission = "op")
    public static void listSpawn(Player player) {
        final CaveNiteHandler caveNiteHandler = Foxtrot.getInstance().getCaveNiteHandler();

        for (Location location : caveNiteHandler.getLocations()) {
            new FancyMessage("&6Location #" + caveNiteHandler.getLocations().indexOf(location) + " at &f" + location.getBlockX() + ", " + location.getBlockY() + ", " + location.getBlockZ()).tooltip(ChatColor.GREEN + "Click to teleport").command("/tppos " + location.getBlockX() + " " + location.getBlockY() + " " + location.getBlockZ()).send(player);
        }
    }

    @Command(names = {"cavenite removeid"}, permission = "op")
    public static void listSpawn(Player player, @Parameter(name = "id")int id) {
        final CaveNiteHandler caveNiteHandler = Foxtrot.getInstance().getCaveNiteHandler();

        final Location indexOf = caveNiteHandler.getLocations().get(id);

        if (indexOf == null) {
            player.sendMessage(ChatColor.RED + "There is no location with that ID! Check /cavenite listspawn for all ids");
            return;
        }

        caveNiteHandler.getLocations().remove(indexOf);

        player.sendMessage(ChatColor.RED + "Removed location with id " + ChatColor.WHITE + id);
    }
}
