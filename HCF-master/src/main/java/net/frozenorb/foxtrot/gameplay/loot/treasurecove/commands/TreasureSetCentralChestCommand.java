package net.frozenorb.foxtrot.gameplay.loot.treasurecove.commands;

import cc.fyre.proton.command.Command;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.treasurecove.TreasureCoveHandler;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class TreasureSetCentralChestCommand {

    @Command(names={"treasure setcentralchest"}, permission="op")
    public static void execute(Player sender) {
        final Block standing = sender.getLocation().getBlock();

        if (standing.getType() != Material.CHEST) {
            sender.sendMessage(ChatColor.RED + "You must be standing on a Chest!");
            return;
        }

        Foxtrot.getInstance().getTreasureCoveHandler().setCentralChest(standing.getLocation());
        sender.sendMessage(TreasureCoveHandler.PREFIX + " " + ChatColor.YELLOW + "Set the central chest location to " + ChatColor.WHITE + standing.getX() + ", " + standing.getY() + ", " + standing.getZ());
    }
}