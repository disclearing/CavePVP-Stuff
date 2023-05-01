package net.frozenorb.foxtrot.gameplay.kitmap.kits.command;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.kitmap.kits.DefaultKit;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KitIconCommand {

    @Command(names = { "defaultkit seticon" }, description = "Sets the icon of a kit", permission = "op")
    public static void execute(Player player, @Parameter(name = "kit") DefaultKit kit) {
        if (player.getItemInHand() == null) {
            player.sendMessage(ChatColor.RED + "You have no item in your hand!");
            return;
        }

        kit.setIcon(player.getItemInHand());
        Foxtrot.getInstance().getMapHandler().getKitManager().saveDefaultKits();

        player.sendMessage(ChatColor.GREEN + "Set icon of " + kit.getName() + "!");
    }

}
