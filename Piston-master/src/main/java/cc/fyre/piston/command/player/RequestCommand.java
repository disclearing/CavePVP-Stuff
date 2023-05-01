package cc.fyre.piston.command.player;

import cc.fyre.neutron.NeutronConstants;
import cc.fyre.neutron.util.FormatUtil;
import cc.fyre.piston.Piston;
import cc.fyre.piston.packet.StaffBroadcastPacket;
import cc.fyre.piston.util.Cooldown;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.universe.Universe;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class RequestCommand {

    @Command(
            names = {"request", "helpop"},
            permission = ""
    )
    public static void execute(Player player, @Parameter(name = "reason", wildcard = true) String reason) {

        if (Piston.getInstance().getRequestCooldownCache().containsKey(player.getUniqueId()) && Piston.getInstance().getRequestCooldownCache().get(player.getUniqueId()).getRemaining() > 0) {
            player.sendMessage(ChatColor.RED + "You can request again in " + ChatColor.BOLD + FormatUtil.millisToTimer(Piston.getInstance().getRequestCooldownCache().get(player.getUniqueId()).getRemaining()) + ChatColor.RED + ".");
            return;
        }

        Piston.getInstance().getRequestCooldownCache().put(player.getUniqueId(), new Cooldown(60_000L));

        Piston.getInstance().sendPacketAsync(new StaffBroadcastPacket(
                NeutronConstants.STAFF_PERMISSION,
                ChatColor.BLUE + "[Request]" + ChatColor.AQUA + "[" + Universe.getInstance().getServerName() + "] " +
                        player.getDisplayName() + ChatColor.GRAY + " has requested assistance: " + ChatColor.WHITE + reason)
        );

        player.sendMessage(ChatColor.GREEN + "We have received your request.");
    }

}
