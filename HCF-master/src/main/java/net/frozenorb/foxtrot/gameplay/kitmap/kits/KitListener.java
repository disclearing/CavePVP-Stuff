package net.frozenorb.foxtrot.gameplay.kitmap.kits;

import cc.fyre.modsuite.mod.ModHandler;
import cc.fyre.proton.util.TimeUtils;
import net.minecraft.util.com.google.common.collect.Maps;
import mkremins.fanciful.FancyMessage;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionType;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class KitListener implements Listener {

    private static final Map<UUID, Long> REFILL_PLAYER_MAP = Maps.newHashMap();
    private static final long REFILL_DELAY = TimeUnit.MINUTES.toMillis(10L);

    private static final Map<UUID, Long> LAST_CLICKED = Maps.newHashMap();

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        REFILL_PLAYER_MAP.remove(event.getEntity().getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        REFILL_PLAYER_MAP.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event) {
        if (event.getRightClicked() instanceof Wolf) {
            ((Wolf) event.getRightClicked()).setSitting(false);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getClickedBlock() == null || !(event.getClickedBlock().getState() instanceof Sign)) {
            return;
        }

        Sign sign = (Sign) event.getClickedBlock().getState();

        // Potion refill sign
        if (sign.getLine(0).startsWith(ChatColor.DARK_RED + "- Refill")) {
            openRefillInventory(event.getClickedBlock().getLocation(), player);
            return;
        }

        if (sign.getLine(1).startsWith(ChatColor.DARK_GREEN + "[Gem Shop]")) {
            player.chat("/gem shop");
            return;
        }

        if (sign.getLine(1).startsWith(ChatColor.DARK_RED + "[Repair]")) {
            player.chat("/gem repair");
            return;
        }

        if (sign.getLine(1).startsWith(ChatColor.RED + "[Block Shop]")) {
            player.chat("/blockshop");
            return;
        }

        if (!sign.getLine(0).startsWith(ChatColor.DARK_RED + "- Kit")) {
            return;
        }

        DefaultKit originalKit = Foxtrot.getInstance().getMapHandler().getKitManager().getDefaultKit(sign.getLine(1));

        if (originalKit != null) {
            attemptApplyKit(player, originalKit);
        }
    }

    public static void openRefillInventory(Location signLocation, Player player) {
        if (DTRBitmask.SAFE_ZONE.appliesAt(signLocation) && !DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation(signLocation)))
            return; // prevent players from using signs inside spawn outside spawn

        if (!DTRBitmask.SAFE_ZONE.appliesAt(signLocation)) { // put them on a cooldown
            long diff = REFILL_PLAYER_MAP.getOrDefault(player.getUniqueId(), 0L) - System.currentTimeMillis();

            if (diff > 0) {
                player.sendMessage(ChatColor.RED + "You have to wait " + TimeUtils.formatIntoDetailedString((int) (diff / 1000)) + " before using this again.");
                return;
            }

            REFILL_PLAYER_MAP.put(player.getUniqueId(), System.currentTimeMillis() + REFILL_DELAY);
            player.sendMessage(ChatColor.RED + "You have been put on a Refill Sign cooldown for " + ChatColor.RED + TimeUtils.formatIntoDetailedString((int) (REFILL_DELAY / 1000)) + ChatColor.YELLOW + ".");
        }

        Inventory inventory = Bukkit.createInventory(player, 45, "Refill");

        Potion healPotion = new Potion(PotionType.INSTANT_HEAL);
        healPotion.setLevel(2);
        healPotion.setSplash(true);
        ItemStack healItem = new ItemStack(Material.POTION, 1);
        healPotion.apply(healItem);

        Potion speedPotion = new Potion(PotionType.SPEED);
        speedPotion.setLevel(2);
        ItemStack speedItem = new ItemStack(Material.POTION, 1);
        speedPotion.apply(speedItem);

        inventory.setItem(0, speedItem);
        inventory.setItem(1, speedItem);
        inventory.setItem(9, speedItem);
        inventory.setItem(10, speedItem);
        inventory.setItem(18, speedItem);
        inventory.setItem(19, speedItem);

        inventory.setItem(27, new ItemStack(Material.ENDER_PEARL, 16));
        inventory.setItem(28, new ItemStack(Material.COOKED_BEEF, 64));
        inventory.setItem(27+9, new ItemStack(Material.ARROW, 16));
        inventory.setItem(28+9, new ItemStack(Material.FEATHER, 64));

        inventory.setItem(2, new ItemStack(Material.GOLD_SWORD, 1));
        inventory.setItem(11, new ItemStack(Material.GOLD_SWORD, 1));
        inventory.setItem(20, new ItemStack(Material.GOLD_SWORD, 1));
        inventory.setItem(29, new ItemStack(Material.GOLD_SWORD, 1));

        while (inventory.firstEmpty() != -1) {
            inventory.addItem(healItem);
        }

        player.openInventory(inventory);
    }

    public static void attemptApplyKit(Player player, Kit kit) {
        if (kit == null) {
            player.sendMessage(ChatColor.RED + "Unknown kit!");
            return;
        }

        if (!DTRBitmask.SAFE_ZONE.appliesAt(player.getLocation())) {
            player.sendMessage(ChatColor.GREEN + "Must be in spawn to use this command!");
            return;
        }

        if (ModHandler.INSTANCE.isInModMode(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You cannot use this while in mod mode.");
            return;
        }

        if (LAST_CLICKED.containsKey(player.getUniqueId()) && (System.currentTimeMillis() - LAST_CLICKED.get(player.getUniqueId()) < TimeUnit.SECONDS.toMillis(5))) {
            player.sendMessage(ChatColor.RED + "Please wait before using this again.");
            return;
        }

        if (Arrays.stream(player.getInventory().getContents()).anyMatch(it -> it != null && it.getType() != Material.AIR)) {
            final FancyMessage spacer = new FancyMessage("");
            final FancyMessage fancyMessage = new FancyMessage(ChatColor.translate("&6Your inventory isn't empty! Are you sure you want to use a kit? &aClick here to confirm the use of a kit"));
            fancyMessage.tooltip(ChatColor.GREEN + "Click here to confirm the use of your " + kit.getName() + " kit.");
            fancyMessage.command("/confirm");

            spacer.tooltip(ChatColor.GREEN + "Click here to confirm the use of your " + kit.getName() + " kit.");
            spacer.command("/confirm");

            spacer.send(player);
            fancyMessage.send(player);
            spacer.send(player);

            player.setMetadata("CONFIRM", new FixedMetadataValue(Foxtrot.getInstance(), kit.getName()));

            Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
                if (player.isOnline()) {
                    player.removeMetadata("CONFIRM", Foxtrot.getInstance());
                }
            }, 20 * 20);
            return;
        }

        DefaultKit originalKit = Foxtrot.getInstance().getMapHandler().getKitManager().getDefaultKit(kit.getName());
        if (originalKit != null) {
            Kit otherKit = Foxtrot.getInstance().getMapHandler().getKitManager().getUserKit(player.getUniqueId(), originalKit);
            if (otherKit != null) {
                otherKit.apply(player);
            } else {
                originalKit.apply(player);
            }
        }

        LAST_CLICKED.put(player.getUniqueId(), System.currentTimeMillis());
    }

}
