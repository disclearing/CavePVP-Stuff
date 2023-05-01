package org.bukkit.event.inventory;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

@Getter @Setter @AllArgsConstructor
public class EquipmentSetEvent extends Event {
    
    private static final HandlerList handlers = new HandlerList();
    
    private final HumanEntity humanEntity;
    private final int slot;
    private final ItemStack previousItem;
    private final ItemStack newItem;
    
    
    public static HandlerList getHandlerList() {
        return handlers;
    }
    
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
