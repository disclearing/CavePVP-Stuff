package org.cavepvp.profiles.command;

import cc.fyre.proton.command.Command;
import cc.fyre.universe.UniverseAPI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.cavepvp.profiles.Profiles;
import org.cavepvp.profiles.menu.PreferencesMenu;
import org.cavepvp.profiles.playerProfiles.PlayerProfile;

public class PreferencesCommand {
    @Command(names = {"preferences", "prefs"}, permission = "")
    public static void execute(Player player) {
        if (UniverseAPI.getServerName().contains("AU")) {
            player.sendMessage(ChatColor.RED + "Disabled on this server!");
            return;
        }

        final PlayerProfile playerProfile = Profiles.getInstance().getPlayerProfileHandler().fetchProfile(player.getUniqueId(), player.getName());

        new PreferencesMenu(playerProfile).openMenu(player);
    }

}
