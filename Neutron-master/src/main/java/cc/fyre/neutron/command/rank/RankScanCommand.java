package cc.fyre.neutron.command.rank;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.neutron.profile.attributes.grant.Grant;
import cc.fyre.neutron.rank.Rank;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.flag.Flag;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.proton.uuid.UUIDCache;
import net.milkbowl.vault.chat.Chat;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RankScanCommand {

    @Command(
            names = {"ranks scan","rank scan"},
            async = true,
            permission = "neutron.command.rank.scan"
    )
    public static void execute(CommandSender sender, @Parameter(name = "rank") Rank rank, @Parameter(name = "reason", wildcard = true)String reason, @Flag(value = {"a","active"})boolean active, @Flag(value = {"g"})boolean any) {
        sender.sendMessage(ChatColor.GOLD + "Scanning rank " + rank.getFancyName() + ChatColor.GOLD + " this may take a bit " + (any ? "Any reason" : ""));

        new Thread(() -> {
            StringBuilder sb = new StringBuilder();
            for (Document document : Neutron.getInstance().getProfileHandler().getCollection().find()) {

                final Profile profile = new Profile(document);
                final Grant grant = profile.getActiveGrant(rank);

                if(grant != null) {

                    if (!any) {
                        if (!grant.getExecutedReason().toLowerCase().contains(reason.toLowerCase())) {
                            continue;
                        }
                    }

                    if (grant.isPermanent()) {
                        continue;
                    }

                    if(sb.length() != 0) {
                        sb.append(", ");
                    }
                    if (active && profile.getActiveRank() == rank) {
                        sb.append(profile.getFancyName());
                    } else
                        sb.append(profile.getFancyName());
                }
            }
            sender.sendMessage(ChatColor.GOLD + "Users: \n" + ChatColor.GOLD + sb.toString() + ChatColor.GRAY + " (" +
                    sb.toString().split(", ").length + ") " + ChatColor.GOLD + " are in rank: " + rank.getFancyName());
        }).start();
    }
    @Command(
            names = {"ranks scan remove","rank scan remove"},
            async = true,
            permission = "neutron.command.rank.scan"
    )
    public static void executeRemove(CommandSender sender, @Parameter(name = "rank") Rank rank) {
        if(sender instanceof Player) {
            sender.sendMessage(ChatColor.RED + "This is disabled for players");
            return;
        }
        sender.sendMessage(ChatColor.GOLD + "Scanning rank " + rank.getFancyName() + ChatColor.GOLD + " this may take a bit");
        new Thread(() -> {
            StringBuilder sb = new StringBuilder();
            for (Document document : Neutron.getInstance().getProfileHandler().getCollection().find()) {

                final Profile profile = new Profile(document);
                final Grant grant = profile.getActiveGrant(rank);
                if (grant != null) {
                    String reason = grant.getExecutedReason().toLowerCase();

                    if (reason.toLowerCase().contains("purchased") || reason.toLowerCase().contains("voucher")) {
                        continue;
                    }

                    if(sb.length() != 0) {
                        sb.append(", ");
                    }
                    grant.setPardoner(UUIDCache.CONSOLE_UUID);
                    grant.setPardonedAt(System.currentTimeMillis());
                    grant.setPardonedReason("Automated Removal for anyone who hasn't purchased or won the ranks.");
                    profile.recalculateGrants();
                    profile.save();
                    sb.append(profile.getName());
                }
            }
            sender.sendMessage(ChatColor.GOLD + "Removed: \n" + ChatColor.GOLD + sb + ChatColor.GRAY + " (" +
                    sb.toString().split(", ").length + ") " + ChatColor.GOLD + " from rank: " + rank.getFancyName());

        }).start();
    }
}
