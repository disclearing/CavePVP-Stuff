package net.frozenorb.foxtrot.gameplay.ability.type;

import cc.fyre.neutron.Neutron;
import cc.fyre.proton.Proton;
import cc.fyre.proton.util.TimeUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.Ability;
import net.frozenorb.foxtrot.gameplay.ability.Category;
import net.frozenorb.foxtrot.gameplay.ability.listener.events.AbilityUseEvent;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.*;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class RealityStone extends Ability {
    @Override
    public Category getCategory() {
        return Category.TREASURE_CHEST;
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Getter
    private Map<UUID,LastDamageEntry> cache = new HashMap<>();

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public Material getMaterial() {
        return Material.REDSTONE;
    }

    @Override
    public String getDisplayName() {
        return ChatColor.RED.toString() + ChatColor.BOLD + "Reality Stone";
    }

    @Override
    public List<String> getLore() {
        final List<String> toReturn = new ArrayList<>();

        toReturn.add("");
        toReturn.add(ChatColor.translate("&6❙ &fRight Click to teleport to the player"));
        toReturn.add(ChatColor.translate("&6❙ &fthat you last hit within a &e&l30 second &fperiod."));
        toReturn.add("");
        toReturn.add(ChatColor.translate("&fCan be found in the &6&lOctober Mystery Box&f!"));

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
        return 300_000L;
    }

    @EventHandler
    private void onInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final long difference = TimeUnit.SECONDS.toMillis(30L);

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) {
            return;
        }

        if (player.getItemInHand() == null || !this.isSimilar(player.getItemInHand())) {
            return;
        }

        final AbilityUseEvent abilityUseEvent = new AbilityUseEvent(player, null, player.getLocation(), this, false);
        Foxtrot.getInstance().getServer().getPluginManager().callEvent(abilityUseEvent);

        if (abilityUseEvent.isCancelled()) {
            return;
        }

        if (!this.cache.containsKey(player.getUniqueId()) || (System.currentTimeMillis() - this.cache.get(player.getUniqueId()).getTime()) > difference) {
            player.sendMessage(ChatColor.RED + "You have not hit a player in the last 30 seconds.");
            return;
        }

        if (!player.hasMetadata("NO_COOLDOWN") && NinjaStar.teleportCooldowns.containsKey(player.getUniqueId()) && NinjaStar.teleportCooldowns.get(player.getUniqueId()) > System.currentTimeMillis()) {
            long remaining = NinjaStar.teleportCooldowns.get(player.getUniqueId())-System.currentTimeMillis();

            player.sendMessage(ChatColor.translate("&cYou cannot use a &b&lTeleportation Item &cfor another &l" + TimeUtils.formatIntoDetailedString((int) (remaining/1000)) + "&c."));
            return;
        }

        final LastDamageEntry entry = cache.get(player.getUniqueId());

        final Player target = Foxtrot.getInstance().getServer().getPlayer(entry.getUuid());

        if (target.isOnline()) {

            target.setMetadata("REALITY_STONE", new FixedMetadataValue(Foxtrot.getInstance(), true));
            target.sendMessage(ChatColor.RED + "You may not use an enderpearl or timewarp for the next 5 seconds...");

            Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
                target.removeMetadata("REALITY_STONE", Foxtrot.getInstance());
            }, 20*5);
        }

        this.fullDescription = "Teleporting to &f" + target.getName() + " &cin 3 seconds...";

        new BukkitRunnable() {
            private int seconds = 3;

            @Override
            public void run() {

                if (!event.getPlayer().isOnline()) {
                    this.cancel();
                    return;
                }
                final Player target = Foxtrot.getInstance().getServer().getPlayer(entry.getUuid());

                if (Foxtrot.getInstance().getDeathbanArenaHandler().isDeathbanArena(target)) {
                    refund(player);
                    player.sendMessage(ChatColor.RED + "Cancelled the process as that player is in the Deathban Arena!");
                    this.cancel();
                    return;
                }

                if (this.seconds < 1) {

                    final Location location = (target != null && target.isOnline()) ? target.getLocation():entry.getLocation();

                    event.getPlayer().teleport(location);

                    this.cancel();
                    return;
                }

                if (Foxtrot.getInstance().getDeathbanArenaHandler().isDeathbanArena(player)) {
                    this.cancel();
                    player.sendMessage(ChatColor.translate("&cYou may not use &f" + getDisplayName() + " &cwhile deathbanned!"));
                    return;
                }

                this.seconds--;

                event.getPlayer().sendMessage(ChatColor.YELLOW + "Teleporting to " + ChatColor.WHITE + Proton.getInstance().getUuidCache().name(entry.getUuid()) + ChatColor.YELLOW + " in " + ChatColor.RED + (this.seconds+1) + ChatColor.YELLOW + " second" + (this.seconds == 1 ? "":"s") + "...");

                if (target != null && target.isOnline()) {
                    target.sendMessage(Neutron.getInstance().getProfileHandler().findDisplayName(player.getUniqueId()) + ChatColor.RED + " will teleport to you in " + ChatColor.WHITE + (this.seconds+1) + ChatColor.RED + " second" + (this.seconds == 1 ? "":"s") + ".");
                }
            }

        }.runTaskTimer(Foxtrot.getInstance(),0L,20L);

        if (player.getItemInHand().getAmount() == 1) {
            player.setItemInHand(null);
        } else {
            player.getItemInHand().setAmount(player.getItemInHand().getAmount()-1);
        }

        this.applyCooldown(player);
    }

    public void refund(Player player) {
        player.getInventory().addItem(this.hassanStack.clone());

        this.removeCooldown(player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onPearl(ProjectileLaunchEvent event) {
        if (!(event.getEntity() instanceof EnderPearl)) {
            return;
        }

        if (!(event.getEntity().getShooter() instanceof Player)) {
            return;
        }

        final Player shooter = (Player) event.getEntity().getShooter();

        if (shooter.hasMetadata("REALITY_STONE")) {
            event.setCancelled(true);
            shooter.updateInventory();
            shooter.sendMessage(ChatColor.RED + "You may not throw enderpearls whilst someone is using a " + this.getDisplayName() + ChatColor.RED + " on you!");
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onEntityDamageByEntity(EntityDamageByEntityEvent event) {

        if (event.isCancelled()) {
            return;
        }

        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) {
            return;
        }

        this.cache.put(event.getDamager().getUniqueId(),new LastDamageEntry(System.currentTimeMillis(),event.getEntity().getUniqueId(),event.getEntity().getLocation()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onEntityDamageByProjectile(EntityDamageByEntityEvent event) {

        if (event.isCancelled() || event.getDamager() instanceof EnderPearl) {
            return;
        }

        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Projectile)) {
            return;
        }

        if (!(((Projectile) event.getDamager()).getShooter() instanceof Player)) {
            return;
        }

        final Player damager = (Player) ((Projectile) event.getDamager()).getShooter();

        this.cache.put(damager.getUniqueId(),new LastDamageEntry(System.currentTimeMillis(),event.getEntity().getUniqueId(),event.getEntity().getLocation()));
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onDeath(PlayerDeathEvent event) {
        this.cache.remove(event.getEntity().getUniqueId());

        final Optional<Map.Entry<UUID, LastDamageEntry>> optionalLastDamageEntry = this.cache.entrySet().stream().filter(it -> it.getValue().getUuid().toString().equalsIgnoreCase(event.getEntity().getUniqueId().toString())).findFirst();

        if (!optionalLastDamageEntry.isPresent()) {
            return;
        }

        this.cache.remove(optionalLastDamageEntry.get().getKey(), optionalLastDamageEntry.get().getValue());
    }

    @AllArgsConstructor
    static class LastDamageEntry {

        @Getter
        private long time;
        @Getter
        private UUID uuid;
        @Getter
        private Location location;

    }
}