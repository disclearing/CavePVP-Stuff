package net.frozenorb.foxtrot.gameplay.events.outposts.command;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.gameplay.events.outposts.data.Outpost;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class OutpostSetHologramCommand {
    @Command(names = {"outpost sethologram"}, permission = "op")
    public static void execute(Player player, @Parameter(name = "outpost")Outpost outpost) {
        outpost.setHologramLocation(player.getLocation().clone());
        outpost.updateHologram();
        player.sendMessage(ChatColor.GREEN + "Set the outpost hologram location for " + outpost.getDisplayColor() + ChatColor.GREEN + "!");
    }

}
