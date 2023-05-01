package cc.fyre.universe.server;

import cc.fyre.universe.UniverseHandler;
import com.google.gson.JsonObject;
import lombok.Getter;
import lombok.Setter;
import cc.fyre.universe.pidgin.PidginHandler;
import cc.fyre.universe.server.fetch.ServerGroup;
import cc.fyre.universe.server.fetch.ServerPerformance;
import cc.fyre.universe.server.fetch.ServerStatus;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author xanderume@gmail (JavaProject)
 */
public class Server {

    @Getter private String name;
    @Getter private Integer port;

    /* tps & full tick*/
    @Getter @Setter private ServerPerformance performance;

    @Getter @Setter private ServerStatus status;
    @Getter @Setter private ServerGroup group;

    @Getter @Setter private AtomicInteger onlinePlayers;
    @Getter @Setter private AtomicInteger maximumPlayers;

    @Getter @Setter private List<UUID> oppedPlayers;
    @Getter @Setter private List<UUID> whitelistedPlayers;

    @Getter @Setter private long lastHeartBeat;

    public Server(String name,Integer port) {
        this.name = name;
        this.port = port;

        this.performance = new ServerPerformance(20.0D,0.0D);
        this.status = ServerStatus.OFFLINE;
        this.group = ServerGroup.HUB;

        this.onlinePlayers = new AtomicInteger(0);
        this.maximumPlayers = new AtomicInteger(0);

        this.oppedPlayers = new ArrayList<>();
        this.whitelistedPlayers = new ArrayList<>();
    }

    public Server(String name,ServerGroup group,JavaPlugin plugin, boolean stopping, int fakePlayers) {
        this.name = name;
        this.port = plugin.getServer().getPort();

        this.performance = new ServerPerformance(20.0,10.0);
        this.status = plugin.getServer().hasWhitelist() ? ServerStatus.WHITELISTED : ServerStatus.ONLINE;

        if (stopping) {
            this.status = ServerStatus.OFFLINE;
        }

        this.group = group;

        this.onlinePlayers = new AtomicInteger(plugin.getServer().getOnlinePlayers().size()+fakePlayers);
        this.maximumPlayers = new AtomicInteger(plugin.getServer().getMaxPlayers());

        this.oppedPlayers = plugin.getServer().getOperators().stream().map(OfflinePlayer::getUniqueId).collect(Collectors.toList());
        this.whitelistedPlayers = plugin.getServer().getWhitelistedPlayers().stream().map(OfflinePlayer::getUniqueId).collect(Collectors.toList());
    }

    public Server(JsonObject jsonObject) {
        this.name = jsonObject.get("name").getAsString();
        this.port = jsonObject.get("port").getAsInt();

        this.performance = new ServerPerformance(jsonObject.get("performance").getAsJsonObject());

        this.status = ServerStatus.valueOf(jsonObject.get("status").getAsString());
        this.group = ServerGroup.valueOf(jsonObject.get("group").getAsString());

        this.onlinePlayers = new AtomicInteger(jsonObject.get("onlinePlayers").getAsInt());
        this.maximumPlayers = new AtomicInteger(jsonObject.get("maximumPlayers").getAsInt());

        this.oppedPlayers = PidginHandler.GSON.<List<String>>fromJson(jsonObject.get("oppedPlayers").getAsString(),ArrayList.class).stream().map(UUID::fromString).collect(Collectors.toList());
        this.whitelistedPlayers = PidginHandler.GSON.<List<String>>fromJson(jsonObject.get("whitelistedPlayers").getAsString(),ArrayList.class).stream().map(UUID::fromString).collect(Collectors.toList());
    }

    public JsonObject toJsonObject() {

        final JsonObject toReturn = new JsonObject();

        toReturn.addProperty("name",this.name);
        toReturn.addProperty("port",this.port);

        toReturn.add("performance",this.performance.toJsonObject());

        toReturn.addProperty("status",this.status.name());
        toReturn.addProperty("group",this.group.name());

        toReturn.addProperty("onlinePlayers",this.onlinePlayers.get());
        toReturn.addProperty("maximumPlayers",this.maximumPlayers.get());

        toReturn.addProperty("oppedPlayers",PidginHandler.GSON.toJson(this.oppedPlayers.stream().map(UUID::toString).collect(Collectors.toList())));
        toReturn.addProperty("whitelistedPlayers",PidginHandler.GSON.toJson(this.whitelistedPlayers.stream().map(UUID::toString).collect(Collectors.toList())));

        return toReturn;
    }

    public void refresh(JsonObject jsonObject) {
        this.performance = new ServerPerformance(jsonObject.get("performance").getAsJsonObject());

        this.status = ServerStatus.valueOf(jsonObject.get("status").getAsString());
        this.group = ServerGroup.valueOf(jsonObject.get("group").getAsString());

        this.onlinePlayers.set(jsonObject.get("onlinePlayers").getAsInt());
        this.maximumPlayers.set(jsonObject.get("maximumPlayers").getAsInt());

        this.oppedPlayers = PidginHandler.GSON.<List<String>>fromJson(jsonObject.get("oppedPlayers").getAsString(),ArrayList.class).stream().map(UUID::fromString).collect(Collectors.toList());
        this.whitelistedPlayers = PidginHandler.GSON.<List<String>>fromJson(jsonObject.get("whitelistedPlayers").getAsString(),ArrayList.class).stream().map(UUID::fromString).collect(Collectors.toList());
    }

    public void cache(JedisPool pool) {

        final String key = UniverseHandler.SERVER_CACHE_KEY + this.port;

        try (Jedis jedis = pool.getResource()) {

            jedis.hset(key,"name",this.name);
            jedis.hset(key,"port","" + this.port);

            jedis.hset(key,"performance","" + this.performance.toJsonObject());

            jedis.hset(key,"status",this.status.name());
            jedis.hset(key,"group",this.group.name());

            jedis.hset(key,"onlinePlayers","" + this.onlinePlayers.get());
            jedis.hset(key,"maximumPlayers",""+ this.maximumPlayers.get());

            jedis.hset(key,"oppedPlayers",PidginHandler.GSON.toJson(this.oppedPlayers.stream().map(UUID::toString).collect(Collectors.toList())));
            jedis.hset(key,"whitelistedPlayers",PidginHandler.GSON.toJson(this.whitelistedPlayers.stream().map(UUID::toString).collect(Collectors.toList())));

            jedis.hset(UniverseHandler.SERVER_LOOKUP_KEY + "port","" + this.port,this.name);
            jedis.hset(UniverseHandler.SERVER_LOOKUP_KEY + "name",this.name,"" + this.port);
        }

    }

    public boolean canJoin(UUID uuid) {

        if (this.status == ServerStatus.WHITELISTED) {
            return this.oppedPlayers.contains(uuid) || this.whitelistedPlayers.contains(uuid);
        }

        if (this.maximumPlayers.get() <= this.onlinePlayers.get()) {
            return false;
        }

        return this.status == ServerStatus.ONLINE;
    }
}
