package net.frozenorb.foxtrot.gameplay.ability.type;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.Ability;
import net.frozenorb.foxtrot.gameplay.ability.Category;
import net.frozenorb.foxtrot.gameplay.ability.listener.events.AbilityUseEvent;
import net.frozenorb.foxtrot.listener.FoxListener;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.util.InventoryUtils;
import net.frozenorb.foxtrot.util.PotionUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.Potion;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static org.bukkit.Material.POTION;

public class MidasTouch extends Ability {
    @Override
    public Category getCategory() {
        return Category.SEASONAL_CRATE;
    }

    @Override
    public String getDescription() {
        return "Players may not break/place/interact blocks on top of your gold trail!";
    }

    public static Map<Location, Material> cache = new HashMap<>();
    public static Map<Location, Byte> data = new HashMap<>();
    private List<Material> disallowedMaterial = Arrays.asList(
            Material.CHEST, Material.TRAPPED_CHEST, Material.ENDER_CHEST, Material.SIGN, Material.SIGN_POST, Material.HOPPER,
            Material.ENCHANTMENT_TABLE, Material.AIR, Material.DROPPER, Material.DISPENSER, Material.FENCE_GATE);

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public Material getMaterial() {
        return Material.GOLD_INGOT;
    }

    @Override
    public String getDisplayName() {
        return ChatColor.GOLD.toString() + ChatColor.BOLD + "Midas Touch";
    }

    @Override
    public List<String> getLore() {
        final List<String> toReturn = new ArrayList<>();

        toReturn.add("");
        toReturn.add(ChatColor.translate("&6❙ &fClick to activate and for &e&l15 seconds"));
        toReturn.add(ChatColor.translate("&6❙ &fa 3x3 of gold blocks appear under you."));
        toReturn.add(ChatColor.translate("&6❙ &fYou cannot break/place/interact with."));
        toReturn.add(ChatColor.translate("&6❙ &fblocks on top of these gold blocks."));
        toReturn.add("");
        toReturn.add(ChatColor.translate("&fCan be found in the &d&lPartner Crate&f!"));

        return toReturn;
    }

    @Override
    public Boolean isAllowedAtLocation(Location location) {
        if (Foxtrot.getInstance().getMapHandler().isKitMap()) {
            return !DTRBitmask.KOTH.appliesAt(location) && !DTRBitmask.CONQUEST.appliesAt(location) && !DTRBitmask.CITADEL.appliesAt(location) && !DTRBitmask.DTC.appliesAt(location) && !DTRBitmask.SAFE_ZONE.appliesAt(location);
        }

        return !Foxtrot.getInstance().getServerHandler().isWarzone(location) && location.getWorld().getEnvironment() == World.Environment.NORMAL && !DTRBitmask.KOTH.appliesAt(location) && !DTRBitmask.CONQUEST.appliesAt(location) && !DTRBitmask.CITADEL.appliesAt(location) && !DTRBitmask.DTC.appliesAt(location) && !DTRBitmask.SAFE_ZONE.appliesAt(location);
    }



    @Override
    public long getCooldown() {
        return 135_000L;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();

        if (!this.isSimilar(player.getItemInHand()) || event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        event.setCancelled(true);

        final AbilityUseEvent abilityUseEvent = new AbilityUseEvent(player, null, player.getLocation(), this, false);
        Foxtrot.getInstance().getServer().getPluginManager().callEvent(abilityUseEvent);

        if (abilityUseEvent.isCancelled()) {
            return;
        }

        event.setCancelled(true);

        if (player.getItemInHand().getAmount() == 1) {
            player.setItemInHand(null);
        } else {
            player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
        }

        player.updateInventory();

        player.setMetadata("ANTI_TRAP", new FixedMetadataValue(Foxtrot.getInstance(), true));

        Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
            if (player.isOnline()) {
                player.removeMetadata("ANTI_TRAP", Foxtrot.getInstance());
            }
        }, 20 * 11);

