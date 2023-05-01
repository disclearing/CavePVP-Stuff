package net.frozenorb.foxtrot.gameplay.blockshop.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

@FunctionalInterface
public interface ClickHandler {
    void onClick(Player player, int i, ClickType clickType);
}
