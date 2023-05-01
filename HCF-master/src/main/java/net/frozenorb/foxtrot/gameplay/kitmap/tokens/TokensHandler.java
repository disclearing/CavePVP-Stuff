package net.frozenorb.foxtrot.gameplay.kitmap.tokens;

import net.minecraft.util.com.google.common.collect.Maps;
import com.mongodb.client.model.UpdateOptions;
import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class TokensHandler implements Listener {

    private static UpdateOptions UPSERT = new UpdateOptions().upsert(true);
    @Getter
    private Map<UUID, Long> pendingTokens = Maps.newConcurrentMap();
    private int lastDateAsInt;
    
    public TokensHandler() {

        Bukkit.getScheduler().runTaskTimerAsynchronously(Foxtrot.getInstance(), () -> {
            List<UUID> toRemove = new ArrayList<>();
            List<UUID> toUpdate = new ArrayList<>();

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!(pendingTokens.containsKey(player.getUniqueId()))) {
                    pendingTokens.put(player.getUniqueId(), System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1));
                }
            }

            for (UUID uuid : pendingTokens.keySet()) {
                long time = pendingTokens.get(uuid);

                if (Bukkit.getPlayer(uuid) == null) {
                    toRemove.add(uuid);
                    continue;
                }

                if (System.currentTimeMillis() >= time) {
                    toUpdate.add(uuid);
                }
            }

            for (UUID uuid : toRemove) {
                pendingTokens.remove(uuid);
            }

            for (UUID uuid : toUpdate) {
                Player player = Bukkit.getPlayer(uuid);

                if (player != null) {
                    player.sendMessage(ChatColor.YELLOW + "You've received " + ChatColor.LIGHT_PURPLE + "1 token" + ChatColor.YELLOW + " for actively playing!");
                    player.sendMessage(ChatColor.YELLOW + "Trade your tokens at " + ChatColor.GOLD + "spawn" + ChatColor.YELLOW + " to receive a crate key!");
                    Foxtrot.getInstance().getKitmapTokensMap().setTokens(uuid, Foxtrot.getInstance().getKitmapTokensMap().getTokens(uuid)+1);
                    pendingTokens.put(uuid, System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1));
                }
            }
        },0, 20L);

        Bukkit.getPluginManager().registerEvents(this, Foxtrot.getInstance());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onDamage(EntityDamageEvent event) {
       if (event.getEntity() instanceof Villager) {
           Villager villager = (Villager) event.getEntity();

           if (villager.getCustomName() != null && villager.getCustomName().contains(ChatColor.COLOR_CHAR + "") && villager.getCustomName().contains("Token")) {
               event.setCancelled(true);
           }

       }
    }

    @EventHandler
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof Villager) {
            Villager villager = (Villager) event.getRightClicked();

            if (villager.getCustomName() != null && villager.getCustomName().contains(ChatColor.COLOR_CHAR + "") && villager.getCustomName().contains("Token")) {
                event.setCancelled(true);
                int tokens = Foxtrot.getInstance().getKitmapTokensMap().getTokens(event.getPlayer().getUniqueId());

                if (tokens <= 0) {
                    event.getPlayer().sendMessage(ChatColor.RED + "You don't have any tokens to claim.");
                    return;
                }

                if (tokens < 3) {
                    event.getPlayer().sendMessage(ChatColor.RED + "You need at least 3 tokens to exchange them for a key.");
                    return;
                }

                int keys = 0;
                while (tokens >= 3) {
                    tokens -=3;
                    keys++;
                }

                Foxtrot.getInstance().getKitmapTokensMap().setTokens(event.getPlayer().getUniqueId(), Foxtrot.getInstance().getKitmapTokensMap().getTokens(event.getPlayer().getUniqueId()) % 3);
                event.getPlayer().sendMessage(ChatColor.GREEN + "You've exchanged your tokens for " + ChatColor.DARK_GREEN + keys + ChatColor.GREEN + " crate keys.");
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "cr givekey " + event.getPlayer().getName() + " token " + keys);
            }
        }
    }

}