        final Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);

        if (team != null) {
            applyCooldown(team, player);
            return;
        }

        this.applyCooldown(player);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        if (player.hasMetadata("ANTI_TRAP")) {
            player.removeMetadata("ANTI_TRAP", Foxtrot.getInstance());
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        if (player.hasMetadata("ANTI_TRAP")) {
            player.removeMetadata("ANTI_TRAP", Foxtrot.getInstance());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockY() == event.getTo().getBlockY() && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        final Player player = event.getPlayer();

        if (event.isCancelled() || !player.hasMetadata("ANTI_TRAP")) {
            return;
        }

        final Block firstBlock = event.getTo().getBlock().getRelative(BlockFace.DOWN);

        if (firstBlock.getType() == Material.AIR) {
            return;
        }

        final Block secondBlock = firstBlock.getRelative(BlockFace.SOUTH);
        final Block thirdBlock = firstBlock.getRelative(BlockFace.WEST);
        final Block fourthBlock = thirdBlock.getRelative(BlockFace.SOUTH);
        final Block fifthBlock = firstBlock.getRelative(BlockFace.NORTH);
        final Block sixthBlock = fifthBlock.getRelative(BlockFace.WEST);
        final Block seventhBlock = fifthBlock.getRelative(BlockFace.EAST);
        final Block eighthBlock = secondBlock.getRelative(BlockFace.EAST);
        final Block ninthBlock = seventhBlock.getRelative(BlockFace.SOUTH);

        setAntiTrapBlock(firstBlock);
        setAntiTrapBlock(secondBlock);
        setAntiTrapBlock(thirdBlock);
        setAntiTrapBlock(fourthBlock);
        setAntiTrapBlock(fifthBlock);
        setAntiTrapBlock(sixthBlock);
        setAntiTrapBlock(seventhBlock);
        setAntiTrapBlock(eighthBlock);
        setAntiTrapBlock(ninthBlock);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onThankMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockY() == event.getTo().getBlockY() && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        final Player player = event.getPlayer();

        if (event.isCancelled() || !player.hasMetadata("ANTI_TRAP")) {
            return;
        }

        final Block block = event.getTo().getBlock();
        final Block firstBlock = block.getRelative(BlockFace.DOWN);

        if (firstBlock.getType() != Material.AIR) {
            return;
        }

        final Block secondBlock = firstBlock.getRelative(BlockFace.DOWN);

        if (secondBlock.getType() != Material.AIR) {
            return;
        }

        final Block thirdBlock = secondBlock.getRelative(BlockFace.DOWN);

        if (thirdBlock.getType() != Material.AIR) {
            return;
        }

        setAntiTrapBlock(firstBlock);
    }

    public boolean isAntiTrapBlock(Block block) {
        return block.hasMetadata("ANTI_TRAP");
    }

    public void setAntiTrapBlock(Block block) {

        final Material type = block.getType();

        if (block.hasMetadata("ANTI_TRAP")) {
            return;
        }

        if (!this.isAllowedAtLocation(block.getLocation())) {
            return;
        }

        if (!type.isSolid()) {
            return;
        }

        if (type.name().toLowerCase().contains("fence")) {
            return;
        }

        if (this.disallowedMaterial.contains(type)) {
            block.setMetadata("ANTI_TRAP", new FixedMetadataValue(Foxtrot.getInstance(), true));

            new BukkitRunnable() {
                @Override
                public void run() {
                    block.removeMetadata("ANTI_TRAP", Foxtrot.getInstance());
                }
            }.runTaskLater(Foxtrot.getInstance(), 20 * 5);
        } else {
            cache.put(block.getLocation(), type);
            data.put(block.getLocation(), block.getData());

            block.setType(Material.GOLD_BLOCK);
            block.setMetadata("ANTI_TRAP", new FixedMetadataValue(Foxtrot.getInstance(), true));

            new BukkitRunnable() {
                @Override
                public void run() {
                    block.removeMetadata("ANTI_TRAP", Foxtrot.getInstance());
                    block.setType(cache.remove(block.getLocation()));
                    block.setData(data.remove(block.getLocation()));
                }
            }.runTaskLater(Foxtrot.getInstance(), 20 * 5);
        }
    }


    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final Player player = event.getPlayer();

        final Block downBlock = event.getBlockPlaced().getRelative(BlockFace.DOWN);
        final Block downTwoBlock = downBlock.getRelative(BlockFace.DOWN);

        if (!this.isAntiTrapBlock(downBlock) && !this.isAntiTrapBlock(downTwoBlock)) {
            return;
        }

        player.sendMessage(ChatColor.RED + "You may not place blocks on top of a " + ChatColor.GOLD + ChatColor.BOLD.toString() + "Midas Touch" + ChatColor.RED + ".");
        event.setCancelled(true);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        final Block block = event.getBlock();

        if (event.isCancelled()) {
            return;
        }

        final Player player = event.getPlayer();

        if (this.isAntiTrapBlock(block) && player.getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "You may not break a anti-trap rod block!");
            return;
        }

        final Block downBlock = block.getRelative(BlockFace.DOWN);
        final Block downTwoBlock = downBlock.getRelative(BlockFace.DOWN);

        if (this.isAntiTrapBlock(downBlock) || this.isAntiTrapBlock(downTwoBlock)) {
            player.sendMessage(ChatColor.RED + "You may not break blocks as there is a " + ChatColor.GOLD + ChatColor.BOLD + "Midas Touch" + ChatColor.RED + " block below.");
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onPlate(PlayerInteractEvent event) {
        final Block clickedBlock = event.getClickedBlock();

        if ((event.getAction() != Action.PHYSICAL || clickedBlock == null || !clickedBlock.getType().name().contains("PLATE"))) {
            return;
        }

        final Block downBlock = event.getClickedBlock().getRelative(BlockFace.DOWN);

        if (!this.isAntiTrapBlock(downBlock)) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    private void onClick(PlayerInteractEvent event) {
        if ((event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getClickedBlock() == null || !FoxListener.NO_INTERACT.contains(event.getClickedBlock().getType()))) {
            return;
        }

        final Player player = event.getPlayer();

        final Block downBlock = event.getClickedBlock().getRelative(BlockFace.DOWN);
        final Block downTwoBlock = downBlock.getRelative(BlockFace.DOWN);
        final Block downThreeBlock = downTwoBlock.getRelative(BlockFace.DOWN);

        if (!this.isAntiTrapBlock(downBlock) && !this.isAntiTrapBlock(downTwoBlock) && !this.isAntiTrapBlock(downThreeBlock)) {
            return;
        }

        if (event.getItem() != null && event.getItem().getType() == POTION && event.getItem().getDurability() != 0) {
            Potion potion = Potion.fromItemStack(event.getItem());

            if (potion.isSplash()) {
                PotionUtil.splashPotion(player, event.getItem());
                if (player.getItemInHand() != null && player.getItemInHand().isSimilar(event.getItem())) {
                    player.setItemInHand(null);
                    player.updateInventory();
                } else {
                    InventoryUtils.removeAmountFromInventory(player.getInventory(), event.getItem(), 1);
                }
            }
        }

        event.setCancelled(true);
        player.sendMessage(ChatColor.RED + "You may not open or close " + event.getClickedBlock().getType().name().toLowerCase().replace("_", " ") + "s as there is a " + ChatColor.GOLD + ChatColor.BOLD + "Midas Touch" + ChatColor.RED + " under it.");
    }
}