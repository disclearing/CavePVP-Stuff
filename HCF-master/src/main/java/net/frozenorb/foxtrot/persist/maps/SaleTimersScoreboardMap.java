package net.frozenorb.foxtrot.persist.maps;

import net.frozenorb.foxtrot.persist.PersistMap;

import java.util.UUID;

public class SaleTimersScoreboardMap extends PersistMap<Boolean> {

    public SaleTimersScoreboardMap() {
        super("SaleTimers", "SaleTimers");
    }

    @Override
    public String getRedisValue(Boolean toggled) {
        return String.valueOf(toggled);
    }

    @Override
    public Object getMongoValue(Boolean toggled) {
        return String.valueOf(toggled);
    }

    @Override
    public Boolean getJavaObject(String str) {
        return Boolean.valueOf(str);
    }

    public void setSaleTimers(UUID update,boolean toggled) {
        updateValueAsync(update, toggled);
    }

    public boolean isSaleTimers(UUID check) {
        return (contains(check) ? getValue(check) : true);
    }

}

