package net.frozenorb.foxtrot.persist.maps;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.kitmap.gem.GemHandler;
import net.frozenorb.foxtrot.persist.PersistMap;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.entity.Player;

import java.util.UUID;

public class GemMap extends PersistMap<Long> {

    public GemMap() {
        super("PlayerGems", "gems");
    }

    public long addGems(UUID uuid, long amount, boolean bypass) {
        if (!bypass) {
            amount = GemHandler.isDoubleGem() ? amount * 2 : amount;
            amount = Foxtrot.getInstance().getGemBoosterMap().hasGemBooster(uuid) ? amount * 2 : amount;

            final Team team = Foxtrot.getInstance().getTeamHandler().getTeam(uuid);

            if (team != null && team.hasRoadOutpost()) {
                amount *= 2;
            }
        }

        Long value = getValue(uuid);
        if (value == null)
            value = amount;
        else
            value += amount;

        updateValueAsync(uuid, value);
        return amount;
    }

    public void addGemsSync(UUID uuid, long amount) {
        Long value = getValue(uuid);
        if (value == null)
            value = amount;
        else
            value += amount;

        updateValueSync(uuid, value);
    }

    public long addGems(UUID uuid, long amount) {
        return addGems(uuid, amount, false);
    }

    public boolean removeGems(UUID uuid, long amount) {
        Long value = getValue(uuid);
        if (value == null)
            return false;
        else
            value -= amount;

        boolean update = value >= 0;
        if (update)
            updateValueAsync(uuid, value);

        return update;
    }

    public long getGems(Player player) {
        return wrappedMap.getOrDefault(player.getUniqueId(), 0L);
    }

    public long getGems(UUID uuid) {
        return wrappedMap.getOrDefault(uuid, 0L);
    }

    public void setValue(Player player, long value) {
        updateValueSync(player.getUniqueId(), value);
    }

    public void setValue(UUID uuid, long value) {
        updateValueAsync(uuid, value);
    }

    @Override
    public String getRedisValue(Long gems) {
        return String.valueOf(gems);
    }

    @Override
    public Object getMongoValue(Long gems) {
        return gems.intValue();
    }

    @Override
    public Long getJavaObject(String str) {
        return Long.parseLong(str);
    }
}