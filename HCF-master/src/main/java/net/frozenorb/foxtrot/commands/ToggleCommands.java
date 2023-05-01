package net.frozenorb.foxtrot.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.frozenorb.foxtrot.Foxtrot;
import cc.fyre.proton.command.Command;

public class ToggleCommands {

    @Command(names = {"cobble", "cobblestone"}, permission = "")
    public static void cobble(Player sender) {
        boolean val = !Foxtrot.getInstance().getCobblePickupMap().isCobblePickup(sender.getUniqueId());

        sender.sendMessage(ChatColor.YELLOW + "You are now " + (!val ? ChatColor.RED + "unable" : ChatColor.GREEN + "able") + ChatColor.YELLOW + " to pick up cobblestone while in Miner class!");
        Foxtrot.getInstance().getCobblePickupMap().setCobblePickup(sender.getUniqueId(), val);
    }

    @Command(names = {"mobdrops"}, permission = "")
    public static void mobdrops(Player sender) {
        boolean val = !Foxtrot.getInstance().getMobDropsPickupMap().isMobPickup(sender.getUniqueId());

        sender.sendMessage(ChatColor.YELLOW + "You are now " + (!val ? ChatColor.RED + "unable" : ChatColor.GREEN + "able") + ChatColor.YELLOW + " to pick up mob drops!");
        Foxtrot.getInstance().getMobDropsPickupMap().setMobPickup(sender.getUniqueId(), val);
    }

}
