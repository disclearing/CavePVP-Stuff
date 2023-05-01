package net.frozenorb.foxtrot.server.deathban;

import com.mongodb.BasicDBObject;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public final class Deathban {

    private static Map<String, Integer> deathban = new LinkedHashMap<>();
    private static int defaultMinutes = 120;

    static {
        deathban.put("VIP", 120);
        deathban.put("PRO", 90);
        deathban.put("EPIC", 45);
        deathban.put("HIGHROLLER", 45);
    }

    public static void load(BasicDBObject object) {
        deathban.clear();

        for (String key : object.keySet()) {
            if (key.equals("DEFAULT"))  {
                defaultMinutes = object.getInt(key);
            } else {
                deathban.put(key, object.getInt(key));
            }
        }
    }

    public static int getDeathbanSeconds(Player player) {
        int minutes = defaultMinutes;

        for (Map.Entry<String, Integer> entry : deathban.entrySet()) {
            if (player.hasPermission("ranks." + entry.getKey().toLowerCase()) && entry.getValue() < minutes) {
                minutes = entry.getValue();
            }
        }

        return (int) TimeUnit.MINUTES.toSeconds(minutes);
    }

}
