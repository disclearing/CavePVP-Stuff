package cc.fyre.neutron.profile.namemc.commands;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.listener.events.FreeRankEvent;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.neutron.profile.namemc.NameMCUtil;
import cc.fyre.neutron.util.PlayerUtil;
import cc.fyre.proton.Proton;
import cc.fyre.proton.command.Command;
import cc.fyre.universe.UniverseAPI;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.spigotmc.SpigotConfig;

public class FreeRankCommand {
    public static boolean verify = true;

    @Command(names = {"verify", "verification"}, permission = "", async = true)
    public static void execute(Player player) {
        if (Neutron.getInstance().getNetwork().equals(Neutron.Network.CRYPTO)) {
            player.sendMessage(SpigotConfig.unknownCommandMessage);
            return;
        }

        if (!verify) {
            player.sendMessage(ChatColor.RED + "Verify is currently disabled!");
            return;
        }

        final Profile profile = Neutron.getInstance().getProfileHandler().fromUuid(player.getUniqueId());

        if (profile == null) {
            player.sendMessage(ChatColor.RED + "Couldn't load your profile! Contact an admin!");
            return;
        }

        if (profile.getGrants().stream().anyMatch(it -> it.getRank().getName().equalsIgnoreCase("Iron"))) {
            player.sendMessage(ChatColor.RED + "You have already been verified!");
            return;
        }

        final Server server = Neutron.getInstance().getServer();

        server.getScheduler().runTaskAsynchronously(Neutron.getInstance(), () -> {

            if (profile.getVotes() < 2) {
                player.sendMessage(ChatColor.RED + "You haven't voted for us twice! Type /freerank to view the links!");
                return;
            }

            NameMCUtil.verify(profile);

            server.broadcastMessage("");
            server.broadcastMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Free Rank");
            server.broadcastMessage(ChatColor.translateAlternateColorCodes('&',player.getName() + " &chas just claimed their free &7&lIron Rank&c."));
            server.broadcastMessage(ChatColor.translateAlternateColorCodes('&',"&7You can do the same by typing &f/freerank&7!"));
            server.broadcastMessage("");

            player.sendMessage("");
            player.sendMessage(ChatColor.GREEN + "You have been verified! Thank you for voting for us!");
            player.sendMessage("");

            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);

            if (UniverseAPI.getServerName().equalsIgnoreCase("Fasts")) {
                PlayerUtil.sendTitle(player, "&4&lFree Rank", "Congratulations! Type &c/reclaim &ffor your keys!");
            }

            Neutron.getInstance().getServer().getPluginManager().callEvent(new FreeRankEvent(player, profile));
        });
    }

    @Command(names = {"namemc"}, permission = "")
    public static void nameMc(Player player) {
        if (Neutron.getInstance().getNetwork().equals(Neutron.Network.CRYPTO)) {
            player.sendMessage(SpigotConfig.unknownCommandMessage);
            return;
        }

        player.sendMessage("");
        player.sendMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "NameMC");
        player.sendMessage(ChatColor.GRAY + "Like us on NameMC to receive a Free Rank!");
        player.sendMessage(ChatColor.RED + "Link: " + ChatColor.WHITE + "https://namemc.com/server/cavepvp.org" + ChatColor.GRAY + ".");
        player.sendMessage("");
    }

    @Command(names = {"disableverify"}, permission = "op")
    public static void cancelVerify(CommandSender commandSender) {
        if (verify) {
            commandSender.sendMessage(ChatColor.RED + "Disabled verify");
            verify = false;
        } else {
            commandSender.sendMessage(ChatColor.GREEN + "Enabled verify");
            verify = true;
        }
    }

    @Command(names = {"freerank", "ironrank", "freeiron", "rankfree"}, permission = "")
    public static void freeRank(Player player) {
        if (Neutron.getInstance().getNetwork().equals(Neutron.Network.CRYPTO)) {
            player.sendMessage(SpigotConfig.unknownCommandMessage);
            return;
        }

        player.sendMessage("");
        player.sendMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Free Rank");
        player.sendMessage(ChatColor.GRAY + "Follow these easy steps to get yourself the Iron Rank for free!");
        player.sendMessage("");
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&4&l┃ &fVote for us on &chttps://cavepvp.org/vote1"));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&4&l┃ &fVote for us on &chttps://cavepvp.org/vote2"));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&4&l┃ &fType &c/verify &fand then enjoy your FREE &7&lIron Rank&f!"));
        player.sendMessage("");
        player.sendMessage(ChatColor.GREEN + "If you have any questions join ts.cavepvp.org");
    }
}
