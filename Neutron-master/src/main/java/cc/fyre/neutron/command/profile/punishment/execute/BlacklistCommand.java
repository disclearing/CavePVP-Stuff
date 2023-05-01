package cc.fyre.neutron.command.profile.punishment.execute;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.neutron.profile.attributes.punishment.impl.RemoveAblePunishment;
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

public class BlacklistCommand {

    @Command(
            names = {"blacklist"},
            permission = "neutron.command.blacklist",
            async = true
    )
    public static void execute(CommandSender sender,@Parameter(name = "player") String name,@Parameter(name = "reason",wildcard = true)String reason,@Flag(value = "p",description = "Execute public")boolean broadcast) {

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

        if (profile.getName().equalsIgnoreCase("SimplyTrash") && !(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage(ChatColor.RED + "You cannot kick SimplyTrash");
            return;
        }

        if (profile.getActivePunishment(RemoveAblePunishment.Type.BLACKLIST) != null) {
            sender.sendMessage(profile.getFancyName() + ChatColor.RED + " is already blacklisted!");
            return;
        }

        final RemoveAblePunishment punishment = new RemoveAblePunishment(RemoveAblePunishment.Type.BLACKLIST,UUIDUtils.uuid(sender.getName()), (long) Integer.MAX_VALUE,reason,!broadcast, UniverseAPI.getServerName());

        Neutron.getInstance().getServer().getPluginManager().callEvent(new PunishmentEvent(profile, punishment));

        profile.getPunishments().add(punishment);
        profile.save();

        if (player != null) {
            punishment.execute(player);
        }

        if (sender instanceof Player) {
            final Player senderPlayer = (Player) sender;

            Neutron.getInstance().getSecurityHandler().addSecurityAlert(profile.getUuid(), senderPlayer.getUniqueId(), AlertType.PUNISHMENTS, false, "Punishment Type: Blacklist", "Reason: " + reason);
        }

        Proton.getInstance().getPidginHandler().sendPacket(new PunishmentExecutePacket(profile.getUuid(),punishment.toDocument(),player != null,profile.getFancyName(), UniverseAPI.getServerName()));
    }

}
