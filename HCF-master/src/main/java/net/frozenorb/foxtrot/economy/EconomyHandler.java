package net.frozenorb.foxtrot.economy;

import cc.fyre.proton.Proton;
import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @author xanderume@gmail.com
 */
public class EconomyHandler {
    
    private Foxtrot instance;
    
    @Getter
    private Map<UUID,Double> balances = new HashMap<>();

    public EconomyHandler(Foxtrot instance) {
        this.instance = instance;
        
        instance.getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler()
            public void onPlayerQuit(PlayerQuitEvent event) {
                instance.getServer().getScheduler().runTaskAsynchronously(instance,() -> save(event.getPlayer().getUniqueId()));
            }

        },instance);

        Proton.getInstance().runRedisCommand((redis) -> {

            redis.keys("balance.*").forEach(it -> {
                final UUID uuid = UUID.fromString(it.substring(8));

                balances.put(uuid,Double.parseDouble(redis.get(it)));
            });

            return null;
        });

    }

    public void setBalance(UUID uuid, double balance) {
        this.balances.put(uuid, balance);
        this.instance.getServer().getScheduler().runTaskAsynchronously(this.instance, () -> save(uuid));
    }

    public double getBalance(UUID uuid) {

        if (!this.balances.containsKey(uuid)) {
            load(uuid);
        }

        return balances.get(uuid);
    }

    public void withdraw(UUID uuid, double amount) {
        setBalance(uuid,getBalance(uuid) - amount);
        this.instance.getServer().getScheduler().runTaskAsynchronously(this.instance, () -> save(uuid));
    }

    public void deposit(UUID uuid, double amount) {
        setBalance(uuid,getBalance(uuid) + amount);
        this.instance.getServer().getScheduler().runTaskAsynchronously(this.instance, () -> save(uuid));
    }

    private double load(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> Proton.getInstance().runRedisCommand((redis) -> {

            double balance = 0.0;

            if (redis.exists("balance." + uuid.toString())) {
                balance = Double.parseDouble(redis.get("balance." + uuid.toString()));
            }

            this.balances.put(uuid,balance);

            return balance;
        })).join();
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
