package net.frozenorb.foxtrot.persist.maps;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.persist.PersistMap;

import java.util.UUID;

public class KillstreakMap extends PersistMap<Integer> {

    public KillstreakMap() {
        super("Killstreak", "Killstreak");
    }

    @Override
    public String getRedisValue(Integer kills) {
        return (String.valueOf(kills));
    }

    @Override
    public Integer getJavaObject(String str) {
        return (Integer.parseInt(str));
    }

    @Override
    public Object getMongoValue(Integer kills) {
        return (kills);
    }

    public int getKillstreak(UUID check) {
        return (contains(check) ? getValue(check) : 0);
    }

    public void setKillstreak(UUID update, int kills) {
        updateValueAsync(update, kills);
        Foxtrot.getInstance().getKdrMap().updateKDR(update);
    }

}