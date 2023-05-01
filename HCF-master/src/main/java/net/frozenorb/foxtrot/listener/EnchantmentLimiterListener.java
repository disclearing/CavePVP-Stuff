package net.frozenorb.foxtrot.listener;

import net.minecraft.util.com.google.common.collect.ImmutableSet;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.InventoryUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class EnchantmentLimiterListener implements Listener {

    public static final ImmutableSet<Character> ITEM_NAME_CHARACTER_BLACKLIST = ImmutableSet.of(
            '卍'
    );

    private Map<String, Long> lastArmorCheck = new HashMap<>();
    private Map<String, Long> lastSwordCheck = new HashMap<>();

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {

        if (event.getDamager() instanceof Player) {
            final Player player = (Player) event.getDamager();

            if (player.getItemInHand().containsEnchantment(Enchantment.ARROW_KNOCKBACK)) {
                event.setCancelled(true);

                player.getItemInHand().removeEnchantment(Enchantment.ARROW_KNOCKBACK);
                player.updateInventory();

                player.sendMessage(ChatColor.RED + "You may not use a Punch Bow!");
            }
        }

        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player player = (Player) event.getDamager();
            ItemStack hand = player.getItemInHand();

            if (!hand.containsEnchantment(Enchantment.KNOCKBACK)) {
                return;
            }

            if (DTRBitmask.CITADEL.appliesAt(player.getLocation()) || DTRBitmask.CONQUEST.appliesAt(player.getLocation()) || DTRBitmask.KOTH.appliesAt(player.getLocation())) {
                final String landAt = LandBoard.getInstance().getTeam(player.getLocation()).getName(player);

                event.setCancelled(true);
                player.sendMessage(CC.translateAlternateColorCodes("&cYou may not use knockback items at " + landAt + "&c!"));
                return;
            }

            if (Foxtrot.getInstance().getServerHandler().isEOTW()) {
                event.setCancelled(true);
                player.sendMessage(CC.translateAlternateColorCodes("&cYou may not use knockback items while &4EOTW &cis active!"));
                return;
            }

            if (player.getLocation().getWorld().getEnvironment() == World.Environment.THE_END || player.getLocation().getWorld().getEnvironment() == World.Environment.NETHER) {
                player.sendMessage(CC.translateAlternateColorCodes("&cYou may not use knockback items in The End/Nether!"));
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory() instanceof AnvilInventory) {
            InventoryView view = event.getView();

            if (event.getCurrentItem() == null || event.getRawSlot() != view.convertSlot(event.getRawSlot()) || event.getRawSlot() != 2) {
                return;
            }

            ItemStack item = event.getCurrentItem();
            ItemMeta meta = item.getItemMeta();

            if (meta != null && meta.hasDisplayName()) {
                ItemStack previous = event.getInventory().getItem(0);

                if (previous != null && previous.hasItemMeta() && previous.getItemMeta().hasDisplayName() && containsColor(previous.getItemMeta().getDisplayName())) {
                    /* Admin item, dont allow repair or rename */
                    event.setCancelled(false);
                    event.setResult(Event.Result.DENY);

                    /* Start stupid workaround to update exp */
                    view.close();

                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            ((Player) event.getWhoClicked()).giveExp(5);
                        }

                    }.runTaskLater(Foxtrot.getInstance(), 2L);

                    new BukkitRunnable() {

                        @Override
                        public void run() {
                            ((Player) event.getWhoClicked()).giveExp(-5);
                        }

                    }.runTaskLater(Foxtrot.getInstance(), 6L);
                    /* End stupid workaround to update exp */

                    return;
                } else {
                    meta.setDisplayName(fixName(meta.getDisplayName()));
                }

                item.setItemMeta(meta);
                event.setCurrentItem(item);
            }
        }
    }

    private boolean containsColor(String displayName) {
        return !ChatColor.stripColor(displayName).equals(displayName);
    }

    private String fixName(String name) {
        StringBuilder result = new StringBuilder();

        for (char nameCharacter : name.toCharArray()) {
            boolean blacklisted = false;

            for (char blacklistCharacter : ITEM_NAME_CHARACTER_BLACKLIST) {
                if (nameCharacter == blacklistCharacter) {
                    blacklisted = true;
                    break;
                }
            }

            if (!blacklisted) {
                result.append(nameCharacter);
            }
        }

        return (result.toString());
    }

    public boolean checkArmor(Player player) {
        boolean check = !lastArmorCheck.containsKey(player.getName()) || (System.currentTimeMillis() - lastArmorCheck.get(player.getName())) > 5000L;

        if (check) {
            lastArmorCheck.put(player.getName(), System.currentTimeMillis());
        }

        return (check);
    }

    public boolean checkSword(Player player) {
        boolean check = !lastSwordCheck.containsKey(player.getName()) || (System.currentTimeMillis() - lastSwordCheck.get(player.getName())) > 5000L;

        if (check) {
            lastSwordCheck.put(player.getName(), System.currentTimeMillis());
        }

        return (check);
    }

}