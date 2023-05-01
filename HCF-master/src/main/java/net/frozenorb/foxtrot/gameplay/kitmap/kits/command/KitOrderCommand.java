package net.frozenorb.foxtrot.gameplay.kitmap.kits.command;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.kitmap.kits.DefaultKit;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KitOrderCommand {

    @Command(names = { "defaultkit setorder" }, description = "Sets the order of a kit", permission = "op")
    public static void execute(Player player, @Parameter(name = "kit") DefaultKit kit, @Parameter(name = "order") int order) {
        kit.setOrder(order);
        Foxtrot.getInstance().getMapHandler().getKitManager().saveDefaultKits();

        player.sendMessage(ChatColor.GREEN + "Set order of " + kit.getName() + " to " + order + "!");
    }

}
