package net.frozenorb.foxtrot.persist.maps;

import net.frozenorb.foxtrot.persist.PersistMap;

import java.util.UUID;

public class FishingKitMap extends PersistMap<Integer> {

    public FishingKitMap() {
        super("FishingKitUses", "FishingKitUses");
    }

    @Override
    public String getRedisValue(Integer uses) {
        return (String.valueOf(uses));
    }

    @Override
    public Integer getJavaObject(String str) {
        return (Integer.parseInt(str));
    }

    @Override
    public Object getMongoValue(Integer uses) {
        return (uses);
    }

    public int getUses(UUID check) {
        return (contains(check) ? getValue(check) : 0);
    }

    public void setUses(UUID update, int uses) {
        updateValueAsync(update, uses);
    }

}