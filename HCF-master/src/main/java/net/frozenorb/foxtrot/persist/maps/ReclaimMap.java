package net.frozenorb.foxtrot.persist.maps;

import net.frozenorb.foxtrot.persist.PersistMap;

import java.util.UUID;

public class ReclaimMap extends PersistMap<Boolean> {

    public ReclaimMap() {
        super("Reclaim", "Reclaim");
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

    public void setReclaimed(UUID update,boolean toggled) {
        updateValueAsync(update, toggled);
    }

    public boolean hasReclaimed(UUID check) {
        return (contains(check) ? getValue(check) : false);
    }
}
