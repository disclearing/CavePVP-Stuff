package net.frozenorb.foxtrot.persist.maps;

import net.minecraft.util.com.google.common.collect.Maps;

import java.util.*;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.persist.PersistMap;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import net.frozenorb.foxtrot.persist.maps.event.SyncPlaytimeEvent;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class PlaytimeMap extends PersistMap<Long> {

    private Map<UUID, Long> joinDate = new HashMap<>();
    private Map<UUID, Long> pendingReward = Maps.newConcurrentMap();

    public PlaytimeMap() {
        super("PlayerPlaytimes", "Playtime");

        AtomicInteger ai = new AtomicInteger(30);
        Bukkit.getScheduler().runTaskTimerAsynchronously(Foxtrot.getInstance(), () -> {
            ai.decrementAndGet();

            if (ai.get() <= 0) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    Bukkit.getPluginManager().callEvent(new SyncPlaytimeEvent(player));
                }

                ai.set(30);
            }

            List<UUID> toRemove = new ArrayList<>();
            List<UUID> toUpdate = new ArrayList<>();

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!(pendingReward.containsKey(player.getUniqueId()))) {
                    pendingReward.put(player.getUniqueId(), System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1));
                }
            }

            for (UUID uuid : pendingReward.keySet()) {
                long time = pendingReward.get(uuid);

                if (Bukkit.getPlayer(uuid) == null) {
                    toRemove.add(uuid);
                    continue;
                }

                if (System.currentTimeMillis() >= time) {
                    toUpdate.add(uuid);
                }
            }

            for (UUID uuid : toRemove) {
                pendingReward.remove(uuid);
            }

            for (UUID uuid : toUpdate) {
                Player player = Bukkit.getPlayer(uuid);

                if (player != null) {

                    Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);

                    if (team != null) {
                        team.addPlaytimeGems(5);
                        team.sendMessage(ChatColor.GREEN + "Your team received 5 gems thanks to " + ChatColor.AQUA + ChatColor.BOLD + player.getName() + ChatColor.GREEN + "'s play-time.");
                    }

                    pendingReward.put(uuid, System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1));
                }
            }
        }, 0, 20L);
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
        return (time.intValue());
    }

    public void playerJoined(UUID update) {
        joinDate.put(update, System.currentTimeMillis());

        if (!contains(update)) {
            updateValueAsync(update, 0L);
        }
    }

    public void playerQuit(UUID update, boolean async) {
        joinDate.putIfAbsent(update, System.currentTimeMillis());

        if (async) {
            updateValueAsync(update, getPlaytime(update) + (System.currentTimeMillis() - joinDate.get(update)) / 1000);
        } else {
            updateValueSync(update, getPlaytime(update) + (System.currentTimeMillis() - joinDate.get(update)) / 1000);
        }
    }

    public long getCurrentSession(UUID check) {
        if (joinDate.containsKey(check)) {
            return (System.currentTimeMillis() - joinDate.get(check));
        }

        return (0L);
    }

    public long getPlaytime(UUID check) {
        return (contains(check) ? getValue(check) : 0L);
    }

    public boolean hasPlayed(UUID check) {
        return (contains(check));
    }

    public void setPlaytime(UUID update, long playtime) {
        updateValueSync(update, playtime);
    }

    private static final long HOUR_IN_MS = 3_600_000L;

    private long calculateNextRewardTime(UUID uuid) {
        return System.currentTimeMillis() + ((HOUR_IN_MS) - (((getPlaytime(uuid) * 1000L) + getCurrentSession(uuid)) % (HOUR_IN_MS * 2)));
    }

}