package cc.fyre.proton.command.param.defaults;

import cc.fyre.proton.util.ItemUtils;
import cc.fyre.proton.command.param.ParameterType;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ItemStackParameterType implements ParameterType<ItemStack> {

    @Override
    public ItemStack transform(CommandSender sender, String source) {

        final ItemStack item = ItemUtils.get(source);

        if (item == null) {
            sender.sendMessage(ChatColor.RED + "No item with the name " + source + " found.");
            return null;
        }

        return item;
    }

    @Override
    public List<String> tabComplete(Player sender, Set<String> flags, String source) {
        return new ArrayList<>(); // it would probably be too intensive to go through all the aliases
    }

}
