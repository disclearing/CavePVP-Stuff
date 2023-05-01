package net.frozenorb.foxtrot.gameplay.ability.type.kitmap;

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
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SharkBite extends Ability {

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public Material getMaterial() {
        return Material.SHEARS;
    }

    @Override
    public String getDisplayName() {
        return ChatColor.DARK_AQUA.toString() + ChatColor.BOLD + "Shark Bite";
    }

    @Override
    public List<String> getLore() {
        final List<String> toReturn = new ArrayList<>();

        toReturn.add("");
        toReturn.add(ChatColor.translate("&6❙ &fHit an opponent to deal a bleeding"));
        toReturn.add(ChatColor.translate("&6❙ &feffect for 10 seconds"));
        toReturn.add("");
        toReturn.add(ChatColor.translate("&fCan be found in an &d&l???&f!"));

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
    public boolean inPartnerPackage() {
        return true;
    }

    @Override
    public long getCooldown() {
        return 120_000L;
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
        Foxtrot.getInstance().getServer().getPluginManager().callEvent(abilityUseEvent);

        if (abilityUseEvent.isCancelled()) {
            return;
        }

        if (damager.getItemInHand().getAmount() == 1) {
            damager.setItemInHand(null);
        } else {
            damager.getItemInHand().setAmount(damager.getItemInHand().getAmount() - 1);
        }

        AtomicInteger ticks = new AtomicInteger(0);

        new BukkitRunnable() {

            @Override
            public void run() {
                if (!target.isOnline() || target.isDead() || ticks.incrementAndGet() == 10) {
                    cancel();
                } else {
                    target.damage(0.5);
                    target.getWorld().playEffect(target.getEyeLocation(), Effect.STEP_SOUND, Material.REDSTONE_BLOCK);
                }
            }
        }.runTaskTimer(Foxtrot.getInstance(), 20, 20);

        fullDescription = target.getName() + " will now be bitten every second for the next 10 seconds!";

        target.sendMessage("");
        target.sendMessage(CC.translate("&4" + damager.getName() + " &chas hit you with " + this.getDisplayName() + "&c!"));
        target.sendMessage("");

        this.applyCooldown(damager);
    }

    @Override
    public Category getCategory() {
        return Category.KIT_MAP;
    }

    @Override
    public String getDescription() {
        return "";
    }


}