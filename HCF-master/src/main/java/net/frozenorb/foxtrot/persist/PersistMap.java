package net.frozenorb.foxtrot.persist;

import cc.fyre.proton.Proton;
import cc.fyre.proton.redis.RedisCommand;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import cc.fyre.proton.util.UUIDUtils;

import org.bukkit.scheduler.BukkitRunnable;
import redis.clients.jedis.Jedis;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public abstract class PersistMap<T> {
    
    @Getter protected Map<UUID, T> wrappedMap = new ConcurrentHashMap<>();
    
    private String keyPrefix;
    private String mongoName;
    private boolean useMongo;

    public PersistMap(String keyPrefix, String mongoName) {
        this(keyPrefix, mongoName, true); // use mongo by default
    }
    
    public PersistMap(String keyPrefix, String mongoName, boolean useMongo) {
        this.keyPrefix = keyPrefix;
        this.mongoName = mongoName;
        this.useMongo = useMongo;
        
        loadFromRedis();
    }
    
    public void loadFromRedis() {
        Proton.getInstance().runRedisCommand(redis -> {
            Map<String, String> results = redis.hgetAll(keyPrefix);

            for (Map.Entry<String, String> resultEntry : results.entrySet()) {
                T object = getJavaObjectSafe(resultEntry.getKey(), resultEntry.getValue());

                if (object != null) {
                    wrappedMap.put(UUID.fromString(resultEntry.getKey()), object);
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
    
    protected void updateValueSync(final UUID key, final T value) {
        wrappedMap.put(key, value);

        Proton.getInstance().runRedisCommand((RedisCommand<T>) redis -> {
            redis.hset(keyPrefix, key.toString(), getRedisValue(getValue(key)));

            boolean succeeded = false;
            int tries = 0;
            while (useMongo && !succeeded && tries++ < 5) {
                try {
                    DBCollection playersCollection = Foxtrot.getInstance().getMongoPool().getDB(Foxtrot.MONGO_DB_NAME).getCollection("Players");
                    BasicDBObject player = new BasicDBObject("_id", key.toString().replace("-", ""));

                    BasicDBObject toSet = new BasicDBObject(mongoName, getMongoValue(getValue(key)));
                    toSet.put("lastUsername", UUIDUtils.name(key));

                    playersCollection.update(player, new BasicDBObject("$set", toSet), true, false);
                    succeeded = true;
                } catch (Exception e) {
                    succeeded = false;
                }
            }

            if (useMongo && !succeeded) {
                throw new RuntimeException("Mongo insert failed after five retries! User: " + key + ".");
            }
            return (null);
        });
    }
    
    protected void updateValueAsync(final UUID key, T value) {
        wrappedMap.put(key, value);
        
        new BukkitRunnable() {

            public void run() {
                Proton.getInstance().runRedisCommand(redis -> {
                    redis.hset(keyPrefix, key.toString(), getRedisValue(getValue(key)));

                    boolean succeeded = false;
                    int tries = 0;
                    while (useMongo && !succeeded && tries++ < 5) {
                        try {
                            DBCollection playersCollection = Foxtrot.getInstance().getMongoPool().getDB(Foxtrot.MONGO_DB_NAME).getCollection("Players");
                            BasicDBObject player = new BasicDBObject("_id", key.toString().replace("-", ""));

                            BasicDBObject toSet = new BasicDBObject(mongoName, getMongoValue(getValue(key)));
                            toSet.put("lastUsername", UUIDUtils.name(key));

                            playersCollection.update(player, new BasicDBObject("$set", toSet), true, false);
                            succeeded = true;
                        } catch (Exception e) {
                            succeeded = false;
                        }
                    }

                    if (useMongo && !succeeded) {
                        throw new RuntimeException("Mongo update failed after five retries! User: " + key + ".");
                    }
                    return (null);
                });
            }
            
        }.runTaskAsynchronously(Foxtrot.getInstance());
    }
    
    protected T getValue(UUID key) {
        return (wrappedMap.get(key));
    }
    
    protected boolean contains(UUID key) {
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