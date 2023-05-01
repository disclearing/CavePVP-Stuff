package cc.fyre.neutron.command.profile.punishment.pardon;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.packet.BroadcastPacket;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.neutron.profile.attributes.grant.Grant;
import cc.fyre.neutron.profile.attributes.punishment.IPunishment;
import cc.fyre.neutron.profile.attributes.punishment.Punishments;
import cc.fyre.neutron.profile.attributes.punishment.impl.Punishment;
import cc.fyre.neutron.profile.attributes.punishment.impl.RemoveAblePunishment;
import cc.fyre.neutron.profile.attributes.punishment.packet.PunishmentExecutePacket;
import cc.fyre.neutron.profile.attributes.punishment.packet.PunishmentPardonPacket;
import cc.fyre.neutron.util.DurationWrapper;
import cc.fyre.proton.Proton;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.uuid.UUIDCache;
import mkremins.fanciful.FancyMessage;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ResetBansCommand {

    public static List<Profile> toSave = new ArrayList<>();

    @Command(
            names = {"punishments reset"},
            async = true,
            permission = "command.punishment.revokeall"
    )
    public static void remove(CommandSender sender, @Parameter(name = "skip", defaultValue = "0")int skip) {

        sender.sendMessage(ChatColor.GREEN + "Starting");

        toSave.clear();

        int count = skip;
        AtomicInteger punishments = new AtomicInteger(skip);

        for (Document document : Neutron.getInstance().getProfileHandler().getCollection().find()) {
            count++;

            final Profile profile = new Profile(document);

            if (profile.getActivePunishments().isEmpty()) {
                continue;
            }

            boolean changed = false;

            for (RemoveAblePunishment removeAblePunishment : profile.getActivePunishments()) {
                punishments.incrementAndGet();

                changed = true;

                punishments.incrementAndGet();

                removeAblePunishment.setPardoner(UUIDCache.CONSOLE_UUID);
                removeAblePunishment.setPardonedReason("[3.0] Punishment Reset");
                removeAblePunishment.setPardonedAt(System.currentTimeMillis());
                removeAblePunishment.setPardonedSilent(true);

                sender.sendMessage(ChatColor.translateAlternateColorCodes('&',profile.getName() + " &6has a punishment &f" + removeAblePunishment.getPunishType().name() + " &6that has been removed."));

                Proton.getInstance().getPidginHandler().sendPacket(new PunishmentPardonPacket(profile.getUuid(),removeAblePunishment.toDocument(),false,profile.getFancyName()));
            }

            if (changed) {
                toSave.add(profile);
            }
        }

        sender.sendMessage("Found a total of " + punishments.get() + " left");
    }

    @Command(names = {"saveprofiles"}, async = true, permission = "op")
    public static void execute(Player player, @Parameter(name = "skip")int skip) {
        player.sendMessage("Found " + toSave.size());

        int i = 0;

        for (Profile profile : new ArrayList<>(toSave)) {
            i++;

            if (skip > i) {
                continue;
            }

            toSave.remove(profile);

            profile.save();
            player.sendMessage(ChatColor.GREEN + "Saving " + profile.getName());
        }
    }
}
