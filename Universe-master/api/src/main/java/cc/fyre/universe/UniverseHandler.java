package cc.fyre.universe;

import lombok.Getter;

import lombok.Setter;
import cc.fyre.universe.pidgin.PidginHandler;
import cc.fyre.universe.proxy.Proxy;
import cc.fyre.universe.proxy.comparator.ProxyPlayersComparator;
import cc.fyre.universe.proxy.comparator.ProxyPortComparator;
import cc.fyre.universe.proxy.comparator.ProxyRegionComparator;
import cc.fyre.universe.proxy.comparator.ProxyStatusComparator;
import cc.fyre.universe.proxy.fetch.ProxyRegion;
import cc.fyre.universe.proxy.fetch.ProxyStatus;
import cc.fyre.universe.server.Server;
import cc.fyre.universe.server.comparator.ServerGroupComparator;
import cc.fyre.universe.server.comparator.ServerPlayersComparator;
import cc.fyre.universe.server.comparator.ServerPortComparator;
import cc.fyre.universe.server.comparator.ServerStatusComparator;
import cc.fyre.universe.server.fetch.ServerGroup;
import cc.fyre.universe.server.fetch.ServerStatus;
import org.bukkit.Bukkit;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author xanderume@gmail (JavaProject)
 */
public class UniverseHandler {

    @Getter private JedisPool pool;

    @Getter private List<Proxy> proxies;
    @Getter private List<Server> servers;

    @Getter @Setter private boolean maintenanceMode;
    @Getter private List<UUID> maintenanceList;

    public static String PROXY_CACHE_KEY = "universe:proxy-cache:";
    public static String SERVER_CACHE_KEY = "universe:server-cache:";

    public static String PROXY_LOOKUP_KEY = "universe:proxy-lookup:";
    public static String SERVER_LOOKUP_KEY = "universe:server-lookup:";

    public static int REDIS_KEY = 15;

    public UniverseHandler(JedisPool pool) {
        this.pool = pool;
        this.proxies = new ArrayList<>();
        this.servers = new ArrayList<>();

        this.load();
    }

    public void load() {

        try (Jedis jedis = this.pool.getResource()) {

            if (jedis.get("universe:maintenanceMode") != null) {
                this.maintenanceMode = Boolean.parseBoolean(jedis.get("universe:maintenanceMode"));
            }

            if (jedis.get("universe:maintenanceList") != null) {
                this.maintenanceList = PidginHandler.GSON.<List<String>>fromJson(jedis.get("universe:maintenanceList"),ArrayList.class).stream().map(UUID::fromString).collect(Collectors.toList());
            } else {
                this.maintenanceList = new ArrayList<>();
            }

            for (String path : jedis.keys("universe:proxy-cache:*")) {

                final String[] split = path.split(":");
                final int port = Integer.valueOf(split[2]);

                final Map<String,String> map = jedis.hgetAll(path);

                if (map == null || map.isEmpty()) {
                    continue;
                }

                Proxy proxy = this.proxyFromName(map.get("name"));

                if (proxy == null) {
                    this.proxies.add(proxy = new Proxy(map.get("name"),port));
                }

                proxy.setStatus(ProxyStatus.valueOf(map.get("status")));
                proxy.setRegion(ProxyRegion.valueOf(map.get("region")));

                proxy.setOnlinePlayers(new AtomicInteger(Integer.valueOf(map.get("onlinePlayers"))));
                proxy.setMaximumPlayers(new AtomicInteger(Integer.valueOf(map.get("maximumPlayers"))));

                if (!this.proxies.contains(proxy)) {
                    this.proxies.add(proxy);
                }

            }

            for (String path : jedis.keys("universe:server-cache:*")) {

                final Map<String, String> map = jedis.hgetAll(path);

                final String[] split = path.split(":");
                final int port = Integer.valueOf(split[2]);

                if (map == null || map.isEmpty()) {
                    continue;
                }

                Server server = this.serverFromName(map.get("name"));

                if (server == null) {
                    this.servers.add(server = new Server(map.get("name"),port));
                }

                server.setStatus(ServerStatus.valueOf(map.get("status")));
                server.setGroup(ServerGroup.valueOf(map.get("group")));
                server.setOnlinePlayers(new AtomicInteger(Integer.valueOf(map.get("onlinePlayers"))));
                server.setMaximumPlayers(new AtomicInteger(Integer.valueOf(map.get("maximumPlayers"))));

                server.setOppedPlayers(PidginHandler.GSON.<List<String>>fromJson(map.get("oppedPlayers"),ArrayList.class).stream().map(UUID::fromString).collect(Collectors.toList()));
                server.setWhitelistedPlayers(PidginHandler.GSON.<List<String>>fromJson(map.get("whitelistedPlayers"),ArrayList.class).stream().map(UUID::fromString).collect(Collectors.toList()));

                if (!this.servers.contains(server)) {
                    this.servers.add(server);
                }

            }
        }

    }

    public Server serverFromName(String name) {
        return this.servers.stream().filter(server -> server.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public Proxy proxyFromName(String name) {
        return this.proxies.stream().filter(proxy -> proxy.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public List<Proxy> getSortedProxies() {
        return Collections.unmodifiableList(this.proxies.stream()
                .sorted(new ProxyRegionComparator().reversed()
                .thenComparing(new ProxyPlayersComparator().reversed()
                .thenComparing(new ProxyPortComparator().reversed())
                .thenComparing(new ProxyStatusComparator().reversed())))
                .collect(Collectors.toList())
        );
    }

    public List<Server> getSortedServers() {
        return Collections.unmodifiableList(this.servers.stream()
                .sorted(new ServerGroupComparator().reversed()
                .thenComparing(new ServerPlayersComparator().reversed()
                .thenComparing(new ServerPortComparator()).reversed()
                .thenComparing(new ServerStatusComparator().reversed())))
                .collect(Collectors.toList())
        );
    }

    public List<Server> getAvailableServers(UUID uuid) {
        return this.getAvailableServers(uuid,Arrays.asList(ServerGroup.values()));
    }

    public List<Server> getAvailableServers(UUID uuid,ServerGroup group) {
        return this.getAvailableServers(uuid,Arrays.asList(group));
    }

    public List<Server> getAvailableServers(UUID uuid,List<ServerGroup> groups) {

        final List<Server> toReturn = new ArrayList<>();

        for (Server server : this.servers) {

            if (!groups.contains(server.getGroup())) {
                continue;
            }

            if (server.getStatus().equals(ServerStatus.OFFLINE)) {
                continue;
            }

            if (server.getOnlinePlayers().get() >= server.getMaximumPlayers().get() && !server.getOppedPlayers().contains(uuid)) {
                continue;
            }

            if (server.getStatus().equals(ServerStatus.WHITELISTED) && !(server.getOppedPlayers().contains(uuid) || server.getWhitelistedPlayers().contains(uuid))) {
                continue;
            }

            toReturn.add(server);
        }

        return toReturn;
    }

    public int getPlayersOnNetwork() {
        return this.getPlayersOnRegion(Arrays.asList(ProxyRegion.values()));
    }

    public int getPlayersOnRegion(List<ProxyRegion> regions) {

        final AtomicInteger toReturn = new AtomicInteger(0);

        for (Proxy proxy : this.proxies) {

            if (!regions.contains(proxy.getRegion())) {
                continue;
            }

            toReturn.addAndGet(proxy.getOnlinePlayers().get());
        }

        return toReturn.get();
    }

}
