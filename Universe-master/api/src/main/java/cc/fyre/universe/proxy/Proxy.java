package cc.fyre.universe.proxy;

import cc.fyre.universe.UniverseHandler;
import com.google.gson.JsonObject;

import lombok.Getter;
import lombok.Setter;

import cc.fyre.universe.proxy.fetch.ProxyRegion;
import cc.fyre.universe.proxy.fetch.ProxyStatus;

import net.md_5.bungee.api.plugin.Plugin;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author xanderume@gmail (JavaProject)
 */
public class Proxy {

    @Getter private String name;
    @Getter private Integer port;

    @Getter @Setter private ProxyStatus status;
    @Getter @Setter private ProxyRegion region;

    @Getter @Setter private AtomicInteger onlinePlayers;
    @Getter @Setter private AtomicInteger maximumPlayers;

    public Proxy(String name,Integer port) {
        this.name = name;
        this.port = port;

        this.status = ProxyStatus.OFFLINE;
        this.region = ProxyRegion.UNITED_STATES;

        this.onlinePlayers = new AtomicInteger(0);
        this.maximumPlayers = new AtomicInteger(0);
    }

    public Proxy(String name,ProxyRegion region,Plugin plugin) {
        this.name = name;
        this.port = 25565;

        this.status = ProxyStatus.ONLINE;
        this.region = region;

        this.onlinePlayers = new AtomicInteger(plugin.getProxy().getPlayers().size());
        this.maximumPlayers = new AtomicInteger(plugin.getProxy().getConfig().getPlayerLimit());
    }

    public Proxy(JsonObject jsonObject) {
        this.name = jsonObject.get("name").getAsString();
        this.port = jsonObject.get("port").getAsInt();

        this.status = ProxyStatus.valueOf(jsonObject.get("status").getAsString());
        this.region = ProxyRegion.valueOf(jsonObject.get("region").getAsString());

        this.onlinePlayers = new AtomicInteger(jsonObject.get("onlinePlayers").getAsInt());
        this.maximumPlayers = new AtomicInteger(jsonObject.get("maximumPlayers").getAsInt());
    }

    public JsonObject toJsonObject() {

        final JsonObject toReturn = new JsonObject();

        toReturn.addProperty("name",this.name);
        toReturn.addProperty("port",this.port);

        toReturn.addProperty("status",this.status.name());
        toReturn.addProperty("region",this.region.name());

        toReturn.addProperty("onlinePlayers",this.onlinePlayers.get());
        toReturn.addProperty("maximumPlayers",this.maximumPlayers.get());

        return toReturn;
    }

    public void refresh(JsonObject jsonObject) {
        this.status = ProxyStatus.valueOf(jsonObject.get("status").getAsString());
        this.region = ProxyRegion.valueOf(jsonObject.get("region").getAsString());

        this.onlinePlayers.set(jsonObject.get("onlinePlayers").getAsInt());
        this.maximumPlayers.set(jsonObject.get("maximumPlayers").getAsInt());
    }

    public void cache(JedisPool pool) {

        final String key = UniverseHandler.PROXY_CACHE_KEY + this.port;

        try (Jedis jedis = pool.getResource()) {

            jedis.hset(key,"name",this.name);
            jedis.hset(key,"port","" + this.port);

            jedis.hset(key,"status",this.status.name());
            jedis.hset(key,"region",this.region.name());

            jedis.hset(key,"onlinePlayers","" + this.onlinePlayers.get());
            jedis.hset(key,"maximumPlayers",""+ this.maximumPlayers.get());

            jedis.hset(UniverseHandler.PROXY_LOOKUP_KEY + "port","" + this.port,this.name);
            jedis.hset(UniverseHandler.PROXY_LOOKUP_KEY + "name",this.name,"" + this.port);
        }

    }
}
