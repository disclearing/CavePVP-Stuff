package net.frozenorb.foxtrot.gameplay.cavesays.type;

import net.frozenorb.foxtrot.gameplay.cavesays.Task;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.enchantment.EnchantItemEvent;

public class EnchantItem extends Task {
    @Override
    public String getTaskDisplayName() {
        return "Enchant an Item";
    }

    @Override
    public String getTaskID() {
        return "EnchantItem";
    }

    @Override
    public int getPointsToWin() {
        return 1;
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onEnchant(EnchantItemEvent event) {
        if (event.isCancelled()) {
            return;
        }

        this.addProgress(event.getEnchanter());
    }
}