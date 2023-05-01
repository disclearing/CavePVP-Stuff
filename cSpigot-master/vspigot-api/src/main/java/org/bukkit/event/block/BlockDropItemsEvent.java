package org.bukkit.event.block;

import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;

public class BlockDropItemsEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private Block block;
    private Player player;
    private List<Item> toDrop;
    private boolean cancelled = false;

    public BlockDropItemsEvent(Block block, Player player, List<Item> toDrop) {
        this.block = block;
        this.player = player;
        this.toDrop = toDrop;
    }

    public Block getBlock() {
        return this.block;
    }

    public Player getPlayer() {
        return this.player;
    }

    public List<Item> getToDrop() {
        return this.toDrop;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}