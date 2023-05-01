package cc.fyre.neutron.profile.attributes.punishment;

import cc.fyre.neutron.profile.attributes.api.Executable;
import cc.fyre.neutron.profile.attributes.punishment.impl.Punishment;
import cc.fyre.neutron.profile.attributes.punishment.impl.RemoveAblePunishment;
import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.NeutronConstants;
import cc.fyre.proton.util.TimeUtils;
import mkremins.fanciful.FancyMessage;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface IPunishment extends Executable {

    Type getIType();

    UUID getUuid();

    Boolean getExecutedSilent();

    Document toDocument();
    String getServer();
    Punishments getPunishType();

    enum Type {

        NORMAL,
        REMOVE_ABLE

    }

    default void broadcast(String punishedFancyName) {

        final FancyMessage fancyMessage = new FancyMessage();
        String message = punishedFancyName + ChatColor.GREEN + " has been ";
        String toolTip = ChatColor.YELLOW + "Reason: " + ChatColor.RED;

        boolean silent = false;

        if (this instanceof Punishment) {

            final Punishment punishment = (Punishment) this;

            silent = punishment.getExecutedSilent();
            message += punishment.getPunishType().getExecutedContext() + " by " + punishment.getExecutedByFancyName() + ChatColor.GREEN + ".";
            toolTip += (punishment.getExecutedReason() == null ? "N/A" : punishment.getExecutedReason());
            toolTip += "\n" +ChatColor.YELLOW + "Server: " + ChatColor.RED + (punishment.getServer());
        } else if (this instanceof RemoveAblePunishment) {

            final RemoveAblePunishment punishment = (RemoveAblePunishment) this;

            silent = punishment.isPardoned() ? punishment.getPardonedSilent():punishment.getExecutedSilent();

            if (punishment.isPardoned()) {
                message += ChatColor.GREEN + punishment.getPunishType().getPardonedContext() + " by " + punishment.getPardonedByFancyName() + ChatColor.GREEN + ".";
                toolTip += (punishment.getPardonedReason() == null ? "N/A" : punishment.getPardonedReason());
            } else {
                message += ChatColor.GREEN + punishment.getPunishType().getExecutedContext() + (punishment.isPermanent() ? "":" for " + TimeUtils.formatIntoDetailedString((int)(punishment.getDuration()/1000))) + " by " + punishment.getExecutedByFancyName() + ChatColor.GREEN + ".";
                toolTip += (punishment.getExecutedReason() == null ? "N/A" : punishment.getExecutedReason());
                toolTip += "\n" +ChatColor.YELLOW + "Server: " + ChatColor.RED + (punishment.getServer());
            }

        }

        message = (silent ? NeutronConstants.SILENT_PREFIX + ChatColor.GREEN + " ":"") + message;

        fancyMessage.text(message);
        fancyMessage.tooltip(toolTip);

        for (Player loopPlayer : Neutron.getInstance().getServer().getOnlinePlayers()) {
            if (!loopPlayer.hasMetadata("NOT_SEE_PUNISHMENTS") && loopPlayer.hasPermission(NeutronConstants.STAFF_PERMISSION)) {
                fancyMessage.send(loopPlayer);
            } else if (!silent) {
                loopPlayer.sendMessage(message);
            }
        }


    }

    void execute(Player player);
}
