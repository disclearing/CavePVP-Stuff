package net.frozenorb.foxtrot.team.upgrade.effects;

import lombok.AllArgsConstructor;
import lombok.Getter;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
public enum PurchaseableEffects {
    SPEED(ChatColor.AQUA + ChatColor.BOLD.toString() + "Speed II"
            , Arrays.asList(ChatColor.GRAY + "Purchase the Speed II", ChatColor.GRAY + "passive effect for your claim."), Material.SUGAR, 11, 15, PotionEffectType.SPEED),
    RESISTANCE(ChatColor.WHITE + ChatColor.BOLD.toString() + "Resistance I"
            , Arrays.asList(ChatColor.GRAY + "Purchase the Resistance I", ChatColor.GRAY + "passive effect for your claim."), Material.IRON_INGOT, 12, 25, PotionEffectType.DAMAGE_RESISTANCE),
    STRENGTH(ChatColor.RED + ChatColor.BOLD.toString() + "Strength I"
            , Arrays.asList(ChatColor.GRAY + "Purchase Strength I", ChatColor.GRAY + "passive effect for your claim."), Material.BLAZE_POWDER, 13, 50, PotionEffectType.INCREASE_DAMAGE),
    FIRE_RESISTANCE(ChatColor.GOLD + ChatColor.BOLD.toString() + "Fire Resistance I"
            , Arrays.asList(ChatColor.GRAY + "Purchase the Fire Resistance I", ChatColor.GRAY + "passive effect for your claim."), Material.MAGMA_CREAM, 14, 10, PotionEffectType.FIRE_RESISTANCE),
    REGENERATION(ChatColor.RED + ChatColor.BOLD.toString() + "Regeneration I",
            Arrays.asList(ChatColor.GRAY + "Purchase the Regeneration I", ChatColor.GRAY + "passive effect for your claim."), Material.GHAST_TEAR, 15, 25, PotionEffectType.REGENERATION);

    @Getter String displayName;
    @Getter List<String> description;
    @Getter Material material;
    @Getter int slot;
    @Getter int cost;
    @Getter PotionEffectType potionEffectType;
}