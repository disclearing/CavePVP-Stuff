package net.frozenorb.foxtrot.listener;

import net.minecraft.util.com.google.common.collect.ImmutableSet;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryHolder;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SignSubclaimListener implements Listener {

    public static final Set<BlockFace> OUTSIDE_FACES = ImmutableSet.of(BlockFace.EAST, BlockFace.WEST, BlockFace.NORTH, BlockFace.SOUTH);
    public static final String SUBCLAIM_IDENTIFIER = ChatColor.YELLOW.toString() + ChatColor.BOLD + "[Subclaim]";
    public static final String NO_ACCESS = ChatColor.YELLOW + "You do not have access to this chest subclaim!";

    @EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
    public void onSignUpdate(SignChangeEvent event) {
        if (!event.getLine(0).toLowerCase().contains("subclaim")) {
            return;
        }

        Team playerTeam = Foxtrot.getInstance().getTeamHandler().getTeam(event.getPlayer());
        Sign sign = (Sign) event.getBlock().getState();

        if (playerTeam == null) {
            event.getBlock().breakNaturally();
            event.getPlayer().sendMessage(ChatColor.RED + "You are not in a faction!");
            return;
        }

        if (!playerTeam.ownsLocation(sign.getLocation())) {
            event.getBlock().breakNaturally();
            event.getPlayer().sendMessage(ChatColor.RED + "You do not own this land!");
            return;
        }

        BlockFace attachedFace = ((org.bukkit.material.Sign) sign.getData()).getAttachedFace();
        Block attachedTo = event.getBlock().getRelative(attachedFace);

        if (!(attachedTo.getType().equals(Material.CHEST)) && !(attachedTo.getType().equals(Material.TRAPPED_CHEST))) {
            event.getPlayer().sendMessage("§cSign subclaims only work on chests.");
            return;
        }

        if (subclaimSigns(attachedTo).size() != 0) {
            event.getBlock().breakNaturally();
            event.getPlayer().sendMessage(ChatColor.RED + "This chest is already subclaimed!");
            return;
        }

        /*if (!playerTeam.isCaptain(event.getPlayer().getKitName()) && !playerTeam.isOwner(event.getPlayer().getKitName())) {
            event.getBlock().breakNaturally();
            event.getPlayer().sendMessage(ChatColor.RED + "You must be a team captain to be able to do this!");
            return;
        }*/

        boolean found = false;

        for (int i = 1; i <= 3; i++) {
            if (sign.getLine(i) != null && sign.getLine(i).equalsIgnoreCase(event.getPlayer().getName())) {
                found = true;
                break;
            }
        }

        if (!found) {
            if (event.getPlayer().getName().length() > 15) {
                event.getBlock().breakNaturally();
                event.getPlayer().sendMessage("§cYour name is too long for sign subclaims. Consider changing your username.");
                return;
            }
        }

        String signText = event.getLine(1) + event.getLine(2) + event.getLine(3);

        if (signText.isEmpty()) {
            event.getPlayer().sendMessage(ChatColor.GREEN + "We automatically added you to this subclaim.");
            event.setLine(1, event.getPlayer().getName());
        }

        event.setLine(0, SUBCLAIM_IDENTIFIER);
        event.getPlayer().sendMessage(ChatColor.GREEN + "Sign subclaim created!");
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onBlockBreakChest(BlockBreakEvent event) {
        if (!(event.getBlock().getState() instanceof Chest) || Foxtrot.getInstance().getServerHandler().isUnclaimedOrRaidable(event.getPlayer(), event.getBlock().getLocation()) || Foxtrot.getInstance().getServerHandler().isAdminOverride(event.getPlayer())) {
            return;
        }

        if (Foxtrot.getInstance().getServerHandler().isEOTW()) {
            return;
        }

        Team owningTeam = LandBoard.getInstance().getTeam(event.getBlock().getLocation());
        UUID uuid = event.getPlayer().getUniqueId();

        for (Sign sign : subclaimSigns(event.getBlock())) {
            if (!(owningTeam.isOwner(uuid) || owningTeam.isCoLeader(uuid) || owningTeam.isCaptain(uuid))) {
                event.getPlayer().sendMessage(NO_ACCESS);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onBlockBreakSign(BlockBreakEvent event) {
        if (!(event.getBlock().getState() instanceof Sign) || Foxtrot.getInstance().getServerHandler().isUnclaimedOrRaidable(event.getPlayer(), event.getBlock().getLocation()) || Foxtrot.getInstance().getServerHandler().isAdminOverride(event.getPlayer())) {
            return;
        }

        if (Foxtrot.getInstance().getServerHandler().isEOTW()) {
            return;
        }

        Team owningTeam = LandBoard.getInstance().getTeam(event.getBlock().getLocation());
        Sign sign = (Sign) event.getBlock().getState();
        UUID uuid = event.getPlayer().getUniqueId();

        if (sign.getLine(0).equals(SUBCLAIM_IDENTIFIER)) {
            boolean canAccess = owningTeam.isOwner(uuid) || owningTeam.isCoLeader(uuid) || owningTeam.isCaptain(uuid);

            for (int i = 0; i <= 3; i++) {
                if (sign.getLine(i) != null && sign.getLine(i).equalsIgnoreCase(event.getPlayer().getName())) {
                    canAccess = true;
                    break;
                }
            }

            if (!canAccess) {
                event.getPlayer().sendMessage(NO_ACCESS);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority=EventPriority.HIGH, ignoreCancelled=true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || !(event.getClickedBlock().getState() instanceof Chest) || Foxtrot.getInstance().getServerHandler().isUnclaimedOrRaidable(event.getPlayer(), event.getClickedBlock().getLocation())
                || Foxtrot.getInstance().getServerHandler().isAdminOverride(event.getPlayer())) {
            return;
        }

        if (Foxtrot.getInstance().getServerHandler().isEOTW()) {
            return;
        }

        // Will never be null, we check isUnclaimedOrRaidable above.
        Team owningTeam = LandBoard.getInstance().getTeam(event.getClickedBlock().getLocation());
        UUID uuid = event.getPlayer().getUniqueId();

        for (Sign sign : subclaimSigns(event.getClickedBlock())) {
            boolean canAccess = owningTeam.isOwner(uuid) || owningTeam.isCoLeader(uuid) || owningTeam.isCaptain(uuid);

            for (int i = 0; i <= 3; i++) {
                if (sign.getLine(i) != null && sign.getLine(i).equalsIgnoreCase(event.getPlayer().getName())) {
                    canAccess = true;
                    break;
                }
            }

            if (!canAccess) {
                event.getPlayer().sendMessage(NO_ACCESS);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventoryMoveItem(InventoryMoveItemEvent event) {
        if (event.getSource().getType() == InventoryType.CHEST && event.getDestination().getType() == InventoryType.HOPPER) {
            InventoryHolder inventoryHolder = event.getSource().getHolder();
            Block moveBlock = null;

            // Special-case double chests
            if (inventoryHolder instanceof DoubleChest) {
                moveBlock = ((DoubleChest) inventoryHolder).getLocation().getBlock();
            } else if (inventoryHolder instanceof BlockState) {
                moveBlock = ((BlockState) inventoryHolder).getBlock();
            }

            if (moveBlock != null && subclaimSigns(moveBlock).size() != 0) {
                event.setCancelled(true);
            }
        }
    }

    public static Set<Sign> subclaimSigns(Block check) {
        Set<Sign> signs = new HashSet<>();

        for (BlockFace blockFace : OUTSIDE_FACES) {
            Block relBlock = check.getRelative(blockFace);

            if (relBlock.getType() == check.getType()) {
                subclaimSigns0(signs, relBlock);
            }
        }

        subclaimSigns0(signs, check);

        return (signs);
    }

    public static void subclaimSigns0(Set<Sign> signs, Block check) {
        for (BlockFace blockFace : OUTSIDE_FACES) {
            Block relBlock = check.getRelative(blockFace);

            if (relBlock.getType() == Material.WALL_SIGN || relBlock.getType() == Material.SIGN) {
                Sign sign = (Sign) relBlock.getState();

                if (sign.getLine(0).equals(SUBCLAIM_IDENTIFIER)) {
                    signs.add(sign);
                }
            }
        }
    }

}