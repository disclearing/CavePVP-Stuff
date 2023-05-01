package net.frozenorb.foxtrot.persist.maps.toggle;

import net.frozenorb.foxtrot.persist.PersistMap;

import java.util.UUID;

public class MobDropsPickupMap extends PersistMap<Boolean> {

    public MobDropsPickupMap() {
        super("MobPickup", "MobPickup");
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

    public void setMobPickup(UUID update, boolean toggled) {
        updateValueAsync(update, toggled);
    }

    public boolean isMobPickup(UUID check) {
        return (contains(check) ? getValue(check) : true);
    }

}
