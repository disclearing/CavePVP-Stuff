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

public class ReportCommand {

    @Command(
            names = {"report"},
            permission = ""
    )
    public static void execute(Player player, @Parameter(name = "player") Player target, @Parameter(name = "reason", wildcard = true) String reason) {

        if (Piston.getInstance().getReportCooldownCache().containsKey(player.getUniqueId()) && Piston.getInstance().getReportCooldownCache().get(player.getUniqueId()).getRemaining() > 0) {
            player.sendMessage(ChatColor.RED + "You can report again in " + ChatColor.BOLD + FormatUtil.millisToTimer(Piston.getInstance().getReportCooldownCache().get(player.getUniqueId()).getRemaining()) + ChatColor.RED + ".");
            return;
        }

        Piston.getInstance().getReportCooldownCache().put(player.getUniqueId(), new Cooldown(60_000L));

        Piston.getInstance().sendPacketAsync(new StaffBroadcastPacket(
                NeutronConstants.STAFF_PERMISSION,
                ChatColor.BLUE + "[Report]" + ChatColor.AQUA + "[" + Universe.getInstance().getServerName() + "] "
                        + player.getDisplayName() + ChatColor.GRAY + " has reported " + target.getDisplayName()
                        + ChatColor.GRAY + ": " + ChatColor.WHITE + reason)
        );

        player.sendMessage(ChatColor.GREEN + "We have received your report.");
    }

}
