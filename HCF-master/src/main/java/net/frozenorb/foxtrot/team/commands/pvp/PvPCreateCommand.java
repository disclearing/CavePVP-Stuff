package net.frozenorb.foxtrot.team.commands.pvp;

import net.frozenorb.foxtrot.Foxtrot;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.concurrent.TimeUnit;

public class PvPCreateCommand {

    @Command(names={ "pvptimer create", "timer create", "pvp create", "pvp add", "pvp give" }, permission="worldedit.*")
    public static void pvpCreate(Player sender, @Parameter(name="player", defaultValue="self") Player player) {
        Foxtrot.getInstance().getPvPTimerMap().createTimer(player.getUniqueId(), (int) TimeUnit.MINUTES.toSeconds(30));
        player.setMetadata("PVP_TIMER_BYPASS", new FixedMetadataValue(Foxtrot.getInstance(), true));
        player.sendMessage(ChatColor.YELLOW + "You have 30 minutes of PVP Timer!");

        if (sender != player) {
            sender.sendMessage(ChatColor.YELLOW + "Gave 30 minutes of PVP Timer to " + player.getName() + ".");
        }
    }

}