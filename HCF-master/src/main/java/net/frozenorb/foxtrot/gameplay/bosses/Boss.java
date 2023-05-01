package net.frozenorb.foxtrot.gameplay.bosses;

import lombok.Getter;
import lombok.Setter;
import net.frozenorb.foxtrot.Foxtrot;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public abstract class Boss implements Listener {
    @Getter @Setter private Entity entity;
    
    public abstract String getBossID();
    public abstract String getBossDisplayName();
    public abstract int getMaxHealth();
    public abstract double getDamageMultiplier();
    public abstract EntityType getEntityType();
    public abstract String getWorldName();

    public void activate() {
        final int x = ThreadLocalRandom.current().nextInt(75, 300);
        final int z = ThreadLocalRandom.current().nextInt(75, 300);
        final Location location = this.findWorld().getHighestBlockAt(x, z).getLocation().add(new Vector(0, 1, 0)).clone();

        this.activate(location);

        for (Player onlinePlayer : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
            onlinePlayer.sendMessage("");
            onlinePlayer.sendMessage(ChatColor.translate("&7███████"));
            onlinePlayer.sendMessage(ChatColor.translate("&7█" + "&4████" + "&7██ " + this.getBossDisplayName()));
            onlinePlayer.sendMessage(ChatColor.translate("&7█" + "&4█" + "&7███" + "&4█" + "&7█ has spawned!"));
            onlinePlayer.sendMessage(ChatColor.translate("&7█" + "&4████" + "&7██"));
            onlinePlayer.sendMessage(ChatColor.translate("&7█" + "&4█" + "&7███" + "&4█" + "&7█ &cLocation:"));
            onlinePlayer.sendMessage(ChatColor.translate("&7█" + "&4████" + "&7██ &f" + location.getBlockX() + ", " + location.getBlockZ() + " [" + WordUtils.capitalize(this.findWorld().getEnvironment().name()) + "]"));
            onlinePlayer.sendMessage(ChatColor.translate("&7███████"));
            onlinePlayer.sendMessage("");

            onlinePlayer.playSound(onlinePlayer.getLocation(), Sound.WITHER_SPAWN, 1, 1);
        }
    }

    public void activate(Location location) {
        System.out.println("============================");
        System.out.println();
        System.out.println("Spawning " + this.getBossID() + " boss at " + location.getBlockX() + ", " + location.getBlockZ());
        System.out.println();
        System.out.println("============================");

        System.out.println("[Foxtrot] Loaded Chunk at " + location.getBlockX() + ", " + location.getBlockZ());
        location.getChunk().load(true);

        IntStream.of(1,4).forEach(it -> location.getWorld().strikeLightningEffect(location));

        final Monster monster = (Monster) location.getWorld().spawnEntity(location, this.getEntityType());
        monster.setMetadata("BOSS", new FixedMetadataValue(Foxtrot.getInstance(), true));
        monster.setMaxHealth(this.getMaxHealth());
        monster.setHealth(monster.getMaxHealth());
        monster.setCustomNameVisible(true);
        monster.setCustomName(this.getBossDisplayName());

        this.entity = monster;

        Foxtrot.getInstance().getServer().getPluginManager().registerEvents(this, Foxtrot.getInstance());
    }

    public void deactivate(Player winner) {
        final Foxtrot instance = Foxtrot.getInstance();
        final BossHandler bossHandler = instance.getBossHandler();

        bossHandler.setActiveBoss(null);
        HandlerList.unregisterAll(this);

        if (winner == null) {
            instance.getServer().broadcastMessage("");
            instance.getServer().broadcastMessage(ChatColor.translate("&4&lBoss"));
            instance.getServer().broadcastMessage(ChatColor.translate(this.getBossDisplayName() + " &7has been forcefully killed."));
            instance.getServer().broadcastMessage("");
            return;
        }

        winner.playSound(winner.getLocation(), Sound.LEVEL_UP, 1, 1);

        if (Foxtrot.getInstance().getMapHandler().isKitMap()) {
            if (ThreadLocalRandom.current().nextInt(0, 100) <= 25) {
                instance.getServer().dispatchCommand(instance.getServer().getConsoleSender(), "chest give Treasure " + winner.getName() + " 1");
            } else {
                instance.getServer().dispatchCommand(instance.getServer().getConsoleSender(), "cr givekey " + winner.getName() + " Mario 3");
            }
        } else {
            if (ThreadLocalRandom.current().nextInt(0, 100) <= 25) {
                instance.getServer().dispatchCommand(instance.getServer().getConsoleSender(), "chest give Omega " + winner.getName() + " 1");
            } else {
                instance.getServer().dispatchCommand(instance.getServer().getConsoleSender(), "mcrate give " + winner.getName() + " Seasonal 2");
            }
        }

        instance.getServer().broadcastMessage("");
        instance.getServer().broadcastMessage(ChatColor.translate("&4&lBoss"));
        instance.getServer().broadcastMessage(ChatColor.translate(winner.getDisplayName() + " &7has killed the boss!"));
        instance.getServer().broadcastMessage("");
    }

    public World findWorld() {
        return Foxtrot.getInstance().getServer().getWorld(this.getWorldName());
    }

    @EventHandler
    private void onDamage(EntityDamageByEntityEvent event) {
        if (!event.getEntity().getType().equals(this.getEntityType())) {
            return;
        }

        if (!event.getEntity().equals(event.getEntity())) {
            return;
        }

        Player damager = null;

        if (event.getDamager() instanceof Player) {
            damager = (Player) event.getDamager();
        }

        if (event.getDamager() instanceof Projectile) {
            final Projectile projectile = (Projectile) event.getDamager();

            if (!(projectile.getShooter() instanceof Player)) {
                return;
            }

            damager = (Player) projectile.getShooter();
        }

        if (damager == null) {
            return;
        }

        if (Foxtrot.getInstance().getPvPTimerMap().hasTimer(damager.getUniqueId())) {
            damager.sendMessage(ChatColor.RED + "Use /pvp enable to toggle your PvP Timer off!");
            event.setCancelled(true);
        }
    }
}
