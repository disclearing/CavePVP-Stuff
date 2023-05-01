package org.cavepvp.profiles.leaderboard;

import lombok.Getter;
import lombok.Setter;
import org.cavepvp.profiles.Profiles;

import java.util.*;

public class LeaderboardHandler {
    private Profiles instance;

    @Getter @Setter private Map<UUID, Double> points = new HashMap<>();

    public LeaderboardHandler(Profiles profiles) {
        this.instance = profiles;

        new LeaderboardService(this.instance).runTaskTimerAsynchronously(this.instance, 20L, 20*60*5);
    }

    public LinkedHashMap<UUID, Double> getSortedMap() {
        final Map<UUID, Double> map = new HashMap<>(points);
        final LinkedList<Map.Entry<UUID, Double>> list = new LinkedList<>(map.entrySet());

        Collections.sort(list, (o1, o2) -> (o2.getValue().compareTo(o1.getValue())));

        final LinkedHashMap<UUID, Double> sortedHashMap = new LinkedHashMap<>();

        for (Map.Entry<UUID, Double> entry : list) {
            sortedHashMap.put(entry.getKey(), entry.getValue());
        }

        return (sortedHashMap);
    }

}
