package cc.fyre.proton.uuid;

import cc.fyre.proton.Proton;
import cc.fyre.proton.pidgin.packet.handler.IncomingPacketHandler;
import cc.fyre.proton.pidgin.packet.listener.PacketListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public final class UUIDCache implements PacketListener, Listener {

    public static final UUID CONSOLE_UUID = UUID.fromString("f78a4d8d-d51b-4b39-98a3-230f2de0c670");

    private final Map<UUID, String> uuidToName = new ConcurrentHashMap<>();
    private final Map<String, UUID> nameToUuid = new ConcurrentHashMap<>();

    public UUIDCache() {
        Proton.getInstance().runBackboneRedisCommand(redis -> {
            Map<String, String> cache = redis.hgetAll("UUIDCache");

            for (Map.Entry<String, String> cacheEntry : cache.entrySet()) {
                UUID uuid = UUID.fromString(cacheEntry.getKey());
                String name = cacheEntry.getValue();

                uuidToName.put(uuid, name);
                nameToUuid.put(name.toLowerCase(), uuid);
            }
            return null;
        });

        Proton.getInstance().getPidginHandler().registerPacket(UUIDCacheUpdatePacket.class);
        Proton.getInstance().getPidginHandler().registerListener(this);

        Proton.getInstance().getServer().getPluginManager().registerEvents(this, Proton.getInstance());

        update(CONSOLE_UUID, "CONSOLE");
    }

    public UUID uuid(String name) {
        return nameToUuid.get(name.toLowerCase());
    }

    public String name(UUID uuid) {
        return uuidToName.get(uuid);
    }

    public boolean cached(UUID uuid) {
        return uuidToName.containsKey(uuid);
    }

    public boolean cached(String name) {
        return nameToUuid.containsKey(name.toLowerCase());
    }

    private void update(UUID uuid, String name) {

        if (nameToUuid.containsKey(name.toLowerCase())) {
            final UUID secondUUID = nameToUuid.get(name.toLowerCase());
            uuidToName.remove(secondUUID);
            nameToUuid.remove(name.toLowerCase());
        }

        uuidToName.put(uuid, name);

        for (Map.Entry<String, UUID> entry : (new HashMap<>(nameToUuid)).entrySet()) {
            if (entry.getValue().equals(uuid)) {
                nameToUuid.remove(entry.getKey());
            }
        }

        nameToUuid.put(name.toLowerCase(), uuid);
    }

    public void updateAll(UUID uuid, String name) {
        update(uuid, name);

        Proton.getInstance().runBackboneRedisCommand(redis -> {
            redis.hset("UUIDCache", uuid.toString(), name);
            return null;
        });

        Proton.getInstance().getPidginHandler().sendPacket(new UUIDCacheUpdatePacket(uuid, name));
    }

    @IncomingPacketHandler
    public void onRedisUUIDUpdate(UUIDCacheUpdatePacket packet) {
        update(packet.uuid(), packet.name());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();
        String name = event.getName();
        String cachedName = name(uuid);

        if (!name.equals(cachedName)) {
            updateAll(event.getUniqueId(), event.getName());
        }

        if (!uuid(name).toString().equalsIgnoreCase(uuid.toString())) {
            updateAll(event.getUniqueId(), event.getName());
        }
    }
}
