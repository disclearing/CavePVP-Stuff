package org.cavepvp.suge.kit.command;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.cavepvp.suge.Suge;
import org.cavepvp.suge.kit.KitHandler;
import org.cavepvp.suge.kit.data.Kit;

import java.util.Map;

public class KitResetCooldownCommand {

    @Command(names = {"kit resetcooldown"}, permission = "op")
    public static void execute(Player player,  @Parameter(name = "target") Player target, @Parameter(name = "kit") Kit kit) {
        final KitHandler kitHandler = Suge.getInstance().getKitHandler();

        if (!kitHandler.getCooldowns().containsKey(target.getUniqueId())) {
            player.sendMessage(ChatColor.WHITE + target.getName() + ChatColor.GOLD + " is not on cooldown for the " + ChatColor.WHITE + kit.getName() + " kit" + ChatColor.GOLD + ".");
            return;
        }

        final Map<String, Long> allKitCooldowns = kitHandler.getCooldowns().get(target.getUniqueId());

        if (!allKitCooldowns.containsKey(kit.getName().toLowerCase())) {
            player.sendMessage(ChatColor.WHITE + target.getName() + ChatColor.GOLD + " is not on cooldown for the " + ChatColor.WHITE + kit.getName() + " kit" + ChatColor.GOLD + ".");
            return;
        }

        player.sendMessage(ChatColor.GOLD + "Reset " + ChatColor.WHITE + target.getName() + "'s" + ChatColor.GOLD + " cooldown for " + ChatColor.WHITE + kit.getName() + ChatColor.GOLD + ".");
        allKitCooldowns.remove(kit.getName().toLowerCase());
        kitHandler.getCooldowns().put(target.getUniqueId(), allKitCooldowns);
    }
}

