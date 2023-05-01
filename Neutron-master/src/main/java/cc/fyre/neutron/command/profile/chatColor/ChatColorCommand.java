package cc.fyre.neutron.command.profile.chatColor;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.neutron.rank.Rank;
import cc.fyre.proton.command.Command;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class ChatColorCommand {
    public static List<ChatColor> disallowedChatColors = Arrays.asList(ChatColor.DARK_AQUA, ChatColor.DARK_BLUE, ChatColor.DARK_GRAY, ChatColor.DARK_GREEN, ChatColor.DARK_PURPLE, ChatColor.DARK_RED, ChatColor.BLACK);

    @Command(names = {"chatcolor", "color"}, permission = "")
    public static void execute(Player player) {
        final Profile profile = Neutron.getInstance().getProfileHandler().fromUuid(player.getUniqueId());

        if (profile == null) {
            player.sendMessage(ChatColor.RED + "Failed to load your profile! Contact an admin.");
            return;
        }

        final Rank activeRank = profile.getActiveRank();

        if (activeRank == null) {
            player.sendMessage(ChatColor.RED + "Failed to load your rank! Contact an admin.");
            return;
        }

        new ChatColorMenu(profile, activeRank).openMenu(player);
    }
}
