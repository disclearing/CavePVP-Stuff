package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.listener.event.CrowbarSpawnerBreakEvent;
import net.frozenorb.foxtrot.util.InventoryUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CrowbarListener implements Listener {

    @EventHandler(ignoreCancelled=true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getItem() == null || !InventoryUtils.isSimilar(event.getItem(), InventoryUtils.CROWBAR_NAME) || !(event.getAction() == Action.LEFT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            return;
        }

        if (!Foxtrot.getInstance().getServerHandler().isUnclaimedOrRaidable(event.getPlayer(), event.getClickedBlock().getLocation()) && !Foxtrot.getInstance().getServerHandler().isAdminOverride(event.getPlayer())) {
            Team team = LandBoard.getInstance().getTeam(event.getClickedBlock().getLocation());
            if (team != null && !team.isMember(event.getPlayer().getUniqueId())) {
                event.getPlayer().sendMessage(ChatColor.YELLOW + "You cannot crowbar in " + ChatColor.RED + team.getName(event.getPlayer()) + ChatColor.YELLOW + "'s territory!");
                return;
            }
        }

        if (DTRBitmask.SAFE_ZONE.appliesAt(event.getClickedBlock().getLocation())) {
            event.getPlayer().sendMessage(ChatColor.YELLOW + "You cannot crowbar spawn!");
            return;
        }

        if (event.getClickedBlock().getType() == Material.ENDER_PORTAL_FRAME) {
            int portals = InventoryUtils.getCrowbarUsesPortal(event.getItem());

            if (portals == 0) {
                event.getPlayer().sendMessage(ChatColor.RED + "This crowbar has no more uses on end portals!");
                return;
            }

            event.getClickedBlock().getWorld().playEffect(event.getClickedBlock().getLocation(), Effect.STEP_SOUND, event.getClickedBlock().getTypeId());
            event.getClickedBlock().setType(Material.AIR);
            event.getClickedBlock().getState().update();

            event.getClickedBlock().getWorld().dropItemNaturally(event.getClickedBlock().getLocation(), new ItemStack(Material.ENDER_PORTAL_FRAME));
            event.getClickedBlock().getWorld().playSound(event.getClickedBlock().getLocation(), Sound.ANVIL_USE, 1.0F, 1.0F);

            for (int x = -3; x < 3; x++) {
                for (int z = -3; z < 3; z++) {
                    Block block = event.getClickedBlock().getLocation().add(x, 0, z).getBlock();

                    if (block.getType() == Material.ENDER_PORTAL) {
                        block.setType(Material.AIR);
                        block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, Material.ENDER_PORTAL.getId());
                    }
                }
            }

            portals -= 1;

            if (portals == 0) {
                event.getPlayer().setItemInHand(null);
                event.getClickedBlock().getLocation().getWorld().playSound(event.getClickedBlock().getLocation(), Sound.ITEM_BREAK, 1.0F, 1.0F);
                return;
            }

            ItemMeta meta = event.getItem().getItemMeta();

            meta.setLore(InventoryUtils.getCrowbarLore(portals, 0));

            event.getItem().setItemMeta(meta);

            double max = Material.DIAMOND_HOE.getMaxDurability();
            double dura = (max / (double) InventoryUtils.CROWBAR_PORTALS) * portals;

            event.getItem().setDurability((short) (max - dura));
            event.getPlayer().setItemInHand(event.getItem());
        } else if (event.getClickedBlock().getType() == Material.MOB_SPAWNER) {
            CreatureSpawner spawner = (CreatureSpawner) event.getClickedBlock().getState();
            int spawners = InventoryUtils.getCrowbarUsesSpawner(event.getItem());

            if (spawners == 0) {
                event.getPlayer().sendMessage(ChatColor.RED + "This crowbar has no more uses on mob spawners!");
                return;
            }

            if (event.getClickedBlock().getWorld().getEnvironment() == World.Environment.NETHER) {
                event.getPlayer().sendMessage(ChatColor.RED + "You cannot break spawners in the nether!");
                event.setCancelled(true);
                return;
            }

            if (event.getClickedBlock().getWorld().getEnvironment() == World.Environment.THE_END) {
                event.getPlayer().sendMessage(ChatColor.RED + "You cannot break spawners in the end!");
                event.setCancelled(true);
                return;
            }

            CrowbarSpawnerBreakEvent crowbarSpawnerBreakEvent = new CrowbarSpawnerBreakEvent(event.getPlayer(), event.getClickedBlock());
            Foxtrot.getInstance().getServer().getPluginManager().callEvent(crowbarSpawnerBreakEvent);

            if (crowbarSpawnerBreakEvent.isCancelled()) {
                return;
            }

            event.getClickedBlock().getLocation().getWorld().playEffect(event.getClickedBlock().getLocation(), Effect.STEP_SOUND, event.getClickedBlock().getTypeId());
            event.getClickedBlock().setType(Material.AIR);
            event.getClickedBlock().getState().update();

            ItemStack drop = new ItemStack(Material.MOB_SPAWNER);
            ItemMeta meta = drop.getItemMeta();

            meta.setDisplayName(ChatColor.RESET + StringUtils.capitaliseAllWords(spawner.getSpawnedType().toString().toLowerCase().replaceAll("_", " ")) + " Spawner");
            drop.setItemMeta(meta);

            event.getClickedBlock().getLocation().getWorld().dropItemNaturally(event.getClickedBlock().getLocation(), drop);
            event.getClickedBlock().getLocation().getWorld().playSound(event.getClickedBlock().getLocation(), Sound.ANVIL_USE, 1.0F, 1.0F);

            spawners -= 1;

            if (spawners == 0) {
                event.getPlayer().setItemInHand(null);
                event.getClickedBlock().getLocation().getWorld().playSound(event.getClickedBlock().getLocation(), Sound.ITEM_BREAK, 1.0F, 1.0F);
                return;
            }

            meta = event.getItem().getItemMeta();

            meta.setLore(InventoryUtils.getCrowbarLore(0, spawners));

            event.getItem().setItemMeta(meta);

            double max = Material.DIAMOND_HOE.getMaxDurability();
            double dura = (max / (double) InventoryUtils.CROWBAR_SPAWNERS) * spawners;

            event.getItem().setDurability((short) (max - dura));
            event.getPlayer().setItemInHand(event.getItem());
        } else {
            event.getPlayer().sendMessage(ChatColor.RED + "Crowbars can only break end portals and mob spawners!");
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.getPlayer().getWorld().getEnvironment() == World.Environment.NETHER && event.getBlock().getType() == Material.MOB_SPAWNER) {
            event.getPlayer().sendMessage(ChatColor.RED + "You cannot break spawners in the nether!");
            event.setCancelled(true);
        } else if (event.getBlock().getType() == Material.MOB_SPAWNER) {
            event.getPlayer().sendMessage(ChatColor.RED + "This is too strong for you to break! Try using a crowbar instead.");
            event.setCancelled(true);
        }
    }

}
