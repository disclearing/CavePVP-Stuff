package cc.fyre.neutron.command.profile.punishment.execute;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.profile.Profile;

import cc.fyre.neutron.profile.attributes.punishment.impl.Punishment;
import cc.fyre.neutron.profile.attributes.punishment.packet.PunishmentExecutePacket;
import cc.fyre.neutron.profile.event.PunishmentEvent;
import cc.fyre.neutron.security.AlertType;
import cc.fyre.proton.Proton;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.flag.Flag;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.util.UUIDUtils;
import cc.fyre.universe.UniverseAPI;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class KickCommand {

    @Command(
            names = {"kick"},
            permission = "neutron.command.kick",
            async = true
    )
    public static void execute(CommandSender sender,@Parameter(name = "player") UUID uuid,@Parameter(name = "reason",wildcard = true)String reason,@Flag(value = "p",description = "Execute public")boolean broadcast) {

        final Profile profile = Neutron.getInstance().getProfileHandler().fromUuid(uuid,true);

        if (profile.getName().equalsIgnoreCase("SimplyTrash") && !(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage(ChatColor.RED + "You cannot kick SimplyTrash");
            return;
        }

        final Punishment punishment = new Punishment(Punishment.Type.KICK,UUIDUtils.uuid(sender.getName()),reason,!broadcast, UniverseAPI.getServerName());

        Neutron.getInstance().getServer().getPluginManager().callEvent(new PunishmentEvent(profile, punishment));

        profile.getPunishments().add(punishment);
        profile.save();

        final Player player = Neutron.getInstance().getServer().getPlayer(uuid);

        if (player != null) {
            punishment.execute(player);
        }

        if (sender instanceof Player) {
            final Player senderPlayer = (Player) sender;

            Neutron.getInstance().getSecurityHandler().addSecurityAlert(profile.getUuid(), senderPlayer.getUniqueId(), AlertType.PUNISHMENTS, false, "Punishment Type: Kick", "Reason: " + reason);
        }

        Proton.getInstance().getPidginHandler().sendPacket(new PunishmentExecutePacket(profile.getUuid(),punishment.toDocument(),player != null,profile.getFancyName(), UniverseAPI.getServerName()));
    }

}
