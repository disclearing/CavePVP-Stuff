package net.frozenorb.foxtrot.persist.maps.toggle;

import net.frozenorb.foxtrot.persist.PersistMap;

import java.util.UUID;

public class DTRDisplayMap extends PersistMap<Boolean> {

    public DTRDisplayMap() {
        super("DTRDisplay", "DTRDisplay");
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

    public void setHearts(UUID update, boolean toggled) {
        updateValueAsync(update, toggled);
    }

    public boolean isHearts(UUID check) {
        return (contains(check) ? getValue(check) : false);
    }

}
