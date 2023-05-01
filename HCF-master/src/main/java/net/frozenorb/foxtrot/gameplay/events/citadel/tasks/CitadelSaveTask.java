package net.frozenorb.foxtrot.gameplay.events.citadel.tasks;

import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.scheduler.BukkitRunnable;

public class CitadelSaveTask extends BukkitRunnable {

    public void run() {
        Foxtrot.getInstance().getCitadelHandler().saveCitadelInfo();
    }

}