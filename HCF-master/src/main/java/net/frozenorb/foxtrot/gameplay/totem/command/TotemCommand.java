package net.frozenorb.foxtrot.gameplay.totem.command;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.util.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class TotemCommand {

    @Command(names = {"totem"} , permission = "")
    public static void execute(CommandSender sender, @Parameter(name = "target")Player target, @Parameter(name = "tier")int tier) {
        final ItemStack itemStack = ItemBuilder.of(Material.NAME_TAG).name(ChatColor.translate("&6&lSelect Totem Effect"))
                .addToLore("", ChatColor.GRAY + "Tier: " + ChatColor.WHITE + tier, "", ChatColor.GREEN + "Click to select Totem Effect.").build();

        target.getInventory().addItem(itemStack.clone());
        target.sendMessage(ChatColor.GREEN + "You have been given a totem.");
        sender.sendMessage(ChatColor.GREEN + "Gave " + target.getName() + " a totem");
    }

}
