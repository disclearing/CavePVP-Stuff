package net.frozenorb.foxtrot.persist.maps;

import net.frozenorb.foxtrot.persist.PersistMap;

import java.util.Date;
import java.util.UUID;

public class LastJoinMap extends PersistMap<Long> {

    public LastJoinMap() {
        super("LastJoin", "LastJoined");
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

    public void setLastJoin(UUID update) {
        updateValueAsync(update, System.currentTimeMillis());
    }

    public long getLastJoin(UUID check) {
        return (contains(check) ? getValue(check) : 0L);
    }

}