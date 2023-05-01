package net.frozenorb.foxtrot.commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.frozenorb.foxtrot.Foxtrot;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;

public class SetNetherBufferCommand {

    @Command(names={ "SetNetherBuffer" }, permission="op")
    public static void setNetherBuffer(Player sender, @Parameter(name="netherBuffer") int newBuffer) {
        Foxtrot.getInstance().getMapHandler().setNetherBuffer(newBuffer);
        sender.sendMessage(ChatColor.GRAY + "The nether buffer is now set to " + newBuffer + " blocks.");

        new BukkitRunnable() {

            @Override
            public void run() {
                Foxtrot.getInstance().getMapHandler().saveNetherBuffer();
            }

        }.runTaskAsynchronously(Foxtrot.getInstance());
    }

}
