package net.frozenorb.foxtrot.persist.maps;

import net.frozenorb.foxtrot.persist.PersistMap;
import org.bukkit.entity.Player;

import java.util.UUID;

public class GemBoosterMap extends PersistMap<Long> {

    public GemBoosterMap() {
        super("GemBooster", "GemBooster");
    }

    @Override
    public String getRedisValue(Long value) {
        return String.valueOf(value);
    }

    @Override
    public Object getMongoValue(Long value) {
        return Long.toString(value);
    }

    @Override
    public Long getJavaObject(String value) {
        return Long.valueOf(value);
    }

    public boolean hasGemBooster(UUID uuid) {
        Long time = getValue(uuid);
        if (time == null) return false;
        return time >= System.currentTimeMillis();
    }

    public long getGemBoosterMillisLeft(Player player) {
        Long time = getValue(player.getUniqueId());
        if (time == null) return 0;

        long left = time - System.currentTimeMillis();
        return left < 0 ? 0 : left;
    }

    public void giveGemBooster(Player player, long minutes) {
        long millis = minutes * 1000 * 60;
        Long time = getValue(player.getUniqueId());

        if (time == null || time < System.currentTimeMillis()) {
            time = System.currentTimeMillis() + millis;
        } else {
            time = time + millis;
        }

        updateValueAsync(player.getUniqueId(), time);
    }

    public void giveGemBooster(UUID player, long minutes) {
        long millis = minutes * 1000 * 60;
        Long time = getValue(player);

        if (time == null || time < System.currentTimeMillis()) {
            time = System.currentTimeMillis() + millis;
        } else {
            time = time + millis;
        }

        updateValueAsync(player, time);
    }
}
