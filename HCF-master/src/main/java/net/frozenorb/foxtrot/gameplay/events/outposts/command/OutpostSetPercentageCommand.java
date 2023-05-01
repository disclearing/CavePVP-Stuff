package net.frozenorb.foxtrot.gameplay.events.outposts.command;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.gameplay.events.outposts.data.Outpost;
import net.minecraft.util.com.google.common.util.concurrent.AtomicDouble;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class OutpostSetPercentageCommand {
    @Command(names = {"outpost setpercentage"}, permission = "op")
    public static void execute(Player player, @Parameter(name = "outpost")Outpost outpost, @Parameter(name = "percentage")double percentage) {
        if (percentage > 100) {
            player.sendMessage(ChatColor.RED + "Can't do a number above 100!");
            return;
        }

        if (percentage < 0) {
            player.sendMessage(ChatColor.RED + "Can't do a numer below 0!");
            return;
        }

        outpost.setPercentage(new AtomicDouble(percentage));
        player.sendMessage(ChatColor.GREEN + "Set the " + outpost.getDisplayColor() + " Outpost " + ChatColor.GREEN + "percentage to " + ChatColor.WHITE + percentage + "%" + ChatColor.GREEN + "!");
    }

}
