package org.cavepvp.profiles.command;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import cc.fyre.universe.UniverseAPI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.cavepvp.profiles.Profiles;
import org.cavepvp.profiles.menu.FriendsMenu;
import org.cavepvp.profiles.playerProfiles.PlayerProfile;
import org.cavepvp.profiles.playerProfiles.PlayerProfileAPI;

import java.util.Optional;
import java.util.UUID;

public class FriendCommand {

    @Command(names = {"friend add", "friend accept", "friends add", "friends accept"}, permission = "", async = true)
    public static void execute(Player player, @Parameter(name = "target")UUID target) {
        if (UniverseAPI.getServerName().contains("AU")) {
            player.sendMessage(ChatColor.RED + "Disabled on this server!");
            return;
        }

        PlayerProfileAPI.sendFriendRequest(player, target);
    }

    @Command(names = {"friend", "friends"}, permission = "", async = true)
    public static void friends(Player player) {
        if (UniverseAPI.getServerName().contains("AU")) {
            player.sendMessage(ChatColor.RED + "Disabled on this server!");
            return;
        }

        final Profile profile = Neutron.getInstance().getProfileHandler().fetchProfile(player.getUniqueId(), player.getName());
        final Optional<PlayerProfile> playerProfile = Profiles.getInstance().getPlayerProfileHandler().requestProfile(player.getUniqueId());

        if (profile == null || !playerProfile.isPresent()) {
            player.sendMessage(ChatColor.RED + "You don't have a valid profile!");
            return;
        }

        if (!playerProfile.get().getPreferences2().isFriendRequests()) {
            player.sendMessage(playerProfile.get().getName() + ChatColor.RED + " has disabled friend requests.");
            return;
        }

        new FriendsMenu(profile, playerProfile.get()).openMenu(player);
    }
}
