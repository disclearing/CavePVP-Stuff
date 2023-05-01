package cc.fyre.neutron.command.profile.punishment.execute;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.neutron.profile.attributes.punishment.impl.RemoveAblePunishment;
import cc.fyre.neutron.profile.attributes.punishment.packet.PunishmentExecutePacket;
import cc.fyre.neutron.profile.event.PunishmentEvent;
import cc.fyre.neutron.security.AlertType;
import cc.fyre.neutron.util.DurationWrapper;
import cc.fyre.proton.Proton;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.flag.Flag;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.util.UUIDUtils;

import cc.fyre.universe.UniverseAPI;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MuteCommand {

    @Command(
            names = {"mute","tempmute"},
            permission = "neutron.command.mute",
            async = true
    )
    public static void execute(CommandSender sender,@Parameter(name = "player") String name,@Parameter(name = "duration") DurationWrapper duration,@Parameter(name = "reason",wildcard = true, defaultValue = " ")String reason,@Flag(value = "p",description = "Execute public")boolean broadcast) {

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

        if (profile.getActivePunishment(RemoveAblePunishment.Type.MUTE) != null) {
            sender.sendMessage(profile.getFancyName() + ChatColor.RED + " is already muted!");
            return;
        }

        if (duration.isPermanent()) {
            reason = duration.getSource() + " " + reason;
        }

        if (ChatColor.stripColor(reason.replace(" ", "")).equalsIgnoreCase("")) {
            sender.sendMessage(ChatColor.RED + "You must provide a reason for this punishment!");
            return;
        }

        final RemoveAblePunishment punishment = new RemoveAblePunishment(RemoveAblePunishment.Type.MUTE,UUIDUtils.uuid(sender.getName()),duration.getDuration(),reason,!broadcast, UniverseAPI.getServerName());

        Neutron.getInstance().getServer().getPluginManager().callEvent(new PunishmentEvent(profile, punishment));

        profile.getPunishments().add(punishment);
        profile.save();

        if (player != null) {
            punishment.execute(player);
        }

        if (sender instanceof Player) {
            final Player senderPlayer = (Player) sender;

            Neutron.getInstance().getSecurityHandler().addSecurityAlert(profile.getUuid(), senderPlayer.getUniqueId(), AlertType.PUNISHMENTS, false, "Punishment Type: Mute","Reason: " + reason, "Duration: " + duration.getSource());
        }

        Proton.getInstance().getPidginHandler().sendPacket(new PunishmentExecutePacket(profile.getUuid(),punishment.toDocument(),
                player != null,profile.getFancyName(), UniverseAPI.getServerName()));
    }

}
