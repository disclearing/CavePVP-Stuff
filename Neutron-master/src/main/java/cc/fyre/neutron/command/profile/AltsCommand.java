package cc.fyre.neutron.command.profile;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.NeutronConstants;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.neutron.profile.attributes.punishment.impl.RemoveAblePunishment;
import cc.fyre.neutron.profile.comparator.ProfileWeightComparator;
import cc.fyre.neutron.profile.menu.AltsMenu;
import cc.fyre.neutron.profile.menu.ProfileMainMenu;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.flag.Flag;
import cc.fyre.proton.command.param.Parameter;

import mkremins.fanciful.FancyMessage;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


public class AltsCommand {

    @Command(
            names = {"alts","alternates"},
            permission = "neutron.command.alts", async = true
    )
    public static void execute(CommandSender sender, @Parameter(name = "player")String name) {

        final Player target = Neutron.getInstance().getServer().getPlayer(name);

        Profile profile;

        if (target != null) {
            profile = Neutron.getInstance().getProfileHandler().fromUuid(target.getUniqueId());
        } else {
            profile = Neutron.getInstance().getProfileHandler().fromName(name,true,true);
        }

        if (profile == null) {
            sender.sendMessage(ChatColor.RED + name + " has never joined the server!");
            return;
        }

        final List<Profile> alts = profile.findAltsAsync().stream().map(Profile::new).sorted(new ProfileWeightComparator()).collect(Collectors.toList());

        if (alts.isEmpty()) {
            sender.sendMessage(profile.getFancyName() + ChatColor.RED + " does not have any alts.");
            return;
        }

        sender.sendMessage(ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat("-", 53));
        sender.sendMessage(profile.getFancyName() + ChatColor.RED + "'s alts: " + ChatColor.YELLOW + "[" + alts.size() + "]");
        sender.sendMessage("");
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aOnline&7, &7Offline, &cBanned, &4Blacklisted"));
        sender.sendMessage("");

        final FancyMessage fancyMessage = new FancyMessage("");

        int countedAlts = 0;

        for (Profile alt : alts) {
            countedAlts++;

            ChatColor startColor = ChatColor.GRAY;

            if (alt.getActivePunishment(RemoveAblePunishment.Type.BAN) != null) {
                startColor = ChatColor.RED;
            } else if (alt.getActivePunishment(RemoveAblePunishment.Type.BLACKLIST) != null) {
                startColor = ChatColor.DARK_RED;
            } else if (alt.getServerProfile().isOnline()) {
                startColor = ChatColor.GREEN;
            }

            fancyMessage.then(alt.getName()).color(startColor).command("/checkprofile " + alt.getName())
                    .tooltip(ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat("-", 25),
                            ChatColor.GREEN + "Click to view " + alt.getName() + "'s profile",
                            ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat("-", 25));

            if (alts.size() > countedAlts) {
                fancyMessage.color(ChatColor.GRAY).then(", ");
            }
        }

        fancyMessage.send(sender);
        sender.sendMessage(ChatColor.DARK_GRAY.toString() + ChatColor.STRIKETHROUGH + StringUtils.repeat("-", 53));
    }

    @Command(names = {"checkprofile"}, permission = "command.checkprofile")
    public static void execute(Player player, @Parameter(name = "target")String target) {
        new ProfileMainMenu(target).openMenu(player);
    }
}
