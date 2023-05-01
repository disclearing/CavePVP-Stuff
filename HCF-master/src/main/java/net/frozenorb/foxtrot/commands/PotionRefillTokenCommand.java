package net.frozenorb.foxtrot.commands;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.util.ItemBuilder;
import net.minecraft.util.com.google.common.collect.ImmutableList;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PotionRefillTokenCommand {

    @Command(names = {"potionrefill"}, permission = "op")
    public static void execute(CommandSender sender, @Parameter(name = "target")Player target) {
        final ItemStack itemStack = ItemBuilder.of(Material.NETHER_STAR).name("&5&k! &d&lPotion Refill Token &5&k!").setUnbreakable(true).setLore(ImmutableList.of("&7Right click to fill your inventory with potions!")).build();

        target.getInventory().addItem(itemStack);
        sender.sendMessage(ChatColor.GREEN + "Gave a potion refill token to " + target.getName());
        target.sendMessage(ChatColor.GREEN + "You were given a Potion Refill Token");
    }

}
