package net.frozenorb.foxtrot.gameplay.cavesays.type;

import net.frozenorb.foxtrot.gameplay.cavesays.Task;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class SpawnACow extends Task {
    @Override
    public String getTaskDisplayName() {
        return "Spawn a Cow";
    }

    @Override
    public String getTaskID() {
        return "Blaze";
    }

    @Override
    public int getPointsToWin() {
        return 1;
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onSpawn(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        final Player player = event.getPlayer();

        final ItemStack itemStack = event.getItem();

        if (itemStack == null || itemStack.getType() != Material.MONSTER_EGG) {
            return;
        }

        this.addProgress(player);
    }
}
