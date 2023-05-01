package net.frozenorb.foxtrot.gameplay.ability.type;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.Ability;
import net.frozenorb.foxtrot.gameplay.ability.Category;
import net.frozenorb.foxtrot.gameplay.ability.listener.events.AbilityUseEvent;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class Combo extends Ability {

    public static Map<UUID, Integer> cache = new HashMap<>();

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public Material getMaterial() {
        return Material.RED_ROSE;
    }

    @Override
    public String getDisplayName() {
        return ChatColor.GREEN.toString() + ChatColor.BOLD + "Combo Ability";
    }

    @Override
    public List<String> getLore() {
        final List<String> toReturn = new ArrayList<>();

        toReturn.add("");
        toReturn.add(ChatColor.translate("&6❙ &fGet a second of strength II for the"));
        toReturn.add(ChatColor.translate("&6❙ &famount of hits dealt within &e&l10 seconds&f."));
        toReturn.add("");
        toReturn.add(ChatColor.translate("&fCan be found in the &4&lCave Crate&f!"));

        return toReturn;
    }

    @Override
    public Category getCategory() {
        return Category.PARTNER_CRATE;
    }

    @Override
    public String getDescription() {
        return "Each hit will grant you a second of Strength II!";
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

    @EventHandler(priority = EventPriority.LOWEST)
    private void onInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();

        if (!this.isSimilar(player.getItemInHand()) || event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        event.setCancelled(true);

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
        player.updateInventory();

        cache.put(player.getUniqueId(), 0);

        this.applyCooldown(event.getPlayer());

        Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {

            int seconds = cache.remove(player.getUniqueId());

            if (!player.isOnline()) {
                return;
            }

            player.playSound(player.getLocation(), Sound.EXPLODE, 1, 1);
            player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20*seconds, 1), true);

            player.sendMessage("");
            player.sendMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Ability Items");
            player.sendMessage(ChatColor.GRAY + "Your " + this.getDisplayName() + ChatColor.GRAY + " has been activated!");
            player.sendMessage(ChatColor.RED + "You have been given a total of " + seconds + " seconds of Strength II!");
            player.sendMessage("");
        }, 20*10L);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onDamage(EntityDamageByEntityEvent event) {
        if (event.isCancelled() || !(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) {
            return;
        }

        final Player damager = (Player) event.getDamager();

        if (!cache.containsKey(damager.getUniqueId())) {
            return;
        }

        int amount = cache.getOrDefault(damager.getUniqueId(), 0)+1;

        if (amount > 10) {
            return;
        }

        if (!Foxtrot.getInstance().getAbilityCooldownsScoreboardMap().isScoreboard(damager.getUniqueId())) {
            damager.sendMessage(ChatColor.RED + "Found another hit, you will get " + amount + " seconds of Strength II.");
        }

        cache.put(damager.getUniqueId(), amount);
    }
}