package net.frozenorb.foxtrot.gameplay.kitmap.kits.command;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.kitmap.kits.DefaultKit;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KitsEditCommand {
    
    @Command(names = { "defaultkit edit" }, permission = "op")
    public static void execute(Player sender, @Parameter(name = "kit", wildcard = true) DefaultKit kit) {
        kit.update(sender.getInventory());
        Foxtrot.getInstance().getMapHandler().getKitManager().saveDefaultKits();

        sender.sendMessage(ChatColor.YELLOW + "Kit " + ChatColor.BLUE + kit.getName() + ChatColor.YELLOW + " has been edited and saved.");
    }

}
