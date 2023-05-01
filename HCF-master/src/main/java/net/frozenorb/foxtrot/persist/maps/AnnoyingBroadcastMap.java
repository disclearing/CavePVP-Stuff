package net.frozenorb.foxtrot.persist.maps;

import net.frozenorb.foxtrot.persist.PersistMap;

import java.util.UUID;

public class AnnoyingBroadcastMap extends PersistMap<Boolean> {

    public AnnoyingBroadcastMap() {
        super("AnnoyingBroadcast", "AnnoyingBroadcast");
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

    public void setAnnoyingBroadcast(UUID update,boolean toggled) {
        updateValueAsync(update, toggled);
    }

    public boolean isAnnoyingBroadcast(UUID check) {
        return (contains(check) ? getValue(check) : true);
    }

}

