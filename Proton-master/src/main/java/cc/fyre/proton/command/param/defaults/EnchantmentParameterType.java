package cc.fyre.proton.command.param.defaults;

import cc.fyre.proton.util.EnchantmentWrapper;
import cc.fyre.proton.command.param.ParameterType;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;

public class EnchantmentParameterType implements ParameterType<Enchantment> {

    @Override
    public Enchantment transform(CommandSender sender,String source) {

        final EnchantmentWrapper toReturn = EnchantmentWrapper.parse(source);

        if (toReturn == null) {
            sender.sendMessage(ChatColor.RED + "Enchant " + ChatColor.YELLOW + source + ChatColor.RED + " not found.");
            return null;
        } else {
            return toReturn.getBukkitEnchantment();
        }

    }
}
