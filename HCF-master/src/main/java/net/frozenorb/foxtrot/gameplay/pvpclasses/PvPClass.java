package net.frozenorb.foxtrot.gameplay.pvpclasses;

import net.minecraft.util.com.google.common.collect.HashBasedTable;
import net.minecraft.util.com.google.common.collect.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionEffectExpireEvent;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Getter
public abstract class PvPClass implements Listener {

    private final String name;
    private final List<Material> consumables;
    private boolean energyBased;
    private boolean warmup;
    private final List<PotionEffectType> permanentEffects = new ArrayList<>();

    private static final Table<UUID, PotionEffectType, PotionEffect> restores = HashBasedTable.create();

    public PvPClass(String name, List<Material> consumables) {
        this.name = name;
        this.consumables = consumables;
    }

    public PvPClass(String name, List<Material> consumables, boolean energyBased) {
        this.name = name;
        this.consumables = consumables;
        this.energyBased = energyBased;
    }

    public void apply(Player player) {
        if (energyBased) {
            Foxtrot.getInstance().getPvpClassHandler().getEnergyService().getCache().put(player.getUniqueId(), 0F);
        }
    }

    public void tick(Player player) {
    }

    public void remove(Player player) {
        if (energyBased) {
            Foxtrot.getInstance().getPvpClassHandler().getEnergyService().getCache().remove(player.getUniqueId());
            Foxtrot.getInstance().getPvpClassHandler().getEnergyService().getLastEffectUsage().remove(player.getUniqueId());
        }
    }

    public boolean canApply(Player player) {
        return true;
    }

    public static void removeInfiniteEffects(Player player) {
        for (PotionEffect potionEffect : player.getActivePotionEffects()) {
            if (potionEffect.getDuration() > 1_000_000) {
                player.removePotionEffect(potionEffect.getType());
            }
        }
    }

    public boolean itemConsumed(Player player, Material type) {
        return true;
    }

    public abstract boolean qualifies(Player player, PlayerInventory armor);

    protected boolean wearingAllArmor(PlayerInventory armor) {
        return armor.getHelmet() != null &&
                armor.getChestplate() != null &&
                armor.getLeggings() != null &&
                armor.getBoots() != null;
    }

    public static void smartAddPotion(final Player player, PotionEffect potionEffect, boolean persistOldValues, PvPClass pvpClass) {
        setRestoreEffect(player, potionEffect);
    }

    @Getter
    @AllArgsConstructor
    public static class SavedPotion {
        private PotionEffect potionEffect;
        private long time;
        private boolean perm;
    }

    public static void setRestoreEffect(Player player, PotionEffect effect) {
        boolean shouldCancel = true;
        Collection<PotionEffect> activeList = player.getActivePotionEffects();

        for (PotionEffect active : activeList) {
            if (!active.getType().equals(effect.getType())) continue;

            // If the current potion effect has a higher amplifier, ignore this one.
            if (effect.getAmplifier() < active.getAmplifier()) {
                return;
            } else if (effect.getAmplifier() == active.getAmplifier()) {
                // If the current potion effect has a longer duration, ignore this one.
                if (0 < active.getDuration() && (effect.getDuration() <= active.getDuration() || effect.getDuration() - active.getDuration() < 10)) {
                    return;
                }
            }

            restores.put(player.getUniqueId(), active.getType(), active);
            shouldCancel = false;
            break;
        }

        // Cancel the previBous restore.
        player.addPotionEffect(effect, true);
        if (shouldCancel && effect.getDuration() > 120 && effect.getDuration() < 9600) {
            restores.remove(player.getUniqueId(), effect.getType());
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPotionEffectExpire(PotionEffectExpireEvent event) {
        LivingEntity livingEntity = event.getEntity();
        if (livingEntity instanceof Player) {
            Player player = (Player) livingEntity;

            if (PvPClassHandler.getPvPClass(player) == null && event.getEffect().getType().getName().equalsIgnoreCase("Speed")) {
                if (player.getInventory().getBoots() != null && player.getInventory().getBoots().getItemMeta().hasLore() && player.getInventory().getBoots().getItemMeta().getLore().contains(ChatColor.RED + "Speed II")) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1), true);
                }
            }

            PotionEffect previous = restores.remove(player.getUniqueId(), event.getEffect().getType());

            if (previous != null && previous.getDuration() < 1_000_000) {
                event.setCancelled(true);
                player.addPotionEffect(previous, true);
                Bukkit.getLogger().info("Restored " + previous.getType().toString() + " for " + player.getName() + ". duration: " + previous.getDuration() + ". amp: " + previous.getAmplifier());
            }
        }
    }
}
