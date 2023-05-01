package cc.fyre.piston.command.admin;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class HeadCommand {

    @Command(
            names = {"head","skull"},
            permission = "command.skull"
    )
    public static void head(Player sender,@Parameter(name = "name",defaultValue = "self") String name) {

        if (name.equals("self")) {
            name = sender.getName();
        }

        final ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
        final SkullMeta meta = (SkullMeta)item.getItemMeta();

        meta.setOwner(name);

        item.setItemMeta(meta);

        sender.getInventory().addItem(new ItemStack[]{item});

        sender.sendMessage(ChatColor.GOLD + "You were given " + ChatColor.WHITE + name + ChatColor.GOLD + "'s head.");
    }
}