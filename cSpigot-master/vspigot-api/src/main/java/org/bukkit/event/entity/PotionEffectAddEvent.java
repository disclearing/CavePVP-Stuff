package org.bukkit.event.entity;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.potion.PotionEffect;

/**
 * Called when a potion effect is applied to an entity, or an existing effect is extended or upgraded
 */
public class PotionEffectAddEvent extends PotionEffectEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private boolean cancelled;
    protected final EffectCause effectCause;

    public PotionEffectAddEvent(LivingEntity entity, PotionEffect effect, EffectCause effectCause) {
        super(entity, effect);
        this.effectCause = effectCause;
    }

    public EffectCause getCause() {
        return effectCause;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    /**
     * The cause of receiving a potion effect
     */
    public enum EffectCause {

        /**
         * Indicates the effect was caused by the proximity of a potion effect splash
         */
        POTION_SPLASH,
        /**
         * Indicates the effect was caused by the proximity of a beacon
         */
        BEACON,
        /**
         * Indicates the effect was caused by damage from a wither skeleton
         */
        WITHER_SKELETON,
        /**
         * Indicates the effect was caused by damage from a wither skull
         */
        WITHER_SKULL,
        /**
         * Indicates the effect was caused by a plugin
         */
        PLUGIN,
        /**
         * Indicates the effect was caused by an event not covered by
         * this enum
         */
        UNKNOWN
    }
}