package net.frozenorb.foxtrot.server.deathban;

import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.listener.LunarClientListener;
import net.frozenorb.foxtrot.server.deathban.listener.DeathbanArenaListener;
import net.frozenorb.foxtrot.server.deathban.listener.DeathbanListener;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.util.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.cavepvp.suge.Suge;
import org.cavepvp.suge.kit.data.Kit;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class DeathbanArenaHandler {
    private Foxtrot instance;

    @Getter private Map<UUID, Integer> cache = new HashMap<>();
    @Getter private Map<UUID, Long> lifeCooldown = new HashMap<>();

    public DeathbanArenaHandler(Foxtrot instance) {
        this.instance = instance;

        if (!this.instance.getMapHandler().isKitMap()) {
            this.instance.getServer().getPluginManager().registerEvents(new DeathbanArenaListener(this.instance), this.instance);
            this.instance.getServer().getPluginManager().registerEvents(new DeathbanListener(this.instance), this.instance);
        }
    }

    public boolean isDeathbanArena(Player player) {
        if (this.instance.getMapHandler().isKitMap()) {
            return false;
        }

        if (player.isOp()) {
            return false;
        }

        if (this.instance.getDeathbanMap().isDeathbanned(player.getUniqueId())) {
            return true;
        }

        return player.getWorld().getName().equalsIgnoreCase("Deathban");
    }

    public void deathban(Player player) {
        if (this.instance.getMapHandler().isKitMap()) {
            return;
        }

        if (player.getWorld().getEnvironment() == World.Environment.THE_END || DTRBitmask.CITADEL.appliesAt(player.getLocation()) || DTRBitmask.KOTH.appliesAt(player.getLocation())) {
            this.lifeCooldown.put(player.getUniqueId(), System.currentTimeMillis()+TimeUnit.MINUTES.toMillis(3));
        } else if (player.getWorld().getEnvironment() == World.Environment.NETHER) {
            this.lifeCooldown.put(player.getUniqueId(), System.currentTimeMillis()+TimeUnit.MINUTES.toMillis(5));
        }

        player.removeMetadata("PVP_TIMER_BYPASS", Foxtrot.getInstance());
        Foxtrot.getInstance().getPvPTimerMap().removeTimer(player.getUniqueId());
        LunarClientListener.updateNametag(player);

        final World world = this.instance.getServer().getWorld("Deathban");

        player.teleport(world.getSpawnLocation().clone());

        player.sendMessage("");
        player.sendMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Deathban Arena");
        player.sendMessage(ChatColor.GRAY + "You are now entering the deathban arena!");
        player.sendMessage(ChatColor.GRAY + "PvP to kill time while waiting out your deathban!");
        player.sendMessage(ChatColor.GRAY + "Once you reach 5 kills you will be automatically revived.");
        player.sendMessage("");
        player.sendMessage(ChatColor.GREEN + "Click the sign to revive yourself using lives.");
        player.sendMessage("");

        this.cache.remove(player.getUniqueId());
    }

    public void revive(UUID uuid) {
        if (this.instance.getMapHandler().isKitMap()) {
            return;
        }

        this.instance.getDeathbanMap().revive(uuid);

        this.lifeCooldown.remove(uuid);
        this.cache.remove(uuid);

        final Player player = this.instance.getServer().getPlayer(uuid);

        if (player == null) {
            return;
        }

        InventoryUtils.resetInventoryNow(player);

        player.sendMessage(ChatColor.GREEN + "You have been revived!");

        this.instance.getServer().getScheduler().runTask(this.instance, () -> player.teleport(this.instance.getServer().getWorld("world").getSpawnLocation()));

        Bukkit.getScheduler().scheduleSyncDelayedTask(Foxtrot.getInstance(), () -> {
            player.setMetadata("PVP_TIMER_BYPASS", new FixedMetadataValue(Foxtrot.getInstance(), true));
            Foxtrot.getInstance().getPvPTimerMap().createTimer(player.getUniqueId(), 30 * 60);//moved inside here due to occasional CME maybe this will fix?
        }, 20L);
    }
}
