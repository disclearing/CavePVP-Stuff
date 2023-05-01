package net.frozenorb.foxtrot.gameplay.loot.airdrop.listener;

import cc.fyre.proton.menu.Button;
import cc.fyre.proton.menu.Menu;
import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.airdrop.command.AirDropCommand;
import net.frozenorb.foxtrot.gameplay.loot.airdrop.reward.AirDropReward;
import net.frozenorb.foxtrot.gameplay.loot.crate.Crate;
import net.frozenorb.foxtrot.gameplay.loot.crate.item.CrateItem;
import net.frozenorb.foxtrot.server.voucher.VoucherCommand;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Dropper;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@AllArgsConstructor
public class AirDropListener implements Listener {
    private Foxtrot instance;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlace(BlockPlaceEvent event) {
        final Player player = event.getPlayer();
        final ItemStack itemStack = event.getItemInHand();

        if (event.isCancelled() || !AirDropCommand.isAirdrop(itemStack)) {
            return;
        }

        event.setCancelled(true);

        final Block block = event.getBlockPlaced();
        final AtomicInteger seconds = new AtomicInteger(4);

        VoucherCommand.spawnFireworks(block.getLocation(), 1, 2, Color.RED, FireworkEffect.Type.BALL_LARGE);

        if (player.getItemInHand().getAmount() == 1) {
            player.setItemInHand(null);
        } else {
            player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
        }

        boolean airdropAll = itemStack.getItemMeta().getLore().size() > 3;

        player.updateInventory();

        new BukkitRunnable() {
            @Override
            public void run() {
                seconds.decrementAndGet();

                if (seconds.get() > 0) {
                    block.getWorld().playEffect(block.getLocation(), Effect.MOBSPAWNER_FLAMES, 2);
                    block.getWorld().playEffect(block.getLocation(), Effect.LARGE_SMOKE, 2);

                    player.sendMessage(ChatColor.RED + "Airdrop will drop in " + ChatColor.WHITE + seconds.get() + ChatColor.RED + ".");
                } else {
                    player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);

                    block.setType(Material.DROPPER);
                    block.getWorld().playEffect(block.getLocation(), Effect.EXPLOSION_HUGE, 7);

                    Foxtrot.getInstance().getAirDropHandler().setContents(block, player, airdropAll);

                    player.sendMessage(ChatColor.GREEN + "The Airdrop has dropped!");
                    this.cancel();

                    if (player.getWorld().getEnvironment() == World.Environment.NETHER || player.getWorld().getEnvironment() == World.Environment.THE_END || Foxtrot.getInstance().getServerHandler().isWarzone(block.getLocation())) {
                        Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {

                            if (block.getType() == Material.DROPPER) {
                                block.setType(Material.AIR);
                            }
                        }, 20 * 60 * 2);
                    }
                }
            }
        }.runTaskTimer(Foxtrot.getInstance(), 0, 20);
    }

    @EventHandler
    private void onClose(InventoryCloseEvent event) {
        final Inventory inventory = event.getInventory();

        if (!inventory.getType().equals(InventoryType.DROPPER)) {
            return;
        }

        // Dont know how this wouild be possible but will do!
        if (!(event.getInventory().getHolder() instanceof Dropper)) {
            return;
        }

        for (ItemStack item : inventory.getContents()) {
            if (item != null && item.getType() != Material.AIR) {
                return;
            }
        }

        final Dropper holder = (Dropper) event.getInventory().getHolder();
        holder.getBlock().breakNaturally();
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onInteract(PlayerInteractEvent event) {
        if (!event.getAction().name().contains("RIGHT")) {
            return;
        }

        final Player player = event.getPlayer();
        final ItemStack itemStack = event.getItem();

        if (itemStack == null || !isSimilar(itemStack)) {
            return;
        }

        if (player.isSneaking()) {
            event.setCancelled(true);
            new Menu() {

                @Override
                public String getTitle(Player player) {
                    return "Airdrop Loot";
                }

                @Override
                public Map<Integer, Button> getButtons(Player player) {
                    final Map<Integer, Button> toReturn = new HashMap<>();

                    for (AirDropReward airDropReward : Foxtrot.getInstance().getAirDropHandler().getCache()) {

                        if (airDropReward.getItemStack() == null || airDropReward.getItemStack().getType().equals(Material.AIR)) {
                            continue;
                        }

                        toReturn.put(toReturn.size(), Button.fromItem(airDropReward.getItemStack().clone()));
                    }

                    return toReturn;
                }
            }.openMenu(player);
        }
    }

    public boolean isSimilar(ItemStack itemStack) {
        final ItemStack compareTo = this.instance.getAirDropHandler().getItemStack();

        if (itemStack == null || itemStack.getItemMeta() == null || itemStack.getType() == Material.AIR) {
            return false;
        }

        if (itemStack.getItemMeta().getDisplayName() == null) {
            return false;
        }

        if (itemStack.getItemMeta().getLore() == null || itemStack.getItemMeta().getLore().isEmpty()) {
            return false;
        }

        return itemStack.getType() == compareTo.getType() && itemStack.getItemMeta().getDisplayName().startsWith(compareTo.getItemMeta().getDisplayName()) && itemStack.getItemMeta().getLore().get(0).equals(compareTo.getItemMeta().getLore().get(0));
    }

}
