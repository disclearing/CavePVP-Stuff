package cc.fyre.neutron.profile.namemc.task;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.profile.Profile;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class NameMCReminderTask extends BukkitRunnable {
    @Override
    public void run() {
        for (Player onlinePlayer : Neutron.getInstance().getServer().getOnlinePlayers()) {
            final Profile profile = Neutron.getInstance().getProfileHandler().fromUuid(onlinePlayer.getUniqueId());

            if (profile == null) {
                continue;
            }

            if (profile.getActiveRank() == null || profile.getActiveRank() != Neutron.getInstance().getRankHandler().getDefaultRank()) {
                continue;
            }

            if (Neutron.getInstance().getNetwork().equals(Neutron.Network.CRYPTO)) {
                continue;
            }

            onlinePlayer.sendMessage("");
            onlinePlayer.sendMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Free Rank");
            onlinePlayer.sendMessage(ChatColor.translateAlternateColorCodes('&',"&cYou have a free &7&lIron Rank &cwaiting for you!"));
            onlinePlayer.sendMessage(ChatColor.translateAlternateColorCodes('&',"&7Type &f/freerank &7to claim your free Iron Rank&7!"));
            onlinePlayer.sendMessage("");
        }
    }
}
