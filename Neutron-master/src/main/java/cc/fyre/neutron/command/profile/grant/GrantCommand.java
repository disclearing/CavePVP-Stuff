package cc.fyre.neutron.command.profile.grant;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.NeutronConstants;
import cc.fyre.neutron.packet.ManagementBroadcastPacket;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.neutron.profile.attributes.grant.Grant;
import cc.fyre.neutron.profile.attributes.grant.packet.GrantApplyPacket;

import cc.fyre.neutron.profile.menu.grants.GrantMenu;
import cc.fyre.neutron.rank.Rank;
import cc.fyre.neutron.security.AlertType;
import cc.fyre.neutron.util.DurationWrapper;
import cc.fyre.proton.Proton;
import cc.fyre.proton.command.Command;

import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.util.TimeUtils;
import cc.fyre.proton.util.UUIDUtils;
import cc.fyre.proton.uuid.UUIDCache;
import cc.fyre.universe.UniverseAPI;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

import static org.bukkit.ChatColor.*;

public class GrantCommand {

    @Command(
            names = {"grant"},
            permission = "neutron.command.grant",
            async = true
    )
    public static void execute(CommandSender sender,@Parameter(name = "player") String name,@Parameter(name = "rank", defaultValue = "N_O_N_E")String rankName,@Parameter(name = "duration", defaultValue = "N_O_N_E")String durationName,@Parameter(name = "reason",defaultValue = "No reason specified.",wildcard = true)String reason) {

        final Player player = Neutron.getInstance().getServer().getPlayer(name);

        Profile profile;

        if (player != null) {
            profile = Neutron.getInstance().getProfileHandler().fromUuid(player.getUniqueId());
        } else {
            profile = Neutron.getInstance().getProfileHandler().fromName(name,true,true);
        }

        if (profile == null) {
            sender.sendMessage(ChatColor.YELLOW + name + ChatColor.RED + " does not exist in the Mojang database.");
            return;
        }

        if (sender instanceof Player) {
            new GrantMenu(profile).openMenu((Player) sender);
            return;
        }

        final Rank rank = Neutron.getInstance().getRankHandler().fromName(rankName);

        if (rank == null) {
            sender.sendMessage(ChatColor.RED + "Invalid rank!");
            return;
        }

        final DurationWrapper duration = NeutronConstants.findDurationWrapper(durationName);

        ogrant(sender, name, rank, duration, reason);
    }

    @Command(
            names = {"ogrant"},
            permission = "neutron.command.grant",
            async = true
    )
    public static void ogrant(CommandSender sender,@Parameter(name = "player") String name,@Parameter(name = "rank")Rank rank,@Parameter(name = "duration")DurationWrapper duration,@Parameter(name = "reason",defaultValue = "No reason specified.",wildcard = true)String reason) {

        final Player player = Neutron.getInstance().getServer().getPlayer(name);

        Profile profile;

        if (player != null) {
            profile = Neutron.getInstance().getProfileHandler().fromUuid(player.getUniqueId());
        } else {
            profile = Neutron.getInstance().getProfileHandler().fromName(name,true,true);
        }

        if (profile == null) {
            sender.sendMessage(ChatColor.YELLOW + name + ChatColor.RED + " does not exist in the Mojang database.");
            return;
        }

        if (profile.getActiveGrant(rank) != null) {
            sender.sendMessage(profile.getFancyName() + ChatColor.RED + " already has an active " + rank.getFancyName() + ChatColor.RED + " grant!");
            return;
        }

        if (duration.isPermanent()) {
            reason = duration.getSource() + " " + reason;
        }

        if (duration.isPermanent() && rank.getName().equalsIgnoreCase("VIP")) {
            sender.sendMessage(ChatColor.RED + "You can't grant VIP permanently!");
            return;
        }

        if (sender instanceof Player) {
            final Profile executorProfile = Neutron.getInstance().getProfileHandler().fetchProfile(((Player) sender).getUniqueId(), sender.getName());

            if (executorProfile == null) {
                sender.sendMessage(ChatColor.RED + "Could not find your profile! Contact an owner!");
                return;
            }

            if (executorProfile.getActiveRank().getWeight().get() <= rank.getWeight().get()) {
                sender.sendMessage(ChatColor.RED + "You can't grant a rank that has a weight equal or higher than yours.");
                return;
            }
        }

        final Grant grant = new Grant(rank,UUIDUtils.uuid(sender.getName()),duration.getDuration(),reason);

        profile.getGrants().add(grant);
        profile.recalculateGrants();
        profile.save();

        if (player == null) {
            Proton.getInstance().getPidginHandler().sendPacket(new GrantApplyPacket(profile.getUuid(),grant.toDocument()));
        }

        UUID senderUUID = sender instanceof Player ? ((Player) sender).getUniqueId() : UUIDCache.CONSOLE_UUID;

        Neutron.getInstance().sendPacketAsync(new ManagementBroadcastPacket(
                NeutronConstants.MANAGER_PERMISSION, senderUUID,
                ChatColor.translateAlternateColorCodes('&',ChatColor.translateAlternateColorCodes('&',
                        LIGHT_PURPLE + "[MC]" + LIGHT_PURPLE + "[" + UniverseAPI.getServerName() + "] &f" + (sender instanceof Player ? ((Player) sender).getDisplayName() : DARK_RED + BOLD.toString() + "Console")
                                + " &7has granted &f" + sender.getName() + " &7" + rank.getFancyName() + " &7rank for &f" + duration.getSource() + " &7for &f" + reason + "&7."))));

        if (!senderUUID.equals(UUIDCache.CONSOLE_UUID)) {
            Neutron.getInstance().getSecurityHandler().addSecurityAlert(profile.getUuid(), senderUUID, AlertType.GRANTS, true, "Granted: " + rank.getFancyName(), "Duration: " + duration.getSource(), "");
        }

        sender.sendMessage(ChatColor.GOLD + "You have granted " + profile.getFancyName() + " " + rank.getFancyName() + ChatColor.GOLD + " rank.");
    }
}
