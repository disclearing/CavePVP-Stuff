package net.splodgebox.monthlycrates.utils;

import net.splodgebox.monthlycrates.Core;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CrateUtils {
    public void giveCrate(final String crateName, final Player player, final Integer amount, final CommandSender sender) {
        final List<String> lores = new ArrayList<>();
        for (final String string : Core.getInstance().getConfig().getStringList("crates." + crateName + ".crate.Lores")) {
            lores.add(string.replace("%player%", player.getName()));
        }
        player.getInventory().addItem(Util.createItemStack(Material.valueOf(Core.getInstance().getConfig().getString("crates." + crateName + ".crate.Material")), amount, Core.getInstance().getConfig().getString("crates." + crateName + ".crate.Name"), Core.getInstance().getConfig().getBoolean("crates." + crateName + ".crate.Glow"), Core.getInstance().getConfig().getInt("crates." + crateName + ".crate.ItemData"), lores));
        sender.sendMessage(Util.c(Core.getInstance().getConfig().getString("messages.success-give").replace("%prefix%", Core.getInstance().prefix).replace("%crate%", crateName).replace("%amount%", Integer.toString(amount)).replace("%player%", player.getName())));
        player.sendMessage(Util.c(Core.getInstance().getConfig().getString("messages.success-give-other").replace("%prefix%", Core.getInstance().prefix).replace("%crate%", crateName).replace("%amount%", Integer.toString(amount))));
    }
}

