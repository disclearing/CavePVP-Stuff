package net.frozenorb.foxtrot.persist.maps;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.persist.PersistMap;

import java.util.UUID;

public class DeathsMap extends PersistMap<Integer> {

    public DeathsMap() {
        super("Deaths", "Deaths");
    }

    @Override
    public String getRedisValue(Integer deaths) {
        return (String.valueOf(deaths));
    }

    @Override
    public Integer getJavaObject(String str) {
        return (Integer.parseInt(str));
    }

    @Override
    public Object getMongoValue(Integer deaths) {
        return (deaths);
    }

    public int getDeaths(UUID check) {
        return (contains(check) ? getValue(check) : 0);
    }

    public void setDeaths(UUID update, int kills) {
        updateValueAsync(update, kills);
        Foxtrot.getInstance().getKdrMap().updateKDR(update);
    }

}
