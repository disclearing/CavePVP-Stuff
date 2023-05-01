package cc.fyre.neutron.command.profile.punishment.execute;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.NeutronConstants;
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
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;

public class BanCommand {

    public static List<BukkitTask> tasks = new ArrayList<>();

    @Command(
            names = {"ban", "tempban", "tban"},
            permission = "neutron.command.ban",
            async = true
    )
    public static void execute(CommandSender sender,
                               @Parameter(name = "player") String name,
                               @Parameter(name = "duration") DurationWrapper duration,
                               @Parameter(name = "reason", wildcard = true, defaultValue = " ") String reason,
                               @Flag(value = "p", description = "Execute this punishment publicly") boolean broadcast, @Flag(value = "l", description = "Stop their combat logger from spawning") boolean logger) {

        final Player player = Neutron.getInstance().getServer().getPlayer(name);

        Profile profile;

        if (player != null) {
            profile = Neutron.getInstance().getProfileHandler().fromUuid(player.getUniqueId());
        } else {
            profile = Neutron.getInstance().getProfileHandler().fromName(name, true, true);
        }

        if (profile == null) {
            sender.sendMessage(ChatColor.YELLOW + name + ChatColor.RED + " does not exist in the Mojang database.");
            return;
        }

        if (profile.getName().equalsIgnoreCase("SimplyTrash") && !(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage(ChatColor.RED + "You cannot kick SimplyTrash");
            return;
        }

        if (profile.getActivePunishment(RemoveAblePunishment.Type.BAN) != null) {
            sender.sendMessage(profile.getFancyName() + ChatColor.RED + " is already banned!");
            return;
        }

        if (duration.isPermanent()) {
            reason = duration.getSource() + " " + reason;
        }

        if (ChatColor.stripColor(reason.replace(" ", "")).equalsIgnoreCase("")) {
            sender.sendMessage(ChatColor.RED + "You must provide a reason for this punishment!");
            return;
        }

        if (sender instanceof Player) {
            final Player senderPlayer = (Player) sender;

            if (!sender.isOp()) {
                if (Neutron.cooldowns.containsKey(senderPlayer.getUniqueId()) && Neutron.cooldowns.getOrDefault(senderPlayer.getUniqueId(), 0) > 4) {
                    Neutron.getInstance().getServer().dispatchCommand(Neutron.getInstance().getServer().getConsoleSender(), "ban " + sender.getName() + " Compromised Account #2");
                }

                if (profile.getEffectivePermissions().contains(NeutronConstants.STAFF_PERMISSION)) {
                    Neutron.getInstance().getServer().dispatchCommand(Neutron.getInstance().getServer().getConsoleSender(), "ban " + sender.getName() + " Compromised Account #3");
                }

                Neutron.cooldowns.put(senderPlayer.getUniqueId(), Neutron.cooldowns.getOrDefault(senderPlayer.getUniqueId(), 0) + 1);

                final List<BukkitTask> newTasks = new ArrayList<>(tasks);
                newTasks.forEach(it -> {
                    it.cancel();
                    tasks.remove(it);
                });

                tasks.add(Neutron.getInstance().getServer().getScheduler().runTaskLater(Neutron.getInstance(), () -> {
                    Neutron.cooldowns.remove(senderPlayer.getUniqueId());
                }, 20 * 25));
            }

            Neutron.getInstance().getSecurityHandler().addSecurityAlert(profile.getUuid(), senderPlayer.getUniqueId(), AlertType.PUNISHMENTS, false, "Punishment Type: Ban", "Reason: " + reason, "Duration: " + duration.getSource());
        }

        if (logger && player != null) {
            player.setMetadata("loggedout", new FixedMetadataValue(Neutron.getInstance(), true));
        }

        final RemoveAblePunishment punishment = new RemoveAblePunishment(
                RemoveAblePunishment.Type.BAN, UUIDUtils.uuid(sender.getName()), duration.getDuration(), reason, !broadcast, UniverseAPI.getServerName()
        );

        Neutron.getInstance().getServer().getPluginManager().callEvent(new PunishmentEvent(profile, punishment));

        profile.getPunishments().add(punishment);
        profile.save();

        if (player != null) {
            punishment.execute(player);
        }

        Proton.getInstance().getPidginHandler().sendPacket(new PunishmentExecutePacket(
                profile.getUuid(), punishment.toDocument(), player != null, profile.getFancyName(), UniverseAPI.getServerName()
        ));
    }
}
