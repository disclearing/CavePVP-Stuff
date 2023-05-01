package org.cavepvp.profiles.command;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.universe.UniverseAPI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.cavepvp.profiles.Profiles;
import org.cavepvp.profiles.menu.MainMenu;
import org.cavepvp.profiles.menu.NotificationsMenu;
import org.cavepvp.profiles.menu.NotificationsTypeMenu;
import org.cavepvp.profiles.menu.other.ViewingOtherMenu;
import org.cavepvp.profiles.playerProfiles.PlayerProfile;
import org.cavepvp.profiles.playerProfiles.impl.PlayerType;

import java.util.Optional;
import java.util.UUID;

public class ProfilesCommand {

    @Command(names = {"profile", "myprofile", "profiles", "profile check", "profiles check"}, permission = "", async = true)
    public static void execute(Player player, @Parameter(name = "target", defaultValue = "self")UUID target) {
        if (UniverseAPI.getServerName().contains("AU")) {
            player.sendMessage(ChatColor.RED + "Disabled on this server!");
            return;
        }

        final Optional<PlayerProfile> playerProfile = Profiles.getInstance().getPlayerProfileHandler().requestProfile(target);

        if (!playerProfile.isPresent()) {
            player.sendMessage(ChatColor.RED + "That player doesn't have a valid profile!");
            return;
        }

        if (player.getUniqueId().toString().equalsIgnoreCase(target.toString())) {
            new MainMenu(Neutron.getInstance().getProfileHandler().fetchProfile(player.getUniqueId(), player.getName()), playerProfile.get()).openMenu(player);
            return;
        }

        final Profile profile = Neutron.getInstance().getProfileHandler().fromUuid(target, true);

        if (profile == null) {
            player.sendMessage(ChatColor.RED + "Couldn't find that Neutron profile! Contact an administrator!");
            return;
        }

        if (playerProfile.get().getPreferences2().getProfileViewing().equals(PlayerType.NOBODY)) {
            player.sendMessage(playerProfile.get().getName() + ChatColor.RED + " has hidden their profile from everyone.");
            return;
        }

        if (!playerProfile.get().getFriends().contains(player.getUniqueId()) && playerProfile.get().getPreferences2().getProfileViewing().equals(PlayerType.FRIENDS_ONLY)) {
            player.sendMessage(playerProfile.get().getName() + ChatColor.RED + " has restricted viewing their profile to friends only.");
            return;
        }

        new ViewingOtherMenu(profile, playerProfile.get()).openMenu(player);
    }

    @Command(names = {"notifications", "notis"}, permission = "")
    public static void notifications(Player player) {
        final Profile profile = Neutron.getInstance().getProfileHandler().fetchProfile(player.getUniqueId(), player.getName());
        final Optional<PlayerProfile> playerProfile = Profiles.getInstance().getPlayerProfileHandler().requestProfile(player.getUniqueId());

        if (profile == null || !playerProfile.isPresent()) {
            player.sendMessage(ChatColor.RED + "You don't have a valid profile!");
            return;
        }

        new NotificationsMenu(profile, playerProfile.get()).openMenu(player);
    }

    @Command(names = {"notification settings"}, permission = "")
    public static void execute(Player player) {
        final Profile profile = Neutron.getInstance().getProfileHandler().fetchProfile(player.getUniqueId(), player.getName());
        final Optional<PlayerProfile> playerProfile = Profiles.getInstance().getPlayerProfileHandler().requestProfile(player.getUniqueId());

        if (profile == null || !playerProfile.isPresent()) {
            player.sendMessage(ChatColor.RED + "You don't have a valid profile!");
            return;
        }

        new NotificationsTypeMenu(profile, playerProfile.get()).openMenu(player);
    }

}
