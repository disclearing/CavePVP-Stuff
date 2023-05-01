package net.frozenorb.foxtrot.gameplay.content.service;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.content.ContentHandler;
import net.frozenorb.foxtrot.gameplay.content.ContentType;
import org.bukkit.entity.Player;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class ContentService extends BukkitRunnable {
    private Foxtrot instance;
    private ContentHandler contentHandler;
    private Map<UUID, Integer> seconds = new HashMap<>();

    @Override
    public void run() {
        for (Player onlinePlayer : this.instance.getServer().getOnlinePlayers()) {

            seconds.putIfAbsent(onlinePlayer.getUniqueId(), 30);
            int seconds = this.seconds.getOrDefault(onlinePlayer.getUniqueId(), 30);

            if (contentHandler.isContentType(onlinePlayer.getUniqueId(), ContentType.RANDOM_EFFECT)) {
                seconds--;

                if (seconds == 0) {
                    List<PotionEffectType> potionEffects = Arrays.asList(PotionEffectType.values());

                    PotionEffectType potionEffectType = potionEffects.get(ThreadLocalRandom.current().nextInt(potionEffects.size()-1));

                    onlinePlayer.addPotionEffect(new PotionEffect(potionEffectType, 20*30, 1));
                }
            }

            if (contentHandler.isContentType(onlinePlayer.getUniqueId(), ContentType.WATER_DAMAGE)) {

                if (onlinePlayer.getLocation().getBlock().getType().name().contains("WATER")) {
                    onlinePlayer.damage(0);
                    onlinePlayer.setHealth(onlinePlayer.getHealth()-4);
                }

            }
        }
    }
}
