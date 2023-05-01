package net.frozenorb.foxtrot.gameplay.armorclass.command;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.util.ItemBuilder;
import net.frozenorb.foxtrot.gameplay.armorclass.ArmorClass;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ArmorClassCommand {
    @Command(
            names = {"armorclass give", "armorclass give"},
            permission = "foxtrot.command.itemboxes"
    )
    public static void execute(CommandSender sender,
                               @Parameter(name = "armorclass") ArmorClass armorClass,
                               @Parameter(name = "amount", defaultValue = "1") int amount,
                               @Parameter(name = "player", defaultValue = "self") Player target) {
        final ItemStack itemStack = armorClass.getRedeemItem().clone();

        itemStack.setAmount(amount);
        target.getInventory().addItem(itemStack);

        sender.sendMessage(ChatColor.GREEN + "Gave " + armorClass.getDisplayName() + ChatColor.GREEN + " to " + ChatColor.WHITE + target.getName());
    }
}
