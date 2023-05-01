package net.frozenorb.foxtrot.listener;

import cc.fyre.piston.Piston;
import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitTask;
import org.cavepvp.profiles.Profiles;
import org.cavepvp.profiles.playerProfiles.PlayerProfile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ReputationListener implements Listener {
    public static List<BukkitTask> tasks = new ArrayList<>();
    public static boolean ggMode = false;
    public static List<UUID> hasSaid = new ArrayList<>();
    public static List<String> currentTargets = new ArrayList<>();

    @Command(names = {"purchase_reputation"}, permission = "op")
    public static void execute(CommandSender sender, @Parameter(name = "target")String target) {
        sender.sendMessage("Dispatching Reputation");

        if (currentTargets.contains(target)) {
            sender.sendMessage("Target already is in cache");
            return;
        }

        for (Player onlinePlayer : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
            onlinePlayer.sendMessage("");
            onlinePlayer.sendMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Reputation");
            onlinePlayer.sendMessage(target + ChatColor.translate(" &7has just made a purchase!"));
            onlinePlayer.sendMessage(ChatColor.translate("&cType &f'gg' &cin chat to gain reputation! You have 5 seconds!"));
            onlinePlayer.sendMessage("");
        }

        final List<BukkitTask> clonedList = new ArrayList<>(tasks);

        clonedList.forEach(it -> {
            it.cancel();
            currentTargets.clear();
            tasks.remove(it);
        });

        tasks.add(Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
            ggMode = false;
            currentTargets.clear();
        }, 20*10));

        Piston.getInstance().getChatHandler().getSlowCache().clear();
        currentTargets.add(target);

        ggMode = true;
        hasSaid.clear();
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onChat(AsyncPlayerChatEvent event) {
        final Player player = event.getPlayer();

        if (!event.getMessage().toLowerCase().contains("gg")) {
            return;
        }

        if (!ggMode || Piston.getInstance().getChatHandler().isMuted()) {
            return;
        }

        if (hasSaid.contains(player.getUniqueId())) {
            return;
        }

        player.sendMessage(ChatColor.translate("&aYou have gained &f0.05 reputation &afor saying GG in chat when a player purchased an item! Thank you!"));

        Profiles.getInstance().getReputationHandler().addReputation(player.getUniqueId(), player.getName(), 0.05);

        hasSaid.add(player.getUniqueId());
    }

}
