package net.frozenorb.foxtrot.gameplay.events.outposts.command;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.gameplay.events.outposts.data.Outpost;
import net.minecraft.util.com.google.common.util.concurrent.AtomicDouble;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class OutpostResetCommand {

    @Command(names = {"outpost reset"}, permission = "op")
    public static void execute(Player player, @Parameter(name = "outpost")Outpost outpost) {
        outpost.setControl(null);
        outpost.getAttacking().clear();
        outpost.getPlayers().clear();
        outpost.setLastMessage(0);
        outpost.setTimeCaptured(0);
        outpost.setPercentage(new AtomicDouble(0));
        player.sendMessage(ChatColor.RED + "Fully reset outpost's control, attackers, players, last message, time captured and percentage");
    }

}
