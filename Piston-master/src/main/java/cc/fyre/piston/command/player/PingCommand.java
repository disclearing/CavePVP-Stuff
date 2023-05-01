package cc.fyre.piston.command.player;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class PingCommand {

    @Command(
            names = {"ping","connection"},
            permission = ""
    )
    public static void execute(Player player,@Parameter(name = "player",defaultValue = "self")Player target) {
        player.sendMessage((target != player ? target.getDisplayName() + ChatColor.GOLD + "'s ":"") + ChatColor.GOLD + "Ping: " + ChatColor.WHITE + ((CraftPlayer)target).getHandle().ping);
    }

}
