package net.frozenorb.foxtrot.gameplay.bosses.type;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.bosses.Boss;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class Surtur extends Boss {

    @Override
    public String getBossID() {
        return "Blaze";
    }

    @Override
    public String getBossDisplayName() {
        return ChatColor.translate("&6&lSurtur");
    }

    @Override
    public int getMaxHealth() {
        return 1500;
    }

    @Override
    public double getDamageMultiplier() {
        return 12.5;
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.BLAZE;
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
                        ((Player) it).playSound(it.getLocation(), Sound.WITHER_SHOOT, 1, 1);
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

        getEntity().getWorld().spawnEntity(block.getLocation().clone(), EntityType.BLAZE);
    }
}
