package org.cavepvp.profiles.command;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.cavepvp.profiles.Profiles;
import org.cavepvp.profiles.playerProfiles.PlayerProfile;

public class IgnoreCommand {
    @Command(names = {"ignore"}, permission = "")
    public static void execute(Player player, @Parameter(name = "target")Player target) {
        final PlayerProfile profile = Profiles.getInstance().getPlayerProfileHandler().fetchProfile(player.getUniqueId(), player.getName());

        if (profile.getPreferences2().getIgnoredPlayers().contains(target.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "That player has already been ignored!");
            return;
        }

        player.sendMessage(ChatColor.GREEN + "You have ignored " + ChatColor.WHITE + target.getName() + ChatColor.GREEN + ".");

        profile.getPreferences2().getIgnoredPlayers().add(target.getUniqueId());
        profile.save();
    }

    @Command(names = {"unignore"}, permission = "")
    public static void unignore(Player player, @Parameter(name = "target")Player target) {
        final PlayerProfile profile = Profiles.getInstance().getPlayerProfileHandler().fetchProfile(player.getUniqueId(), player.getName());

        if (!profile.getPreferences2().getIgnoredPlayers().contains(target.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "That player isn't already ignored!");
            return;
        }

        player.sendMessage(ChatColor.GREEN + "You have unignored " + ChatColor.WHITE + target.getName() + ChatColor.GREEN + ".");

        profile.getPreferences2().getIgnoredPlayers().remove(target.getUniqueId());
        profile.save();
    }

}
