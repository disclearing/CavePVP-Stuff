package net.frozenorb.foxtrot.gameplay.ability.type;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.Ability;
import net.frozenorb.foxtrot.gameplay.ability.Category;
import net.frozenorb.foxtrot.gameplay.ability.listener.events.AbilityUseEvent;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.util.ParticleEffect;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class PhoenixMode extends Ability {
    private Map<UUID, Location> cache = new HashMap<>();

    public PhoenixMode() {
        new BukkitRunnable() {

            @Override
            public void run() {
                for (Map.Entry<UUID, Location> entry : cache.entrySet()) {
                    final Player player = Foxtrot.getInstance().getServer().getPlayer(entry.getKey());

                    if (player == null || player.isDead() || !player.isOnline()) {
                        cancel();
                        return;
                    }

                    Location location = entry.getValue();
                    Location cornerOne = new Location(location.getWorld(), location.getX() - 5, location.getY() - 5, location.getZ() - 5),
                            cornerTwo = new Location(location.getWorld(), location.getX() + 5, location.getY() + 5, location.getZ() + 5);
                    List<Vector> vectors = getHollowCube(cornerOne, cornerTwo);
                    for(Vector vector : vectors)
                        ParticleEffect.CLOUD.display(0.0f, 0.0f, 0.0f, 0.01f, 1, vector.toLocation(player.getWorld()), 50);
                }
            }
        }.runTaskTimer(Foxtrot.getInstance(), 10, 10);
    }

    @Override
    public Category getCategory() {
        return Category.SEASONAL_CRATE;
    }

    @Override
    public String getDescription() {
        return "Created a cloud of particles! Fight with an advantage!";
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public Material getMaterial() {
        return Material.EMERALD;
    }

    @Override
    public String getDisplayName() {
        return ChatColor.DARK_RED.toString() + ChatColor.BOLD + "Phoenix Mode";
    }

    @Override
    public boolean inPartnerPackage() {
        return true;
    }
    @Override
    public List<String> getLore() {
        final List<String> toReturn = new ArrayList<>();

        toReturn.add("");
        toReturn.add(ChatColor.translate("&6❙ &fClick to create a cloud of particles."));
        toReturn.add(ChatColor.translate("&6❙ &fInside the cloud, enemies deal &c&l-15% &fdamage"));
        toReturn.add(ChatColor.translate("&6❙ &fto you and you deal &c&l+15% &fdamage to enemies."));
        toReturn.add("");
        toReturn.add(ChatColor.translate("&fCan be found in the &4&lCave Crate&f!"));

        return toReturn;
    }

    @Override
    public Boolean isAllowedAtLocation(Location location) {
        if (Foxtrot.getInstance().getMapHandler().isKitMap()) {
            return !DTRBitmask.KOTH.appliesAt(location) && !DTRBitmask.CONQUEST.appliesAt(location) && !DTRBitmask.CITADEL.appliesAt(location) && !DTRBitmask.DTC.appliesAt(location) && !DTRBitmask.SAFE_ZONE.appliesAt(location);
        }

        return !Foxtrot.getInstance().getServerHandler().isWarzone(location) && location.getWorld().getEnvironment() == World.Environment.NORMAL && !DTRBitmask.KOTH.appliesAt(location) && !DTRBitmask.CONQUEST.appliesAt(location) && !DTRBitmask.CITADEL.appliesAt(location) && !DTRBitmask.DTC.appliesAt(location) && !DTRBitmask.SAFE_ZONE.appliesAt(location);
    }

    @Override
    public long getCooldown() {
        return 120_000L;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onInteract(PlayerInteractEvent event) {
        if (!event.getAction().name().contains("RIGHT")) {
            return;
        }

        final Player player = event.getPlayer();

        final Location blockAt = player.getLocation();

        if (!this.isSimilar(event.getItem())) {
            return;
        }

        final AbilityUseEvent abilityUseEvent = new AbilityUseEvent(player, null, player.getLocation(), this, false);
        Foxtrot.getInstance().getServer().getPluginManager().callEvent(abilityUseEvent);

        if (abilityUseEvent.isCancelled()) {
            return;
        }

        event.setCancelled(true);

        if (player.getItemInHand().getAmount() == 1) {
            player.setItemInHand(null);
        } else {
            player.getItemInHand().setAmount(player.getItemInHand().getAmount()-1);
        }

        cache.put(player.getUniqueId(), player.getLocation().clone().add(0, 1, 0));

        Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
            cache.remove(player.getUniqueId());

            if (player.isOnline()) {
                player.sendMessage("");
                player.sendMessage(ChatColor.RED + "Your " + this.getDisplayName() + ChatColor.RED + " has expired!");
                player.sendMessage("");
            }
        }, 15*20);
        
        this.applyCooldown(player);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) {
            return;
        }

        final Player damager = (Player) event.getDamager();

        if (!cache.containsKey(damager.getUniqueId())) {
            return;
        }

        final Location location = cache.get(damager.getUniqueId());

        if (location.distance(damager.getLocation()) > 5) {
            return;
        }

        event.setDamage(event.getDamage()*1.15D);
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) {
            return;
        }

        final Player player = (Player) event.getEntity();

        if (!cache.containsKey(player.getUniqueId())) {
            return;
        }

        final Location location = cache.get(player.getUniqueId());

        if (location.distance(player.getLocation()) > 5) {
            return;
        }

        event.setDamage(event.getDamage()*0.85D);
    }

    public static List<Vector> getHollowCube(Location corner1, Location corner2) {
        List<Vector> result = new ArrayList<>();
        int minX = Math.min(corner1.getBlockX(), corner2.getBlockX());
        int minY = Math.min(corner1.getBlockY(), corner2.getBlockY());
        int minZ = Math.min(corner1.getBlockZ(), corner2.getBlockZ());
        int maxX = Math.max(corner1.getBlockX(), corner2.getBlockX());
        int maxY = Math.max(corner1.getBlockY(), corner2.getBlockY());
        int maxZ = Math.max(corner1.getBlockZ(), corner2.getBlockZ());

        // 2 areas
        for(int x = minX; x <= maxX; x++) {
            for(int z = minZ; z <= maxZ; z++) {
                result.add(new Vector(x, minY, z));
                result.add(new Vector(x, maxY, z));
            }
        }

        // 2 sides (front & back)
        for(int x = minX; x <= maxX; x++) {
            for(int y = minY; y <= maxY; y++) {
                result.add(new Vector(x, y, minZ));
                result.add(new Vector(x, y, maxZ));
            }
        }

        // 2 sides (left & right)
        for(int z = minZ; z <= maxZ; z++) {
            for(int y = minY; y <= maxY; y++) {
                result.add(new Vector(minX, y, z));
                result.add(new Vector(maxX, y, z));
            }
        }

        return result;
    }
}