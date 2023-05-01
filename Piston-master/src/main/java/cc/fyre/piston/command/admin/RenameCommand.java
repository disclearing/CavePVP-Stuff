package cc.fyre.piston.command.admin;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class RenameCommand {
    public static List<String> canRename = Arrays.asList("HELMET", "AXE", "BOOTS", "CHESTPLATE", "LEGGINGS", "SWORD", "SHOVEL");

    @Command(
            names = {"rename"},
            permission = "command.rename"
    )
    public static void execute(Player player,@Parameter(name = "name",wildcard = true)String name) {

        final ItemStack itemStack = player.getItemInHand();

        if (player.getItemInHand() == null) {
            player.sendMessage(ChatColor.RED + "You are not holding an item.");
            return;
        }

        if (canRename.stream().noneMatch(it -> itemStack.getType().name().contains(it)) && !player.isOp()) {
            player.sendMessage(ChatColor.RED + "You may not rename this item!");
            return;
        }

        final ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&',name));
        itemStack.setItemMeta(itemMeta);

        player.sendMessage(ChatColor.GOLD + "Renamed item to: " + ChatColor.translateAlternateColorCodes('&',name));

    }

}
