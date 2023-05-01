package net.frozenorb.foxtrot.commands;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.neutron.profile.attributes.punishment.IPunishment;
import cc.fyre.neutron.profile.attributes.punishment.impl.Punishment;
import cc.fyre.neutron.profile.attributes.punishment.impl.RemoveAblePunishment;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.util.TimeUtils;
import cc.fyre.proton.util.UUIDUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.client.model.DBCollectionFindOptions;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.persist.maps.PlaytimeMap;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class StaffBreakdownCommand {

    @Command(names = {"staffbreakdown"}, permission = "op", async = true)
    public static void execute(CommandSender sender, @Parameter(name = "target") UUID uuid) {
        final String name = UUIDUtils.name(uuid);

        sender.sendMessage(System.currentTimeMillis()-TimeUnit.DAYS.toMillis(7) + " <--");
        sender.sendMessage(ChatColor.GREEN + "Displaying staff breakdown for the last week for " + name + "...");

        final Player player = Foxtrot.getInstance().getServer().getPlayer(uuid);
        final Profile profile = Neutron.getInstance().getProfileHandler().fromUuid(uuid, true);

        if (profile == null) {
            player.sendMessage(ChatColor.RED + "Unable to request profile belonging to " + name + "!");
            return;
        }

        final DBCollection dbCollection = Foxtrot.getInstance().getMongoPool().getDB(Foxtrot.MONGO_DB_NAME).getCollection("Deaths");
        final int refunds = dbCollection.find(new BasicDBObject("refundedBy", uuid.toString().replace("-", "")), new DBCollectionFindOptions()).count();

        int playtimeTime = (int) Foxtrot.getInstance().getPlaytimeMap().getPlaytime(uuid);

        final Map<UUID, Punishment> executedPunishments = new HashMap<>();
        final Map<UUID, RemoveAblePunishment> executedRemoveAblePunishments = new HashMap<>();
        final Map<UUID,RemoveAblePunishment> pardonedRemoveAblePunishments = new HashMap<>();

        for (Document document : Neutron.getInstance().getProfileHandler().getCollection().find()) {

            final Profile targetProfile = new Profile(document);

            if (targetProfile.getPunishments().isEmpty()) {
                continue;
            }

            for (IPunishment punishment : targetProfile.getPunishments()) {
                if (punishment.getExecutedAt() < System.currentTimeMillis()-TimeUnit.DAYS.toMillis(7)) {
                    continue;
                }

                if (punishment.getExecutor().equals(uuid)) {
                    if (punishment.getIType() == IPunishment.Type.NORMAL) {
                        executedPunishments.put(targetProfile.getUuid(),(Punishment)punishment);
                    } else if (punishment.getIType() == IPunishment.Type.REMOVE_ABLE) {
                        executedRemoveAblePunishments.put(targetProfile.getUuid(),(RemoveAblePunishment)punishment);
                    }
                }
                if (punishment instanceof RemoveAblePunishment && ((RemoveAblePunishment)punishment).isPardoned() && ((RemoveAblePunishment)punishment).getPardoner().equals(uuid)) {
                    pardonedRemoveAblePunishments.put(targetProfile.getUuid(),(RemoveAblePunishment)punishment);
                }
            }
        }

        if (player != null) {
            playtimeTime += Foxtrot.getInstance().getPlaytimeMap().getCurrentSession(player.getUniqueId()) / 1000;
        }

        sender.sendMessage("");
        sender.sendMessage(name + ChatColor.GOLD + " Breakdown");
        sender.sendMessage("");
        sender.sendMessage(ChatColor.translate("&6&lGeneral Info"));
        sender.sendMessage(ChatColor.translate("&ePlaytime: &f" + TimeUtils.formatIntoDetailedString(playtimeTime)));
        sender.sendMessage(ChatColor.translate("&eRestored Inventories: &f" + refunds));
        sender.sendMessage("");
        sender.sendMessage(ChatColor.translate("&6&lPunishments"));
        sender.sendMessage(ChatColor.translate("&eTotal Bans (Last 7 days): &f" + executedRemoveAblePunishments.values().stream().filter(it -> it.getType() == RemoveAblePunishment.Type.BAN).count()));
        sender.sendMessage(ChatColor.translate("&eTotal Mutes (Last 7 days): &f" + executedRemoveAblePunishments.values().stream().filter(it -> it.getType() == RemoveAblePunishment.Type.MUTE).count()));
        sender.sendMessage(ChatColor.translate("&eTotal Unbans (Last 7 days): &f" + pardonedRemoveAblePunishments.values().stream().filter(it -> it.getType() == RemoveAblePunishment.Type.BAN).count()));
        sender.sendMessage(ChatColor.translate("&eTotal Unmutes (Last 7 days): &f" + pardonedRemoveAblePunishments.values().stream().filter(it -> it.getType() == RemoveAblePunishment.Type.MUTE).count()));
        sender.sendMessage(ChatColor.translate("&eTotal Warns (Last 7 days): &f" + executedPunishments.values().stream().filter(it -> it.getType() == Punishment.Type.WARN).count()));
        sender.sendMessage(ChatColor.translate("&eTotal Kicks (Last 7 days): &f" + executedPunishments.values().stream().filter(it -> it.getType() == Punishment.Type.KICK).count()));
        sender.sendMessage("");
    }

}
