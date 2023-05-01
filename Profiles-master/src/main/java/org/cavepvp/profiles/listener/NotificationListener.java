package org.cavepvp.profiles.listener;

import cc.fyre.neutron.profile.attributes.punishment.IPunishment;
import cc.fyre.neutron.profile.attributes.punishment.Punishments;
import cc.fyre.neutron.profile.attributes.punishment.impl.Punishment;
import cc.fyre.neutron.profile.attributes.punishment.impl.RemoveAblePunishment;
import cc.fyre.neutron.profile.event.PunishmentEvent;
import cc.fyre.neutron.util.FormatUtil;
import cc.fyre.proton.util.TimeUtils;
import cc.fyre.universe.UniverseAPI;
import lombok.AllArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.cavepvp.profiles.Profiles;
import org.cavepvp.profiles.playerProfiles.PlayerProfile;
import org.cavepvp.profiles.playerProfiles.PlayerProfileAPI;
import org.cavepvp.profiles.playerProfiles.impl.NotificationType;

import java.util.ArrayList;
import java.util.Date;

@AllArgsConstructor
public class NotificationListener implements Listener {
    private Profiles instance;

    @EventHandler(priority = EventPriority.LOW)
    private void onBan(PunishmentEvent event) {
        if (UniverseAPI.getServerName().contains("AU")) {
            return;
        }

        final PlayerProfile playerProfile = this.instance.getPlayerProfileHandler().fetchProfile(event.getProfile().getUuid(), event.getProfile().getName());

        if (playerProfile.getEnabledNotificatons() == null) {
            playerProfile.setEnabledNotificatons(new ArrayList<>());
            return;
        }

        if (!playerProfile.getEnabledNotificatons().contains(NotificationType.PUNISHMENTS)) {
            return;
        }

        final IPunishment iPunishment = event.getPunishment();

        Punishments punishments = Punishments.BAN;

        if (iPunishment instanceof RemoveAblePunishment) {
            final RemoveAblePunishment removeAblePunishment = (RemoveAblePunishment) iPunishment;

            punishments = removeAblePunishment.getPunishType();

            PlayerProfileAPI.sendNotification(event.getProfile().getUuid(),
                    "&fYou have been &c" + punishments.getExecutedContext() + "&f!",
                    "Reason: &c" + iPunishment.getExecutedReason(),
                    "Server: &c" + removeAblePunishment.getServer(),
                    "Duration: &c" + (removeAblePunishment.isPermanent() ? "Permanent" :
                            FormatUtil.millisToRoundedTime(removeAblePunishment.getDuration(), true)),
                    "Date of Punishment: &c" + TimeUtils.formatIntoCalendarString(new Date(iPunishment.getExecutedAt())));
        }

        if (iPunishment instanceof Punishment) {
            final Punishment punishment = (Punishment) iPunishment;

            PlayerProfileAPI.sendNotification(event.getProfile().getUuid(),
                    "&fYou have been &c" + punishments.getExecutedContext() + "&f!",
                    "Reason: &c" + iPunishment.getExecutedReason(),
                    "Server: &c" + punishment.getServer(),
                    "Date of Punishment: &c" + TimeUtils.formatIntoCalendarString(new Date(iPunishment.getExecutedAt())));
        }
    }

}
