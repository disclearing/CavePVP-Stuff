package net.frozenorb.foxtrot.gameplay.events.outposts.command.road;

import cc.fyre.proton.command.Command;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class OutpostLoadLoottableCommand {

    @Command(names={"roadoutpost loadloottable"}, permission="op")
    public static void citadelLoadLoottable(Player sender) {
        sender.getInventory().clear();
        int itemIndex = 0;

        for (ItemStack itemStack : Foxtrot.getInstance().getOutpostHandler().findRoadOutpost().getRoadOutpostLoot()) {
            sender.getInventory().setItem(itemIndex, itemStack);
            itemIndex++;
        }

        sender.sendMessage(ChatColor.YELLOW + "Loaded Outpost loot into your inventory.");
    }

}