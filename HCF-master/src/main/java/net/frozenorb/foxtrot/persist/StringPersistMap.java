package net.frozenorb.foxtrot.persist;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import net.frozenorb.foxtrot.Foxtrot;
import cc.fyre.proton.Proton;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class StringPersistMap<T> {

    protected Map<String, T> wrappedMap = new ConcurrentHashMap<>();

    private String keyPrefix;
    private String mongoName;

    public StringPersistMap(String keyPrefix, String mongoName) {
        this.keyPrefix = keyPrefix;
        this.mongoName = mongoName;

        loadFromRedis();
    }

    public void loadFromRedis() {
        Proton.getInstance().runRedisCommand(redis -> {
            Map<String, String> results = redis.hgetAll(keyPrefix);

            for (Map.Entry<String, String> resultEntry : results.entrySet()) {
                T object = getJavaObjectSafe(resultEntry.getKey(), resultEntry.getValue());

                if (object != null) {
                    wrappedMap.put(resultEntry.getKey(), object);
                }
            }

            return (null);
        });
    }

    protected void wipeValues() {
        wrappedMap.clear();

        Proton.getInstance().runRedisCommand(redis -> {
            redis.del(keyPrefix);
            return (null);
        });
    }

    protected void updateValueSync(final String key, final T value) {
        wrappedMap.put(key, value);

        Proton.getInstance().runRedisCommand(redis -> {
            redis.hset(keyPrefix, key, getRedisValue(getValue(key)));

            DBCollection playersCollection = Foxtrot.getInstance().getMongoPool().getDB(Foxtrot.MONGO_DB_NAME).getCollection("Players");
            BasicDBObject player = new BasicDBObject("_id", key.replace("-", ""));

            playersCollection.update(player, new BasicDBObject("$set", new BasicDBObject(mongoName, getMongoValue(getValue(key)))), true, false);
            return (null);
        });
    }

    protected void updateValueAsync(final String key, T value) {
        wrappedMap.put(key, value);

        new BukkitRunnable() {

            public void run() {
                Proton.getInstance().runRedisCommand(redis -> {
                    redis.hset(keyPrefix, key, getRedisValue(getValue(key)));

                    DBCollection playersCollection = Foxtrot.getInstance().getMongoPool().getDB(Foxtrot.MONGO_DB_NAME).getCollection("Players");
                    BasicDBObject player = new BasicDBObject("_id", key.replace("-", ""));

                    playersCollection.update(player, new BasicDBObject("$set", new BasicDBObject(mongoName, getMongoValue(getValue(key)))), true, false);
                    return (null);
                });
            }

        }.runTaskAsynchronously(Foxtrot.getInstance());
    }

    protected T getValue(String key) {
        return (wrappedMap.get(key));
    }

    protected boolean contains(String key) {
        return (wrappedMap.containsKey(key));
    }

    public abstract String getRedisValue(T t);

    public abstract Object getMongoValue(T t);

    public T getJavaObjectSafe(String key, String redisValue) {
        try {
            return (getJavaObject(redisValue));
        } catch (Exception e) {
            System.out.println("Error parsing Redis result.");
            System.out.println(" - Prefix: " + keyPrefix);
            System.out.println(" - Key: " + key);
            System.out.println(" - Value: " + redisValue);
            return (null);
        }
    }

    public abstract T getJavaObject(String str);

}