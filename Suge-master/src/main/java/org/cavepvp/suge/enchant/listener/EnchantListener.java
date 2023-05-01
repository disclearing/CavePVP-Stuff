package org.cavepvp.suge.enchant.listener;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.EquipmentSetEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cavepvp.suge.Suge;
import org.cavepvp.suge.enchant.data.CustomEnchant;
import org.cavepvp.suge.enchant.data.type.CubedEnchant;
import org.cavepvp.suge.enchant.data.type.DiskEnchant;

import java.util.*;

public class EnchantListener implements Listener {
    private Suge instance;
    private List<Material> ignoreMaterials = Arrays.asList(Material.BEDROCK, Material.CHEST,
            Material.ENDER_CHEST, Material.TRAPPED_CHEST, Material.OBSIDIAN, Material.MOB_SPAWNER);

    public EnchantListener(Suge instance) {
        this.instance = instance;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onBreak(BlockBreakEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final Player player = event.getPlayer();
        final Block block = event.getBlock();

        final ItemStack itemStack = event.getPlayer().getItemInHand();

        if (itemStack == null) {
            return;
        }

        if (!itemStack.getType().equals(Material.DIAMOND_PICKAXE) || itemStack.getItemMeta() == null || itemStack.getItemMeta().getLore() == null) {
            return;
        }

        final Map<CustomEnchant, Integer> customEnchants = this.instance.getEnchantHandler().findAllCustomEnchants(itemStack.getItemMeta().getLore(), false, player);

        if (customEnchants.keySet().stream().anyMatch(it -> it instanceof DiskEnchant)) {

            for (int i = 0; i < 3; i++) {

            }

        }


        if (customEnchants.keySet().stream().anyMatch(it -> it instanceof CubedEnchant)) {

        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onDurability(PlayerItemDamageEvent event) {
        final ItemStack itemStack = event.getItem();

        if (itemStack == null || itemStack.getItemMeta() == null || itemStack.getItemMeta().getLore() == null) {
            return;
        }

        final Map<CustomEnchant, Integer> customEnchants = this.instance.getEnchantHandler().findAllCustomEnchants(itemStack.getItemMeta().getLore(), false, event.getPlayer());

        if (customEnchants.containsKey(this.instance.getEnchantHandler().findCustomEnchant("HellForged"))) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onWorldChange(PlayerChangedWorldEvent event) {
        final Player player = event.getPlayer();

        if (player.getWorld().getEnvironment() != World.Environment.NETHER) {
            return;
        }

        Bukkit.getScheduler().runTask(Suge.getInstance(), () -> {
            final Map<CustomEnchant, Integer> customEnchants = this.instance.getEnchantHandler().findAllCustomEnchants(player, true);

            for (Map.Entry<CustomEnchant, Integer> entry : customEnchants.entrySet()) {
                entry.getKey().onDisable(player, entry.getValue());
            }
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onHunger(FoodLevelChangeEvent event) {
        final Player player = (Player) event.getEntity();

        final Map<CustomEnchant, Integer> customEnchants = this.instance.getEnchantHandler().findAllCustomEnchants(player, true);

        if (customEnchants.containsKey(this.instance.getEnchantHandler().findCustomEnchant("Implants"))) {
            player.setFoodLevel(20);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onPotionEffectExpire(PotionEffectExpireEvent event) {
        this.onPotionEffectRemove(event);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onPotionEffectRemove(PotionEffectRemoveEvent event) {

        if (event.isCancelled() || !(event.getEntity() instanceof Player)) {
            return;
        }


        if (event.getEffect().getType().equals(PotionEffectType.INVISIBILITY) && event.getEntity().hasMetadata("ARCHER_TAG")) {
            return;
        }

        final Player player = (Player) event.getEntity();
        final List<PotionEffect> effects = new ArrayList<>();

        Arrays.stream(player.getInventory().getArmorContents()).filter(it -> it != null && it.getItemMeta() != null && it.getItemMeta().getLore() != null).forEach(it -> {

            final Map<CustomEnchant, Integer> customEnchantIntegerMap = Suge.getInstance().getEnchantHandler().findAllCustomEnchants(it.getItemMeta().getLore(), false, player);

            for (Map.Entry<CustomEnchant, Integer> entry : customEnchantIntegerMap.entrySet()) {

                if (entry.getKey().getEffect() == null) {
                    continue;
                }

                effects.add(new PotionEffect(entry.getKey().getEffect(), Integer.MAX_VALUE, entry.getValue() - 1));
            }

        });

        this.instance.getServer().getScheduler().runTaskLater(this.instance, () -> effects.forEach(player::addPotionEffect), 1);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        this.instance.getEnchantHandler().findAllCustomEnchants(player, false).forEach((key, value) -> key.onDisable(player, value));
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onDeath(PlayerDeathEvent event) {
        final Player player = event.getEntity();
        final Player killer = player.getKiller();

        if (killer == null) {
            return;
        }

        final List<CustomEnchant> customEnchants = new ArrayList<>(this.instance.getEnchantHandler().findAllCustomEnchants(killer, false).keySet());

        if (customEnchants.stream().anyMatch(it -> it.getName().equalsIgnoreCase("Fury"))) {
            killer.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 11, 1), true);
        }

        if (customEnchants.stream().anyMatch(it -> it.getName().equalsIgnoreCase("Resistor"))) {
            killer.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 9, 4), true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        if (!event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
            return;
        }

        final Player player = (Player) event.getEntity();

        final List<CustomEnchant> customEnchants = new ArrayList<>(this.instance.getEnchantHandler().findAllCustomEnchants(player, false).keySet());

        if (customEnchants.stream().anyMatch(it -> it.getName().equalsIgnoreCase("GuardianAngel"))) {
            event.setDamage(event.getDamage() * 0.8);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onDeath(EntityDeathEvent event) {
        if (event.getEntity() instanceof Player) {
            return;
        }

        final Player killer = event.getEntity().getKiller();

        if (killer == null) {
            return;
        }

        final List<CustomEnchant> customEnchants = new ArrayList<>(this.instance.getEnchantHandler().findAllCustomEnchants(killer, false).keySet());

        if (customEnchants.stream().noneMatch(it -> it.getName().equalsIgnoreCase("Farmer"))) {
            return;
        }

        for (ItemStack drop : event.getDrops()) {
            drop.setAmount(drop.getAmount() * 2);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onEquipmentSet(EquipmentSetEvent event) {

        if (!(event.getHumanEntity() instanceof Player)) {
            return;
        }

        final ItemStack newItem = event.getNewItem();
        final ItemStack previousItem = event.getPreviousItem();

        final Player player = (Player) event.getHumanEntity();

        final Map<CustomEnchant, Integer> oldCustomEnchants = (previousItem == null || previousItem.getItemMeta() == null || previousItem.getItemMeta().getLore() == null) ? new HashMap<>() : Suge.getInstance().getEnchantHandler().findAllCustomEnchants(previousItem.getItemMeta().getLore(), true, player);

        if (player.getWorld().getEnvironment() == World.Environment.NETHER && this.isNetherCitadel(player)) {
            oldCustomEnchants.forEach((key, value) -> key.onDisable(player, value));
            return;
        }

        final Map<CustomEnchant, Integer> newCustomEnchants = (newItem == null || newItem.getItemMeta() == null || newItem.getItemMeta().getLore() == null) ? new HashMap<>() : Suge.getInstance().getEnchantHandler().findAllCustomEnchants(newItem.getItemMeta().getLore(), false, player);

        oldCustomEnchants.entrySet().stream().filter(it -> !newCustomEnchants.containsKey(it.getKey()) || !newCustomEnchants.get(it.getKey()).equals(it.getValue())).forEach(it -> it.getKey().onDisable(player, it.getValue()));
        newCustomEnchants.forEach((key, value) -> key.onEnable(player, value));
    }

    private boolean isNetherCitadel(Player player) {
        return false;
    }

//    public boolean canBreak(Player player, Block block) {
//
//        final BlockBreakEvent blockBreakEvent = new BlockBreakEvent(block, player);
//        Suge.getInstance().getServer().getPluginManager().callEvent(blockBreakEvent);
//
//        if (blockBreakEvent.isCancelled()) {
//            return false;
//        }
//
//        block.breakNaturally(player.getItemInHand());
//
//        return true;
//    }
}