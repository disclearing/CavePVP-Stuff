package net.frozenorb.foxtrot.gameplay.loot.itemboxes.command;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.util.ItemBuilder;
import net.frozenorb.foxtrot.gameplay.loot.itemboxes.ItemBox;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemBoxCommand {
    @Command(
            names = {"itemboxes give", "itembox give"},
            permission = "foxtrot.command.itemboxes"
    )
    public static void execute(CommandSender sender,
                               @Parameter(name = "itembox") ItemBox itemBox,
                               @Parameter(name = "amount", defaultValue = "1") int amount,
                               @Parameter(name = "player", defaultValue = "self") Player target) {
        final ItemStack itemStack = ItemBuilder.of(itemBox.getMaterial()).name(itemBox.getDisplayName()).setLore(itemBox.getLore()).build();

        itemStack.setAmount(amount);
        target.getInventory().addItem(itemStack);

        sender.sendMessage(ChatColor.GREEN + "Gave " + itemBox.getDisplayName() + ChatColor.GREEN + " to " + ChatColor.WHITE + target.getName());
    }
}
