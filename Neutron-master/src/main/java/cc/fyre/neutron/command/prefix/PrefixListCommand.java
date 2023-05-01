package cc.fyre.neutron.command.prefix;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.prefix.Prefix;
import cc.fyre.neutron.prefix.menu.PrefixListMenu;

import cc.fyre.proton.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PrefixListCommand {

    @Command(
            names = {"prefix list"},
            permission = "neutron.command.prefix.list"
    )
    public static void execute(CommandSender sender) {

        if (!(sender instanceof Player)) {

            for (Prefix prefix : Neutron.getInstance().getPrefixHandler().getSortedValueCache()) {
                sender.sendMessage(prefix.getName() + ChatColor.GRAY + " (" + prefix.getWeight() + ") (" + prefix.getDisplay() + ChatColor.GRAY + ")");
            }

            return;
        }

        new PrefixListMenu().openMenu((Player)sender);
    }

}
