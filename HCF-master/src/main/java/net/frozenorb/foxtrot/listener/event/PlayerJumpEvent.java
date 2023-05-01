package net.frozenorb.foxtrot.listener.event;

import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerStatisticIncrementEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class PlayerJumpEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private static final Listener listener = new PlayerJumpEventListener();
    private boolean isCancelled = false;

    static {
        Bukkit.getServer().getPluginManager().registerEvents(listener, Foxtrot.getInstance());
    }

    public PlayerJumpEvent(Player player) {
        super(player);
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        isCancelled = cancel;
        if (cancel) {
            player.setVelocity(new Vector());
        }
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    private static class PlayerJumpEventListener implements Listener {
        @EventHandler
        public void onPlayerMove(PlayerMoveEvent event) {
            Player player = event.getPlayer();
            Vector velocity = player.getVelocity();
            // Check if the player is moving "up"
            if (velocity.getY() > 0)
            {
                // Default jump velocity
                double jumpVelocity = 0.42F; // Default jump velocity
                PotionEffect jumpPotion = player.getActivePotionEffects().stream().filter(it -> it.getType() == PotionEffectType.JUMP).findFirst().orElse(null);
                if (jumpPotion != null) {
                    // If player has jump potion add it to jump velocity
                    jumpVelocity += (double) ((float) jumpPotion.getAmplifier() + 1) * 0.1F;
                }
                // Check if player is not on ladder and if jump velocity calculated is equals to player Y velocity
                if (player.getLocation().getBlock().getType() != Material.LADDER && Double.compare(velocity.getY(), jumpVelocity) == 0) {
                    Bukkit.getServer().getPluginManager().callEvent(new PlayerJumpEvent(event.getPlayer()));
                }
            }
        }
    }
}
