package org.cavepvp.profiles.command;

import cc.fyre.neutron.NeutronConstants;
import cc.fyre.piston.Piston;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.cavepvp.profiles.Profiles;
import org.cavepvp.profiles.playerProfiles.PlayerProfile;
import org.cavepvp.profiles.playerProfiles.impl.PlayerType;

public class ReplyCommand {

    @Command(
            names = {"reply", "r"},
            permission = ""
    )
    public static void execute(Player player, @Parameter(name = "message", wildcard = true) String message) {

        if (!Profiles.getInstance().getConversationCache().containsKey(player.getUniqueId()) || Profiles.getInstance().getConversationCache().get(player.getUniqueId()) == null) {
            player.sendMessage(ChatColor.RED + "You have no one to reply to.");
            return;
        }

        final Player target = Profiles.getInstance().getServer().getPlayer(Profiles.getInstance().getConversationCache().get(player.getUniqueId()));

        if (target == null) {
            player.sendMessage(ChatColor.RED + "That player is no longer online.");
            return;
        }

        final PlayerProfile profile = Profiles.getInstance().getPlayerProfileHandler().fetchProfile(player.getUniqueId(), player.getName());
        final PlayerProfile targetProfile = Profiles.getInstance().getPlayerProfileHandler().fetchProfile(target.getUniqueId(), target.getName());

        if (!Profiles.getInstance().canMessage(player, target, true)) {
            return;
        }

        Profiles.getInstance().getConversationCache().put(player.getUniqueId(), target.getUniqueId());
        Profiles.getInstance().getConversationCache().put(target.getUniqueId(), player.getUniqueId());

        player.sendMessage(ChatColor.GRAY + "(To " + target.getDisplayName() + ChatColor.GRAY + ") " + message);

        if (Piston.getInstance().getChatHandler().isFiltered(message) && !player.hasPermission(NeutronConstants.STAFF_PERMISSION)) {
            Piston.getInstance().getServer().getOnlinePlayers().stream().filter(it -> it.hasPermission(NeutronConstants.STAFF_PERMISSION) && !Piston.getInstance().getToggleStaff().contains(it.getUniqueId())).forEach(it -> {
                it.sendMessage(ChatColor.translate("&c[Filtered] " + player.getDisplayName() + " &emessaged " + target.getDisplayName() + "&7: &f" + message));
            });
            return;
        }

        if (targetProfile.getPreferences2().getIgnoredPlayers().contains(player.getUniqueId())) {
            return;
        }

        if (targetProfile.getPreferences2().getSounds().equals(PlayerType.EVERYONE) || targetProfile.getPreferences2().getSounds().equals(PlayerType.FRIENDS_ONLY) && targetProfile.getFriends().contains(player.getUniqueId())) {
            target.playSound(target.getLocation(), Sound.SUCCESSFUL_HIT, 1.0F, 1.0F);
        }

        target.sendMessage(ChatColor.GRAY + "(From " + player.getDisplayName() + ChatColor.GRAY + ") " + message);

    }

}
