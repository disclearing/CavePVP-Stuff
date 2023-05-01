package net.frozenorb.foxtrot.gameplay.clickitem.command;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.util.ItemBuilder;
import net.frozenorb.foxtrot.gameplay.clickitem.ClickItem;
import net.frozenorb.foxtrot.gameplay.loot.itemboxes.ItemBox;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ClickItemCommand {
    @Command(
            names = {"clickitem give", "clickitems give"},
            permission = "foxtrot.command.clickitem"
    )
    public static void execute(CommandSender sender,
                               @Parameter(name = "clickitem")ClickItem clickItem,
                               @Parameter(name = "amount", defaultValue = "1") int amount,
                               @Parameter(name = "player", defaultValue = "self") Player target) {
        final ItemStack itemStack = ItemBuilder.of(clickItem.getMaterial()).name(clickItem.getDisplayName()).setLore(clickItem.getLore()).build();

        itemStack.setAmount(amount);
        target.getInventory().addItem(itemStack);

        sender.sendMessage(ChatColor.GREEN + "Gave " + clickItem.getDisplayName() + ChatColor.GREEN + " to " + ChatColor.WHITE + target.getName());
    }
}
