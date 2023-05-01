package net.frozenorb.foxtrot.gameplay.armorclass.type;

import cc.fyre.proton.util.ItemBuilder;
import cc.fyre.proton.util.TimeUtils;
import com.sun.org.apache.bcel.internal.generic.LOR;
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
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class PyromaniacClass extends ArmorClass {
    private Map<UUID, Long> cooldown = new HashMap<>();

    @Override
    public String getId() {
        return "pyromaniac";
    }

    @Override
    public String getDisplayName() {
        return ChatColor.RED + ChatColor.BOLD.toString() + "Pyromaniac";
    }

    @Override
    public int getSlot() {
        return 12;
    }

    @Override
    public Material getDisplayItem() {
        return Material.FIREBALL;
    }

    @Override
    public ChatColor getChatColor() {
        return ChatColor.RED;
    }

    @Override
    public List<String> getPerks() {
        final List<String> toReturn = new ArrayList<>();

        toReturn.add("SHIFT + Click to throw shockwaves");
        toReturn.add("1% chance of putting them on Fire and giving them Wither I for 8 seconds.");

        return toReturn;
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        final Player player = (Player) event.getEntity();

        if (!this.isWearing(player)) {
            return;
        }

        if (!ArmorClassHandler.isAllowed(player.getLocation())) {
            return;
        }

        if (event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
            event.setDamage(event.getDamage() * 1.1);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onInteract(PlayerInteractEvent event) {
        if (!event.getAction().name().contains("RIGHT")) {
            return;
        }

        final Player player = event.getPlayer();

        if (!this.isWearing(player)) {
            return;
        }

        if (!player.isSneaking()) {
            return;
        }

        if (!ArmorClassHandler.isAllowed(player.getLocation())) {
            return;
        }

        if (cooldown.containsKey(player.getUniqueId()) && cooldown.get(player.getUniqueId()) > System.currentTimeMillis()) {
            long millisLeft = ((cooldown.get(player.getUniqueId()) - System.currentTimeMillis()) / 1000L) * 1000L;
            String msg = TimeUtils.formatIntoDetailedString((int) (millisLeft / 1000));

            player.sendMessage(ChatColor.RED + "You cannot use this for another §c§l" + msg + "§c.");
            return;
        }

        final Fireball fireball = player.launchProjectile(Fireball.class);

        fireball.setMetadata("SHOCK_WAVE", new FixedMetadataValue(Foxtrot.getInstance(), player.getUniqueId().toString()));
        fireball.setIsIncendiary(false);
        fireball.setYield(0.0f);
        fireball.setShooter(player);

        cooldown.put(player.getUniqueId(), System.currentTimeMillis()+TimeUnit.MINUTES.toMillis(5));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Player)) {
            return;
        }

        final Player damager = (Player) event.getDamager();

        if (!isWearing(damager)) {
            return;
        }

        if (!ArmorClassHandler.isAllowed(damager.getLocation())) {
            return;
        }

        final Player target = (Player) event.getEntity();

        if (ThreadLocalRandom.current().nextInt(1, 200) == 38) {
            target.sendMessage(ChatColor.translate(damager.getName() + " &chas used the Butcher Armor Class ability on you!"));

            target.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20*6, 0));
        }
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

        if (armorPiece.getDefaultMaterial().equals(Material.DIAMOND_BOOTS)) {
            itemBuilder.enchant(Enchantment.PROTECTION_FALL, 4);
            itemBuilder.addToLore("&cSpeed II");
        }

        return itemBuilder.build();
    }

    @EventHandler
    private void onLand(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Fireball) || !(event.getEntity().getShooter() instanceof Player) || !event.getEntity().hasMetadata("SHOCK_WAVE")) {
            return;
        }

        final Projectile fireBall = event.getEntity();
        final Player player = (Player) fireBall.getShooter();

        fireBall.getNearbyEntities(10, 10, 10).stream().filter(it -> it instanceof Player && this.isAllowedAtLocation(it.getLocation())).forEach(it -> {
            it.setVelocity(it.getLocation().getDirection().multiply(-2.5));

            ((Player) it).setHealth(((Player) it).getHealth() - 1);

            ((Player) it).sendMessage("");
            ((Player) it).sendMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Ability Items");
            ((Player) it).sendMessage(ChatColor.GRAY + "You have been hit with the " + this.getDisplayName() + ChatColor.GRAY + "!");
            ((Player) it).sendMessage(ChatColor.RED + "You have been sent flying in the air!");
            ((Player) it).sendMessage("");
        });
    }
}
