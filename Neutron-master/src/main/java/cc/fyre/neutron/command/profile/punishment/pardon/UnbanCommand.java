package cc.fyre.neutron.command.profile.punishment.pardon;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.neutron.profile.attributes.punishment.impl.RemoveAblePunishment;

import cc.fyre.neutron.profile.attributes.punishment.packet.PunishmentPardonPacket;
import cc.fyre.neutron.security.AlertType;
import cc.fyre.proton.Proton;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.flag.Flag;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.util.UUIDUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class UnbanCommand {

    @Command(
            names = {"unban","pardon"},
            permission = "neutron.command.unban",
            async = true
    )
    public static void execute(CommandSender sender,@Parameter(name = "player")String name,@Parameter(name = "reason",wildcard = true)String reason,@Flag(value = "p",description = "Pardon public")boolean broadcast) {

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

        if (profile.getActivePunishment(RemoveAblePunishment.Type.BAN) == null) {
            sender.sendMessage(profile.getFancyName() + ChatColor.RED + " is not banned!");
            return;
        }

        final RemoveAblePunishment punishment = profile.getActivePunishment(RemoveAblePunishment.Type.BAN);

        punishment.setPardoner(UUIDUtils.uuid(sender.getName()));
        punishment.setPardonedAt(System.currentTimeMillis());
        punishment.setPardonedReason(reason);
        punishment.setPardonedSilent(!broadcast);

        profile.save();

        if (sender instanceof Player) {
            final Player senderPlayer = (Player) sender;

            Neutron.getInstance().getSecurityHandler().addSecurityAlert(profile.getUuid(), senderPlayer.getUniqueId(), AlertType.UNPUNISHMENTS, false, "Punishment Type: Unban", "Reason: " + reason);
        }

        Proton.getInstance().getPidginHandler().sendPacket(new PunishmentPardonPacket(profile.getUuid(),punishment.toDocument(),false,profile.getFancyName()));
    }

}
