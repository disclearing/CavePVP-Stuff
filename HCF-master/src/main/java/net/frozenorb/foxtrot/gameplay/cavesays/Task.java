package net.frozenorb.foxtrot.gameplay.cavesays;

import lombok.Getter;
import lombok.Setter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.extra.stats.StatsEntry;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public abstract class Task implements Listener {
    
    @Getter private final Map<UUID, Integer> points = new HashMap<>();
    
    public abstract String getTaskID();
    public abstract String getTaskDisplayName();
    public abstract int getPointsToWin();

    @Getter @Setter private int airdrops;

    public void addProgress(Player player) {
        if (player.getGameMode() == GameMode.CREATIVE) {
            return;
        }

        this.points.put(player.getUniqueId(), points.getOrDefault(player.getUniqueId(), 0)+1);

        if (this.points.get(player.getUniqueId()) >= this.getPointsToWin()) {
            deactivate(player);
            return;
        }

        player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1, 1);
        player.sendMessage(ChatColor.translate("&6You have obtained a point towards this task! (&f" + this.points.get(player.getUniqueId()) + "/" + getPointsToWin() + "&6)"));
    }

    public void activate() {
        final Foxtrot instance = Foxtrot.getInstance();

        this.airdrops = ThreadLocalRandom.current().nextInt(0, 100) <= 50 ? 2 : 3;

        instance.getServer().getPluginManager().registerEvents(this, instance);

        for (Player onlinePlayer : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
            onlinePlayer.sendMessage("");
            onlinePlayer.sendMessage(ChatColor.translate("&7███████"));
            onlinePlayer.sendMessage(ChatColor.translate("&7█" + "&4█████" + "&7█ &4&lCave Says"));
            onlinePlayer.sendMessage(ChatColor.translate("&7█" + "&4█" + "&7█████ &7First person to"));
            onlinePlayer.sendMessage(ChatColor.translate("&7█" + "&4█" + "&7█████ &f" + this.getTaskDisplayName()));
            onlinePlayer.sendMessage(ChatColor.translate("&7█" + "&4█" + "&7█████ &cwill receive"));
            onlinePlayer.sendMessage(ChatColor.translate("&7█" + "&4█████" + "&7█ &b&l" + airdrops + " Airdrops"));
            onlinePlayer.sendMessage(ChatColor.translate("&7███████"));
            onlinePlayer.sendMessage("");
        }
    }

    public void deactivate(Player winner) {
        final Foxtrot instance = Foxtrot.getInstance();
        final CaveSaysHandler caveSaysHandler = instance.getCaveSaysHandler();

        this.points.clear();
        caveSaysHandler.setActiveTask(null);
        HandlerList.unregisterAll(this);

        if (winner == null) {
            instance.getServer().broadcastMessage("");
            instance.getServer().broadcastMessage(ChatColor.translate("&4&lCave Says"));
            instance.getServer().broadcastMessage(ChatColor.translate(getTaskDisplayName() + " &7has been forcefully cancelled."));
            instance.getServer().broadcastMessage("");
            return;
        }

        winner.playSound(winner.getLocation(), Sound.LEVEL_UP, 1, 1);

        instance.getServer().dispatchCommand(instance.getServer().getConsoleSender(), "airdrops give " + winner.getName() + " " + this.airdrops);

        instance.getServer().broadcastMessage("");
        instance.getServer().broadcastMessage(ChatColor.translate("&4&lCave Says"));
        instance.getServer().broadcastMessage("");
        instance.getServer().broadcastMessage(ChatColor.translate(winner.getDisplayName() + " &7has completed the task first and received &b&l" + this.airdrops + "x Airdrops&7."));
        instance.getServer().broadcastMessage("");

        final StatsEntry statsEntry = Foxtrot.getInstance().getMapHandler().getStatsHandler().getStats(winner.getUniqueId());

        if (statsEntry == null) {
            return;
        }

        statsEntry.addCaveSaysCompleted();
    }
}
