package net.frozenorb.foxtrot.gameplay.events.outposts.command.road;

import cc.fyre.proton.command.Command;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class OutpostSaveLoottableCommand {

    @Command(names={"roadoutpost saveloottable"}, permission="op")
    public static void citadelSaveLoottable(Player sender) {

        Foxtrot.getInstance().getOutpostHandler().findRoadOutpost().getRoadOutpostLoot().clear();

        for (ItemStack itemStack : sender.getInventory().getContents()) {
            if (itemStack != null && itemStack.getType() != Material.AIR) {
                Foxtrot.getInstance().getOutpostHandler().findRoadOutpost().getRoadOutpostLoot().add(itemStack);
            }
        }

        Foxtrot.getInstance().getOutpostHandler().findRoadOutpost().saveOutpostLoot();
        sender.sendMessage(ChatColor.YELLOW + "Saved Outpost loot from your inventory.");
    }

}