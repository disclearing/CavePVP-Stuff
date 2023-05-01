package net.frozenorb.foxtrot.gameplay.events.cavenite;

import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Chest;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;

@AllArgsConstructor
public class GameTask extends BukkitRunnable {
    private Foxtrot instance;
    private CaveNiteHandler caveNiteHandler;

    @Override
    public void run() {
        if (caveNiteHandler.getGameState() != CaveNiteState.RUNNING) {
            return;
        }

        for (Location chestLocation : caveNiteHandler.getChestLocations()) {

            if (chestLocation.getBlock() == null || chestLocation.getBlock().getType() != Material.CHEST) {
                continue;
            }

            final Chest chest = (Chest) chestLocation.getBlock().getState();

            if (Arrays.stream(chest.getInventory().getContents()).noneMatch(it -> it != null && it.getType() != Material.AIR)) {
                continue;
            }

            chestLocation.getWorld().playEffect(chestLocation, Effect.ENDER_SIGNAL, 1);
            chestLocation.getWorld().playSound(chestLocation, Sound.PORTAL_TRAVEL, 0.03F, 1);
        }
    }
}
