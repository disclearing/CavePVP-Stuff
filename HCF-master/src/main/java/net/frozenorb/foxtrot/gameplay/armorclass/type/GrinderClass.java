package net.frozenorb.foxtrot.gameplay.armorclass.type;

import cc.fyre.proton.util.ItemBuilder;
import cc.fyre.proton.util.TimeUtils;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.armorclass.ArmorClass;
import net.frozenorb.foxtrot.gameplay.armorclass.ArmorClassHandler;
import net.frozenorb.foxtrot.gameplay.armorclass.ArmorPiece;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class GrinderClass extends ArmorClass {

    @Override
    public String getId() {
        return "grinder";
    }

    @Override
    public String getDisplayName() {
        return ChatColor.GREEN + ChatColor.BOLD.toString() + "Grinder";
    }

    @Override
    public int getSlot() {
        return 11;
    }

    @Override
    public Material getDisplayItem() {
        return Material.EXP_BOTTLE;
    }

    @Override
    public ChatColor getChatColor() {
        return ChatColor.GREEN;
    }

    @Override
    public List<String> getPerks() {
        final List<String> toReturn = new ArrayList<>();

        toReturn.add("2x permanent XP multiplier");

        return toReturn;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onDamage(PlayerExpChangeEvent event) {
        final Player player = event.getPlayer();

        if (!isWearing(player)) {
            return;
        }

        if (!ArmorClassHandler.isAllowed(player.getLocation())) {
            return;
        }

        event.setAmount(event.getAmount()*3);
    }

    @Override
    public ItemStack createPiece(ArmorPiece armorPiece) {
        final ItemBuilder itemBuilder = ItemBuilder.of(armorPiece.getDefaultMaterial())
                .enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2)
                .enchant(Enchantment.DURABILITY, 3)
                .name(getDisplayName() + " " + getPieceName(armorPiece))
                .addToLore("", getChatColor() + "Armor Class: &f" + ChatColor.stripColor(getDisplayName()), getChatColor() + "Perks:");
        for (String perk : this.getPerks()) {
            itemBuilder.addToLore(getChatColor() + "❙ &f" + perk);
        }
        itemBuilder.addToLore("");

        if (armorPiece.getDefaultMaterial().equals(Material.DIAMOND_CHESTPLATE)) {
            itemBuilder.addToLore("&cFireResistance I");
        }

        if (armorPiece.getDefaultMaterial().equals(Material.DIAMOND_LEGGINGS)) {
            itemBuilder.addToLore("&cImplants IV");
        }

        if (armorPiece.getDefaultMaterial().equals(Material.DIAMOND_BOOTS)) {
            itemBuilder.enchant(Enchantment.PROTECTION_FALL, 4);
            itemBuilder.addToLore("&cSpeed II");
        }

        return itemBuilder.build();
    }
}
