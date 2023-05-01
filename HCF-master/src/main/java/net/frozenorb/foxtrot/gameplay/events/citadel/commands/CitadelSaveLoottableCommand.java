package net.frozenorb.foxtrot.gameplay.events.citadel.commands;

import cc.fyre.proton.command.Command;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.events.citadel.CitadelHandler;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CitadelSaveLoottableCommand {

    @Command(names={"citadel saveloottable"}, permission="op")
    public static void citadelSaveLoottable(Player sender) {

        if (!Foxtrot.getInstance().getMapHandler().isKitMap()) {
            sender.sendMessage(ChatColor.RED + "This command may only be used on Kitmap!");
            return;
        }

        Foxtrot.getInstance().getCitadelHandler().getCitadelLoot().clear();

        for (ItemStack itemStack : sender.getInventory().getContents()) {
            if (itemStack != null && itemStack.getType() != Material.AIR) {
                Foxtrot.getInstance().getCitadelHandler().getCitadelLoot().add(itemStack);
            }
        }

        Foxtrot.getInstance().getCitadelHandler().saveCitadelInfo();
        sender.sendMessage(CitadelHandler.PREFIX + " " + ChatColor.YELLOW + "Saved Citadel loot from your inventory.");
    }

}