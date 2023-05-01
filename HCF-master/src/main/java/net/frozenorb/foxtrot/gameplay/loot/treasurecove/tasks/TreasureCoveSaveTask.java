package net.frozenorb.foxtrot.gameplay.loot.treasurecove.tasks;

import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.scheduler.BukkitRunnable;

public class TreasureCoveSaveTask extends BukkitRunnable {

    public void run() {
        Foxtrot.getInstance().getTreasureCoveHandler().saveTreasureInfo();
    }

}