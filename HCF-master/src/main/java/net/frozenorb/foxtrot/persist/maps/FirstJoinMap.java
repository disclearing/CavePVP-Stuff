package net.frozenorb.foxtrot.persist.maps;

import net.frozenorb.foxtrot.persist.PersistMap;

import java.util.Collection;
import java.util.Date;
import java.util.UUID;

public class FirstJoinMap extends PersistMap<Long> {

    public FirstJoinMap() {
        super("FirstJoin", "FirstJoined");
    }

    @Override
    public String getRedisValue(Long time) {
        return (String.valueOf(time));
    }

    @Override
    public Long getJavaObject(String str) {
        return (Long.parseLong(str));
    }

    @Override
    public Object getMongoValue(Long time) {
        return (new Date(time));
    }

    public void setFirstJoin(UUID update) {
        updateValueAsync(update, System.currentTimeMillis());
    }

    public long getFirstJoin(UUID check) {
        return (contains(check) ? getValue(check) : 0L);
    }

    public int getAllPlayersSize() {
        return (wrappedMap.size());
    }

    public Collection<UUID> getAllPlayers() {
        return wrappedMap.keySet();
    }

}