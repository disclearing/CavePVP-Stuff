package net.frozenorb.foxtrot.gameplay.armorclass.type;

import cc.fyre.proton.util.ItemBuilder;
import cc.fyre.proton.util.TimeUtils;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.AbilityHandler;
import net.frozenorb.foxtrot.gameplay.ability.type.NinjaStar;
import net.frozenorb.foxtrot.gameplay.armorclass.ArmorClass;
import net.frozenorb.foxtrot.gameplay.armorclass.ArmorClassHandler;
import net.frozenorb.foxtrot.gameplay.armorclass.ArmorPiece;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
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

public class TrapperClass extends ArmorClass {
    private Map<UUID, Long> cooldown = new HashMap<>();

    @Override
    public String getId() {
        return "trapper";
    }

    @Override
    public String getDisplayName() {
        return ChatColor.DARK_PURPLE + ChatColor.BOLD.toString() + "Trapper";
    }

    @Override
    public int getSlot() {
        return 15;
    }

    @Override
    public Material getDisplayItem() {
        return Material.COBBLESTONE;
    }

    @Override
    public ChatColor getChatColor() {
        return ChatColor.DARK_PURPLE;
    }

    @Override
    public List<String> getPerks() {
        final List<String> toReturn = new ArrayList<>();

        toReturn.add("Take 10% less damage");
        toReturn.add("Med Kits and Portable Resistance last 25% longer");
        toReturn.add("SHIFT + Click to put the last player you hit on Ability Item cooldown");

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
            event.setDamage(event.getDamage() * 0.9);
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

        final long difference = TimeUnit.SECONDS.toMillis(30L);

        if (!NinjaStar.cache.containsKey(player.getUniqueId()) || (System.currentTimeMillis() - NinjaStar.cache.get(player.getUniqueId()).getTime()) > difference) {
            player.sendMessage(ChatColor.RED + "No player has hit you within the last 30 seconds.");
            return;
        }

        final NinjaStar.LastDamageEntry lastDamageEntry = NinjaStar.cache.get(player.getUniqueId());
        final Player target = Foxtrot.getInstance().getServer().getPlayer(lastDamageEntry.getUuid());

        final AbilityHandler abilityHandler = Foxtrot.getInstance().getMapHandler().getAbilityHandler();
        abilityHandler.getGlobalCooldowns().put(target.getUniqueId(), System.currentTimeMillis()+TimeUnit.SECONDS.toMillis(15));

        cooldown.put(player.getUniqueId(), System.currentTimeMillis()+TimeUnit.MINUTES.toMillis(5));
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
}
