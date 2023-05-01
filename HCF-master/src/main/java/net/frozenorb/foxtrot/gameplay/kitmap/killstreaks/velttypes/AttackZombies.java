package net.frozenorb.foxtrot.gameplay.kitmap.killstreaks.velttypes;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.kitmap.killstreaks.Killstreak;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AttackZombies extends Killstreak implements Listener {

    public AttackZombies() {
        Foxtrot.getInstance().getServer().getPluginManager().registerEvents(this, Foxtrot.getInstance());
    }

    @Override
    public String getName() {
        return "Attack Zombies";
    }

    @Override
    public int[] getKills() {
        return new int[] {
                75
        };
    }

    @Override
    public Material getMaterial() {
        return Material.ROTTEN_FLESH;
    }

    @Override
    public List<String> getDescription() {
        return Collections.singletonList("Spawn a herd of baby zombies on your side!");
    }

    @Override
    public void apply(Player player) {
        player.sendMessage(ChatColor.GREEN + "Your attack zombies have spawned near you.");
        player.playSound(player.getLocation(), Sound.ZOMBIE_INFECT, 1, 1);

        final List<Zombie> cache = new ArrayList<>();

        for (int i = 0; i < 4; i++) {
            Zombie zombie = player.getWorld().spawn(player.getLocation(), Zombie.class);

            zombie.setBaby(true);
            zombie.setMetadata("ATTACK_ZOMBIE", new FixedMetadataValue(Foxtrot.getInstance(), player.getUniqueId().toString()));

            zombie.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 4));
            zombie.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 255));
            zombie.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 2147483647, 255));
            zombie.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 1));

            cache.add(zombie);
        }

        Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
            cache.stream().filter(it -> !it.isDead()).forEach(Entity::remove);

            if (player.isOnline()) {
                player.sendMessage(ChatColor.RED + "All your wolves have despawned!");
            }
        }, 20*60*10);
    }

    @EventHandler
    private void onTarget(EntityTargetEvent event) {
        if (!(event.getTarget() instanceof Player) || !(event.getEntity() instanceof Zombie)) {
            return;
        }

        final Player target = (Player) event.getTarget();
        final Zombie zombie = (Zombie) event.getEntity();

        if (!zombie.hasMetadata("ATTACK_ZOMBIE")) {
            return;
        }

        if (zombie.getMetadata("ATTACK_ZOMBIE").get(0).asString().equalsIgnoreCase(target.getUniqueId().toString())) {
            event.setCancelled(true);
        }

    }

}
