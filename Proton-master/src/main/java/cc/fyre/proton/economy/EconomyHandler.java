package cc.fyre.proton.economy;

import cc.fyre.proton.Proton;
import lombok.Getter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EconomyHandler {

    @Getter private Map<UUID, Double> balances = new HashMap<>();

    public EconomyHandler() {

        Proton.getInstance().getServer().getPluginManager().registerEvents(new Listener() {

            @EventHandler()
            public void onPlayerQuit(PlayerQuitEvent event) {
                Proton.getInstance().getServer().getScheduler().runTaskAsynchronously(Proton.getInstance(),() -> {
                    save(event.getPlayer().getUniqueId());
                });
            }

        },Proton.getInstance());


        Proton.getInstance().runRedisCommand((redis) -> {

            for (String key : redis.keys("balance.*")) {

                final UUID uuid = UUID.fromString(key.substring(8));

                balances.put(uuid,Double.parseDouble(redis.get(key)));
            }

            return null;
        });
    }

    public void setBalance(UUID uuid, double balance) {
        this.balances.put(uuid, balance);
        Proton.getInstance().getServer().getScheduler().runTaskAsynchronously(Proton.getInstance(), () -> save(uuid));
    }

    public double getBalance(UUID uuid) {

        if (!this.balances.containsKey(uuid)) {
            load(uuid);
        }

        return balances.get(uuid);
    }

    public void withdraw(UUID uuid, double amount) {
        setBalance(uuid,getBalance(uuid) - amount);
        Proton.getInstance().getServer().getScheduler().runTaskAsynchronously(Proton.getInstance(), () -> save(uuid));
    }

    public void deposit(UUID uuid, double amount) {
        setBalance(uuid,getBalance(uuid) + amount);
        Proton.getInstance().getServer().getScheduler().runTaskAsynchronously(Proton.getInstance(), () -> save(uuid));
    }

    private void load(UUID uuid) {
        /*
        Proton.getInstance().runRedisCommand((redis) -> {

            if (redis.exists("balance." + uuid.toString())) {
                balances.put(uuid, Double.parseDouble(redis.get("balance." + uuid.toString())));
            } else {
                balances.put(uuid, 0.0D);
            }

            return null;
        });*/

    }

    private void save(UUID uuid) {
        Proton.getInstance().runRedisCommand((redis) -> redis.set("balance." + uuid.toString(), String.valueOf(getBalance(uuid))));
    }

    public void save() {
        Proton.getInstance().runRedisCommand((redis) -> {

            for (Map.Entry<UUID,Double> entry : this.balances.entrySet()) {
                redis.set("balance." + entry.getKey().toString(),String.valueOf(entry.getValue()));
            }

            return null;
        });
    }
}
