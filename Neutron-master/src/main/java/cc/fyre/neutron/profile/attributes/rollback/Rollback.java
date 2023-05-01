package cc.fyre.neutron.profile.attributes.rollback;

import cc.fyre.neutron.profile.attributes.api.Executable;
import cc.fyre.neutron.profile.attributes.grant.Grant;
import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.NeutronConstants;
import cc.fyre.neutron.profile.Profile;

import cc.fyre.neutron.profile.attributes.punishment.impl.RemoveAblePunishment;
import lombok.AllArgsConstructor;
import lombok.Getter;

import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
public class Rollback implements Executable {

    @Getter private UUID executor;
    @Getter private Long executedAt;
    @Getter private String executedReason;

    @Getter private boolean log;
    @Getter private int amount;

    @Getter private UUID executorTarget;
    @Getter private Long duration;
    @Getter private RollbackType type;

    public void execute(CommandSender sender) {

        final List<Profile> toSave = new ArrayList<>();

        for (Player loopPlayer : Neutron.getInstance().getServer().getOnlinePlayers()) {

            if (!loopPlayer.hasPermission(NeutronConstants.MANAGER_PERMISSION)) {
                continue;
            }

            loopPlayer.sendMessage(ChatColor.RED + "⚠ " + ChatColor.DARK_RED.toString() + ChatColor.STRIKETHROUGH + "------------------------" + ChatColor.RED + " ⚠");
            sender.sendMessage(Neutron.getInstance().getProfileHandler().fromUuid(this.executor).getFancyName() + ChatColor.RED + " is executing a rollback for " + this.type.getReadable() + ".");
            sender.sendMessage(ChatColor.RED + "⚠ " + ChatColor.DARK_RED.toString() + ChatColor.STRIKETHROUGH + "------------------------" + ChatColor.RED + " ⚠");
        }

        Neutron.getInstance().getServer().getScheduler().runTaskAsynchronously(Neutron.getInstance(),() -> {

            for (Document document : Neutron.getInstance().getMongoHandler().getMongoDatabase().getCollection(NeutronConstants.PROFILE_COLLECTION).find()) {

                final Profile profile = new Profile(document);

                if (this.type == RollbackType.PUNISHMENT) {

                    if (profile.getActivePunishments().isEmpty()) {
                        continue;
                    }

                    if (!toSave.contains(profile)) {
                        toSave.add(profile);
                    }

                    for (RemoveAblePunishment activePunishment : profile.getActivePunishments()) {

                        if (!activePunishment.getExecutor().equals(this.executorTarget)) {
                            continue;
                        }

                        if (System.currentTimeMillis() - activePunishment.getExecutedAt() <= this.duration) {
                            activePunishment.setPardoner(this.executor);
                            activePunishment.setPardonedAt(this.executedAt);
                            activePunishment.setPardonedSilent(true);
                            activePunishment.setPardonedReason(this.executedReason + " (Rollback)");

                            if (this.log) {
                                sender.sendMessage(ChatColor.GOLD + "Pardoned " + ChatColor.WHITE + activePunishment.getPunishType().getReadable() + ChatColor.GRAY + " -> " + profile.getFancyName() + ChatColor.GOLD + ".");
                            }

                            this.amount++;
                        }
                    }

                } else if (this.type == RollbackType.GRANT) {

                    if (profile.getActiveGrants().isEmpty()) {
                        continue;
                    }

                    if (!toSave.contains(profile)) {
                        toSave.add(profile);
                    }

                    for (Grant grant : profile.getActiveGrants()) {

                        if (!grant.getExecutor().equals(this.executorTarget)) {
                            continue;
                        }

                        if (System.currentTimeMillis() - grant.getExecutedAt() <= this.duration) {
                            grant.setPardoner(this.executor);
                            grant.setPardonedAt(this.executedAt);
                            grant.setPardonedReason(this.executedReason + " (Rollback)");

                            if (this.log && sender != null) {
                                sender.sendMessage(ChatColor.GOLD + "Removed grant " + grant.getRank().getFancyName() + ChatColor.GRAY + " -> " + profile.getFancyName() + ChatColor.GOLD + ".");
                            }

                            this.amount++;
                        }
                    }

                }
            }

            toSave.forEach(Profile::save);

            final Document document = new Document();

            document.put("type",this.type.name());
            document.put("amount",this.amount);
            document.put("executedReason",this.executedReason);
            document.put("executor",this.executor.toString());
            document.put("executorTarget",this.executorTarget.toString());
            document.put("duration",this.duration);

            Neutron.getInstance().getMongoHandler().getMongoDatabase().getCollection(NeutronConstants.ROLLBACK_LOG_COLLECTION).insertOne(document);

            if (sender != null) {
                sender.sendMessage(ChatColor.RED + "⚠ " + ChatColor.DARK_RED.toString() + ChatColor.STRIKETHROUGH + "------------------------" + ChatColor.RED + " ⚠");
                sender.sendMessage(ChatColor.RED + "Removed a total of " + ChatColor.WHITE + amount + ChatColor.RED + " " + this.type.getReadable() + ".");
                sender.sendMessage(ChatColor.RED + "⚠ " + ChatColor.DARK_RED.toString() + ChatColor.STRIKETHROUGH + "------------------------" + ChatColor.RED + " ⚠");
            }

        });

    }

    public Rollback(Document document) {
        this.type = RollbackType.valueOf(document.getString("type").toUpperCase());
        this.amount = document.getInteger("amount");
        this.executedReason = document.getString("executedReason");
        this.executor = UUID.fromString(document.getString("executor"));
        this.executorTarget = UUID.fromString(document.getString("executorTarget"));
        this.duration = document.getLong("duration");
    }
}
