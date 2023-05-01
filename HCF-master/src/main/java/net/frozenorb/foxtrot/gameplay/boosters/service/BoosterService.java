package net.frozenorb.foxtrot.gameplay.boosters.service;

import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.boosters.Booster;
import net.frozenorb.foxtrot.gameplay.boosters.NetworkBoosterHandler;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.TimeUnit;

@AllArgsConstructor
public class BoosterService extends BukkitRunnable {
    private Foxtrot instance;
    private NetworkBoosterHandler networkBoosterHandler;

    @Override
    public void run() {
        for (Map.Entry<Booster, UUID> entry : networkBoosterHandler.getActiveBoosters().entrySet()) {
            if (entry.getKey().getActivatedAt()+ TimeUnit.HOURS.toMillis(1L) <= System.currentTimeMillis()) {
                entry.getKey().deactivate();
            }
        }

        final Map<UUID, List<Booster>> queuedBoosters = new HashMap<>(networkBoosterHandler.getBoostersQueued());

        for (Map.Entry<UUID, List<Booster>> entry : queuedBoosters.entrySet()) {
            final List<Booster> boosters = new ArrayList<>(entry.getValue());

            for (Booster booster : boosters) {
                if (booster.isActive()) {
                    continue;
                }

                if (booster.getLastDeactivateAt()+TimeUnit.HOURS.toMillis(1) >= System.currentTimeMillis()) {
                    continue;
                }

                booster.activate(entry.getKey());

                final List<Booster> secondBoosters = new ArrayList<>(entry.getValue());
                secondBoosters.remove(booster);

                if (secondBoosters.isEmpty()) {
                    networkBoosterHandler.getBoostersQueued().remove(entry.getKey());
                } else {
                    networkBoosterHandler.getBoostersQueued().put(entry.getKey(), secondBoosters);
                }
            }
        }
    }
}
