package net.frozenorb.foxtrot.gameplay.armorclass.type;

import cc.fyre.proton.util.ItemBuilder;
import cc.fyre.proton.util.TimeUtils;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.armorclass.ArmorClass;
import net.frozenorb.foxtrot.gameplay.armorclass.ArmorClassHandler;
import net.frozenorb.foxtrot.gameplay.armorclass.ArmorPiece;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class SpidermanClass extends ArmorClass {
    private Map<UUID, Long> cooldown = new HashMap<>();

    @Override
    public String getId() {
        return "spiderman";
    }

    @Override
    public String getDisplayName() {
        return ChatColor.RED + ChatColor.BOLD.toString() + "Spider Man";
    }

    @Override
    public int getSlot() {
        return 14;
    }

    @Override
    public Material getDisplayItem() {
        return Material.WEB;
    }

    @Override
    public ChatColor getChatColor() {
        return ChatColor.RED;
    }

    @Override
    public List<String> getPerks() {
        final List<String> toReturn = new ArrayList<>();

        toReturn.add("25% Fall Damage Reduction");
        toReturn.add("Permanent Jump Boost II, Speed II, Fire Resistance I");
        toReturn.add("SHIFT + Click to put the last player you hit in cobwebs!");

        return toReturn;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onDamage(EntityDamageEvent event) {
        if (!event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
            return;
        }

        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        final Player damager = (Player) event.getEntity();

        if (!this.isWearing(damager)) {
            return;
        }

        if (!ArmorClassHandler.isAllowed(damager.getLocation())) {
            return;
        }

        event.setDamage(event.getDamage()*0.75);
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

        if (!ArmorClassHandler.isAllowed(player.getLocation())) {
            return;
        }

        if (!player.isSneaking()) {
            return;
        }

        if (cooldown.containsKey(player.getUniqueId()) && cooldown.get(player.getUniqueId()) > System.currentTimeMillis()) {
            long millisLeft = ((cooldown.get(player.getUniqueId()) - System.currentTimeMillis()) / 1000L) * 1000L;
            String msg = TimeUtils.formatIntoDetailedString((int) (millisLeft / 1000));

            player.sendMessage(ChatColor.RED + "You cannot use this for another §c§l" + msg + "§c.");
            return;
        }

        player.playSound(player.getLocation(), Sound.ZOMBIE_WOODBREAK, 1, 1);

        final Block blockAt = player.getLocation().getBlock();

        setCobweb(blockAt);
        setCobweb(blockAt.getRelative(BlockFace.UP));
        setCobweb(blockAt.getRelative(BlockFace.EAST));
        setCobweb(blockAt.getRelative(BlockFace.EAST).getRelative(BlockFace.UP));
        setCobweb(blockAt.getRelative(BlockFace.NORTH));
        setCobweb(blockAt.getRelative(BlockFace.NORTH).getRelative(BlockFace.UP));

        cooldown.put(player.getUniqueId(), System.currentTimeMillis()+TimeUnit.MINUTES.toMillis(5));
    }

    public void setCobweb(Block block) {
        if (!block.getType().equals(Material.AIR)) {
            return;
        }

        block.setType(Material.WEB);

        Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> block.setType(Material.AIR), 20*10);
    }

    @Override
    public void apply(Player player) {
        player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 1));
    }

    @Override
    public void unapply(Player player) {
        player.removePotionEffect(PotionEffectType.JUMP);
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
