package net.frozenorb.foxtrot.persist.maps;

import java.util.UUID;

import net.frozenorb.foxtrot.persist.PersistMap;

public class FriendLivesMap extends PersistMap<Integer> {

    public FriendLivesMap() {
        super("FriendLives", "Lives.Friend");
    }

    @Override
    public String getRedisValue(Integer lives) {
        return (String.valueOf(lives));
    }

    @Override
    public Integer getJavaObject(String str) {
        return (Integer.parseInt(str));
    }

    @Override
    public Object getMongoValue(Integer lives) {
        return (lives);
    }

    public int getLives(UUID check) {
        return (contains(check) ? getValue(check) : 0);
    }

    public void setLives(UUID update, int lives) {
        updateValueSync(update, lives);
    }

}