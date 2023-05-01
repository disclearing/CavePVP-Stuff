package net.frozenorb.foxtrot.gameplay.kitmap.kits.command;

import net.frozenorb.foxtrot.gameplay.kitmap.kits.DefaultKit;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class KitsLoadCommand {
    
    @Command(names = { "defaultkit load" }, permission = "op")
    public static void execute(Player sender, @Parameter(name = "kit", wildcard = true) DefaultKit kit) {
        kit.apply(sender);
        
        sender.sendMessage(ChatColor.YELLOW + "Applied the " + ChatColor.BLUE + kit.getName() + ChatColor.YELLOW + ".");
    }

}
