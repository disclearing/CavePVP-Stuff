package net.frozenorb.foxtrot.gameplay.bosses.type;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.bosses.Boss;
import net.frozenorb.foxtrot.util.RandomUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.stream.Collectors;

public class Giant extends Boss {

    @Override
    public String getBossID() {
        return "Giant";
    }

    @Override
    public String getBossDisplayName() {
        return ChatColor.translate("&2&lGiant");
    }

    @Override
    public int getMaxHealth() {
        return 1400;
    }

    @Override
    public double getDamageMultiplier() {
        return 10;
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.GIANT;
    }

    @Override
    public String getWorldName() {
        return "world";
    }

    private int seconds;

    @Override
    public void activate(Location location) {
        super.activate(location);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (getEntity().isDead()) {
                    this.cancel();
                    return;
                }

                if (seconds % 15 == 0) {
                    getEntity().getNearbyEntities(10, 10, 10).stream().filter(it -> it instanceof Player).forEach(it -> {
                        it.setVelocity(it.getLocation().getDirection().multiply(-2.5));
                        ((Player) it).playSound(it.getLocation(), Sound.ZOMBIE_WOODBREAK, 1, 1);
                    });
                }

                seconds++;

                if (seconds % 25 != 0) {
                    return;
                }

                if (getEntity().getNearbyEntities(30, 30, 30).isEmpty()) {
                    return;
                }

                for (int i = 0; i < 5; i++) {
                    spawnMinion(BlockFace.UP);
                }

                for (int i = 0; i < 5; i++) {
                    spawnMinion(BlockFace.WEST);
                }

                for (int i = 0; i < 5; i++) {
                    spawnMinion(BlockFace.EAST);
                }

                for (int i = 0; i < 5; i++) {
                    spawnMinion(BlockFace.SOUTH);
                }

                for (int i = 0; i < 5; i++) {
                    spawnMinion(BlockFace.NORTH);
                }
            }
        }.runTaskTimer(Foxtrot.getInstance(), 20, 20);
    }

    public void stomp() {
        if (this.getEntity().isOnGround()) {
            this.getEntity().setVelocity(new Vector(0, RandomUtil.getRandDouble(1.5, 2.25), 0));
            this.getEntity().setMetadata("stomp", new FixedMetadataValue(Foxtrot.getInstance(), true));

            new BukkitRunnable() {
                @Override
                public void run() {
                    if(getEntity().isDead() || !getEntity().isValid()) {
                        cancel();
                        return;
                    }

                    if(getEntity().isOnGround()) {
                        List<Player> nearbyPlayers = getEntity().getNearbyEntities(10, 10, 10).stream()
                                .filter(Player.class::isInstance)
                                .map(Player.class::cast)
                                .collect(Collectors.toList());
                        for(Player player : nearbyPlayers)
                            player.damage(20.0D, getEntity());
//                            player.setHealth(Math.max(player.getHealth() - RandomUtil.getRandInt(10, 20), 8));

                        getEntity().removeMetadata("stomp", Foxtrot.getInstance());
                        cancel();
                    }
                }
            }.runTaskTimer(Foxtrot.getInstance(), 20L, 4L);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Monster)) {
            return;
        }

        if (event.getEntity().hasMetadata("BOSS")) {
            this.deactivate(event.getEntity().getKiller());
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        Monster monster = null;

        if (event.getDamager() instanceof Monster) {
            monster = (Monster) event.getDamager();
        }

        if (event.getDamager() instanceof Projectile) {
            final Projectile projectile = (Projectile) event.getDamager();

            if (projectile.getShooter() instanceof Monster) {
                monster = (Monster) projectile.getShooter();
            }
        }

        if (monster == null) {
            return;
        }

        if (monster.hasMetadata("BOSS")) {
            event.setDamage(event.getDamage()*this.getDamageMultiplier());
        }
    }

    public void spawnMinion(BlockFace blockFace) {
        final Location entityLocation = getEntity().getLocation().clone();
        final Block block = entityLocation.getBlock().getRelative(blockFace, 2);

        this.getEntity().getWorld().spawnEntity(block.getLocation().clone(), EntityType.ZOMBIE);
        this.getEntity().getWorld().playSound(block.getLocation(), Sound.ZOMBIE_INFECT, 1, 1);
    }
}
