package org.cavepvp.profiles.leaderboard;

import cc.fyre.universe.UniverseAPI;
import lombok.AllArgsConstructor;
import org.bson.Document;
import org.bukkit.scheduler.BukkitRunnable;
import org.cavepvp.profiles.Profiles;

import java.util.UUID;

@AllArgsConstructor
public class LeaderboardService extends BukkitRunnable {
    private Profiles instance;

    @Override
    public void run() {
        if (UniverseAPI.getServerName().contains("AU")) {
            return;
        }

        this.instance.getLeaderboardHandler().getPoints().clear();

        for (Document document : this.instance.getPlayerProfileHandler().getCollection().find()) {
            if (!document.containsKey("playerReputation")) {
                continue;
            }

            double reputation = document.getDouble("playerReputation");

            if (reputation == 0) {
                continue;
            }

            final UUID uuid = UUID.fromString(document.getString("_id"));

            this.instance.getLeaderboardHandler().getPoints().put(uuid, reputation);
        }
    }
}
