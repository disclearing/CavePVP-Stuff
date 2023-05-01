package org.cavepvp.profiles.playerProfiles.listener;

import cc.fyre.proton.Proton;
import cc.fyre.universe.UniverseAPI;
import lombok.AllArgsConstructor;
import mkremins.fanciful.FancyMessage;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.cavepvp.profiles.Profiles;
import org.cavepvp.profiles.packet.type.FriendSessionPacket;
import org.cavepvp.profiles.playerProfiles.PlayerProfile;
import org.cavepvp.profiles.playerProfiles.impl.Preferences;

import java.util.ArrayList;

@AllArgsConstructor
public class ProfileListener implements Listener {
    private Profiles instance;

    @EventHandler(priority = EventPriority.LOWEST)
    private void onAsyncJoin(AsyncPlayerPreLoginEvent event) {

        PlayerProfile playerProfile = null;

        try {
            playerProfile = this.instance.getPlayerProfileHandler().fetchProfile(event.getUniqueId(),event.getName());
        } catch (Exception ex) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, ChatColor.RED + "There was an issue contacting the API. Contact an administrator immediately.");
            ex.printStackTrace();
        }

        this.instance.getPlayerProfileHandler().getCache().put(event.getUniqueId(), playerProfile);
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        if (UniverseAPI.getServerName().contains("AU")) {
            return;
        }

        final PlayerProfile playerProfile = this.instance.getPlayerProfileHandler().fetchProfile(player.getUniqueId(), player.getName());

        if (playerProfile == null) {
            player.sendMessage(ChatColor.RED + "Failed to load your profile! Contact an administrator!");
            return;
        }

        if (playerProfile.getCoinPurchases() == null) {
            playerProfile.setCoinPurchases(new ArrayList<>());
            playerProfile.save();
        }

        if (playerProfile.getPreferences2() == null) {
            playerProfile.setPreferences2(new Preferences());
            playerProfile.save();
        }

        if (!playerProfile.getNotifications().isEmpty()) {
            Profiles.getInstance().getServer().getScheduler().runTaskLater(Profiles.getInstance(), () -> {
                if (!player.isOnline()) {
                    return;
                }

                player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 1);
                player.sendMessage("");
                new FancyMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Notifications").tooltip(ChatColor.GREEN + "Click to view all unread notifications").command("/notifications").send(player);
                new FancyMessage(ChatColor.GRAY + "You have " + ChatColor.WHITE + playerProfile.getNotifications().size() + ChatColor.GRAY + " unread notifications! " + ChatColor.GREEN + "[Click to view]").tooltip(ChatColor.GREEN + "Click to view all unread notifications").command("/notifications").send(player);
                player.sendMessage("");
            }, 5);
        }

        Proton.getInstance().getPidginHandler().sendPacket(new FriendSessionPacket(player.getUniqueId(), UniverseAPI.getServerName(), false));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerQuit(PlayerQuitEvent event) {
        if (UniverseAPI.getServerName().contains("AU")) {
            this.instance.getPlayerProfileHandler().getCache().remove(event.getPlayer().getUniqueId());
            return;
        }

        final Player player = event.getPlayer();

        final PlayerProfile playerProfile = this.instance.getPlayerProfileHandler().fetchProfile(player.getUniqueId(), player.getName());

        if (playerProfile == null) {
            this.instance.getPlayerProfileHandler().getCache().remove(event.getPlayer().getUniqueId());
            player.sendMessage(ChatColor.RED + "Failed to load your profile! Contact an administrator!");
            return;
        }

        Proton.getInstance().getPidginHandler().sendPacket(new FriendSessionPacket(player.getUniqueId(), UniverseAPI.getServerName(), true));

        this.instance.getPlayerProfileHandler().getCache().remove(event.getPlayer().getUniqueId());
    }

}
