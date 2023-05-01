package net.frozenorb.foxtrot.gameplay.ability.type;

import cc.fyre.proton.util.TimeUtils;
import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.Ability;
import net.frozenorb.foxtrot.gameplay.ability.Category;
import net.frozenorb.foxtrot.gameplay.ability.listener.events.AbilityUseEvent;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class CraftingChaos extends Ability {

    @Getter
    public static Map<UUID, Long> cache = new HashMap<>();

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public Material getMaterial() {
        return Material.WORKBENCH;
    }

    @Override
    public String getDisplayName() {
        return ChatColor.DARK_AQUA.toString() + ChatColor.BOLD + "Crafting Chaos";
    }

    @Override
    public List<String> getLore() {
        final List<String> toReturn = new ArrayList<>();

        toReturn.add("");
        toReturn.add(ChatColor.translate("&6❙ &fHit an enemy and for &e&l10 seconds&f, every"));
        toReturn.add(ChatColor.translate("&6❙ &fhit you deal has a &c&l15% &fchance of"));
        toReturn.add(ChatColor.translate("&6❙ &fputting them in a crafting table."));
        toReturn.add("");
        toReturn.add(ChatColor.translate("&fCan be found in the &3&lHeaded Crate&f!"));

        return toReturn;
    }

    @Override
    public Category getCategory() {
        return Category.TREASURE_CHEST;
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public boolean inPartnerPackage() {
        return true;
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
        return 90_000L;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onDamage(EntityDamageByEntityEvent event) {
        if (event.isCancelled() || !(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Player)) {
            return;
        }

        final Player target = (Player) event.getEntity();
        final Player damager = (Player) event.getDamager();

        if (damager.getItemInHand() == null || !this.isSimilar(damager.getItemInHand())) {
            return;
        }

        final AbilityUseEvent abilityUseEvent = new AbilityUseEvent(damager, target, damager.getLocation(), this, false);

        int value = target.hasMetadata("CHAOS") ? (target.getMetadata("CHAOS").get(0).asInt()+1) : 1;

        if (cache.containsKey(target.getUniqueId())) {
            damager.sendMessage(ChatColor.translate("&c" + target.getName() +  " is already in the crafting chaos for &l" + TimeUtils.formatIntoMMSS((int) (cache.get(target.getUniqueId())-System.currentTimeMillis())/1000) + "&c."));
            return;
        }

        if (value != 3) {
            abilityUseEvent.setOneHit(true);
        }

        Foxtrot.getInstance().getServer().getPluginManager().callEvent(abilityUseEvent);

        if (abilityUseEvent.isCancelled()) {
            return;
        }

        target.setMetadata("CHAOS", new FixedMetadataValue(Foxtrot.getInstance(), value));

        if (value != 3) {
            damager.sendMessage(CC.translate("&cYou have to hit &f" + target.getName() + " &c" + (3 - value) + " more times!"));
            return;
        }

        target.removeMetadata("CHAOS", Foxtrot.getInstance());

        cache.put(target.getUniqueId(), System.currentTimeMillis()+TimeUnit.SECONDS.toMillis(10));

        if (damager.getItemInHand().getAmount() == 1) {
            damager.setItemInHand(null);
        } else {
            damager.getItemInHand().setAmount(damager.getItemInHand().getAmount()-1);
        }

        this.fullDescription = "Each hit has a chance of putting " + target.getName() + " in a crafting table.";

        target.sendMessage("");
        target.sendMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Ability Items");
        target.sendMessage(ChatColor.GRAY + "You have been hit with the " + this.getDisplayName() + ChatColor.RED + ".");
        target.sendMessage(ChatColor.RED + "For the next 10 seconds, every hit you take has a 10% chance of putting you in a crafting table.");
        target.sendMessage("");

        this.applyCooldown(damager);
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onPlayerHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Player)) {
            return;
        }

        final Player target = (Player) event.getEntity();
        final Player damager = (Player) event.getDamager();

        if (!cache.containsKey(target.getUniqueId()) || cache.get(target.getUniqueId()) < System.currentTimeMillis()) {
            cache.remove(target.getUniqueId());
            return;
        }

        if (ThreadLocalRandom.current().nextInt(100) <= 15) {
            damager.playSound(damager.getLocation(), Sound.LEVEL_UP, 1, 1);
            damager.sendMessage(ChatColor.RED + target.getName() + " has been put in a crafting table due to the " + this.getDisplayName() + "!");

            target.playSound(target.getLocation(), Sound.ZOMBIE_WOODBREAK, 1, 1);
            target.sendMessage(ChatColor.RED + "You were placed in a crafting table due to the " + this.getDisplayName() + ChatColor.RED + "!");
            target.openWorkbench(target.getLocation(), true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onPlace(BlockPlaceEvent event) {
        final Player player = event.getPlayer();

        if (this.isSimilar(event.getItemInHand())) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You can't place partner items!");
        }

    }

}
