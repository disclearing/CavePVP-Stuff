package cc.fyre.neutron.command.profile;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.neutron.profile.comparator.ProfileWeightComparator;
import cc.fyre.neutron.profile.menu.AltsMenu;
import cc.fyre.neutron.profile.packet.AltLimitPacket;
import cc.fyre.proton.Proton;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.flag.Flag;
import cc.fyre.proton.command.param.Parameter;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class AltLimiterCommand {
    @Command(
            names = {"altlimer"},
            permission = "neutron.command.altlimer", async = true
    )
    public static void execute(CommandSender sender, @Parameter(name = "Max Alts") int maxalts) {
        Bukkit.getScheduler().runTaskAsynchronously(Neutron.getInstance(), new Runnable() {
            @Override
            public void run() {
                Proton.getInstance().getPidginHandler().sendPacket(new AltLimitPacket(maxalts));
            }
        });
        sender.sendMessage(ChatColor.GOLD + "Set Max Alt Limit to " + ChatColor.BOLD + maxalts + ChatColor.GOLD + "!");
    }
}
