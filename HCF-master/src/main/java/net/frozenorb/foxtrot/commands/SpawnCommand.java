package net.frozenorb.foxtrot.commands;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class SpawnCommand {

    @Command(names={ "spawn" }, permission="command.spawn")
    public static void spawn(Player sender, @Parameter(name="world", defaultValue="world") String worldName) {

        if (!sender.hasPermission("command.spawn.advanced")) {
            if (!canSpawn(sender)) {
                sender.sendMessage(ChatColor.RED + "You can't warp to Spawn unless you have a PvP Timer!");
                return;
            }

            World spawn = Foxtrot.getInstance().getServer().getWorld("Spawn");

            if (Foxtrot.getInstance().getMapHandler().isKitMap()) {
                spawn = Foxtrot.getInstance().getServer().getWorld("world");
            }

            sender.teleport(spawn.getSpawnLocation());
            sender.sendMessage(ChatColor.GREEN + "Successfully warped to Spawn!");
            return;
        }

        if (worldName.equalsIgnoreCase("s")) {
            worldName = sender.getWorld().getName();
        }

        final World world = Foxtrot.getInstance().getServer().getWorld(worldName);

        if (world == null) {
            final Player target = Foxtrot.getInstance().getServer().getPlayer(worldName);

            if (target == null) {
                sender.sendMessage(ChatColor.RED + "No world or player with that name exists!");
                return;
            }

            World spawn = Foxtrot.getInstance().getServer().getWorld("Spawn");

            if (Foxtrot.getInstance().getMapHandler().isKitMap()) {
                spawn = Foxtrot.getInstance().getServer().getWorld("world");
            }

            target.teleport(spawn.getSpawnLocation());
            return;
        }

        sender.teleport(world.getSpawnLocation().add(0.5, 0.5, 0.5));
    }

    public static boolean canSpawn(Player player) {

        if (CustomTimerCreateCommand.isSOTWTimer() && !CustomTimerCreateCommand.hasSOTWEnabled(player.getUniqueId())) {
            return true;
        }

        if (DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) {
            return true;
        }

        if (Foxtrot.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId())) {
            return true;
        }

        return false;
    }

}
