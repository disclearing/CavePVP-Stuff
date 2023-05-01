package net.frozenorb.foxtrot.gameplay.loot.treasurechest.listener;

import cc.fyre.proton.Proton;
import cc.fyre.proton.hologram.construct.Hologram;
import cc.fyre.proton.util.ItemUtils;
import lombok.Getter;
import lombok.Setter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.loot.treasurechest.TreasureChest;
import net.frozenorb.foxtrot.gameplay.loot.treasurechest.TreasureChestHandler;
import net.frozenorb.foxtrot.gameplay.loot.treasurechest.menu.TreasureChestMenu;
import net.frozenorb.foxtrot.gameplay.loot.treasurechest.reward.TreasureChestReward;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

public class TreasureChestListener implements Listener {
    private Foxtrot instance;

    public TreasureChestListener(Foxtrot instance) {
        this.instance = instance;
    }
    
    public TreasureChestHandler getTreasureChestHandler() {
        return this.instance.getTreasureChestHandler();
    }

    @Getter @Setter private TreasureChest currentlyOpening;
    @Getter @Setter private UUID currentPlayer = null;
    @Getter @Setter private int chestsOpened = 0;
    @Getter @Setter private boolean ending = false;
    @Getter private List<TreasureChestReward> rewardsWon = new ArrayList<>();
    @Getter private List<Location> openedChests = new ArrayList<>();

    @EventHandler(priority = EventPriority.LOW)
    private void onInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();

        if (!event.getAction().name().contains("RIGHT")) {
            return;
        }

        final Block block = event.getClickedBlock();

        if (block == null || !block.getType().equals(Material.CHEST)) {
            return;
        }

        if (!DTRBitmask.SAFE_ZONE.appliesAt(block.getLocation())) {
            return;
        }

        if (currentlyOpening == null || !currentlyOpening.getChests().contains(block.getLocation())) {
            return;
        }

        if (currentPlayer == null || !currentPlayer.toString().equalsIgnoreCase(player.getUniqueId().toString())) {
            player.sendMessage(ChatColor.RED + "You may not open this chest someone else is using it!");
            return;
        }

        if (openedChests.contains(block.getLocation())) {
            player.sendMessage(ChatColor.RED + "You can't open that chest as you've already opened it!");
            return;
        }

        if (ending) {
            player.sendMessage(ChatColor.RED + "You've already opened them all");
            return;
        }

        event.setCancelled(true);

        final Chest chest = (Chest) block.getState();
        final TreasureChestReward treasureChestReward = this.getTreasureChestHandler().openChest(currentlyOpening, chest, player);
        chest.getWorld().playSound(chest.getLocation(), Sound.CHEST_OPEN, 1, 1);

        final String item = treasureChestReward.getItemStack().getItemMeta().hasDisplayName() ? treasureChestReward.getItemStack().getItemMeta().getDisplayName() : treasureChestReward.getItemStack().getAmount() + "x " + ItemUtils.getName(treasureChestReward.getItemStack());

        final Hologram hologram = Proton.getInstance().getHologramHandler().createHologram().addLines(ChatColor.translate("&4&lReward"), item).at(chest.getBlock().getRelative(BlockFace.UP).getLocation().add(0.5, 0, 0.5)).build();
        hologram.send();

        openedChests.add(chest.getLocation().clone());
        rewardsWon.add(treasureChestReward);
        chestsOpened++;

        if (chestsOpened >= 4) {
            this.end(player);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        if (currentPlayer == null || !currentPlayer.toString().equalsIgnoreCase(player.getUniqueId().toString())) {
            return;
        }

        this.end(player);
    }

    public void end(Player player) {
        ending = true;
        chestsOpened = 0;
        currentPlayer = null;

        this.instance.getServer().getScheduler().runTaskLater(this.instance, () -> {
            Foxtrot.getInstance().getServer().getWorld(Foxtrot.getInstance().getMapHandler().isKitMap() ? "world" : "Spawn").getEntities().stream().filter(it -> it instanceof Item && it.hasMetadata("TREASURE_REWARD")).forEach(Entity::remove);

            for (Location location : currentlyOpening.getChests()) {
                location.getBlock().setType(Material.AIR);

                location.getWorld().playSound(location, Sound.ZOMBIE_WOODBREAK, 1, 1);
            }

            final List<Hologram> holograms = new ArrayList<>(Proton.getInstance().getHologramHandler().getCache().values());

            holograms.stream().filter(it -> it.getLines().contains(ChatColor.translate("&4&lReward"))).forEach(Hologram::delete);

            String color = "&c";
            String link = "store.cavepvp.org/category/omega-chests";

            if (currentlyOpening.getId().equalsIgnoreCase("Illuminated")) {
                color = "&b";
                link = "store.cavepvp.org/category/illuminated-chests";
            }

            if (currentlyOpening.getId().equalsIgnoreCase("Treasure")) {
                color = "&c";
                link = "store.cavepvp.org/category/illuminated-chests";
            }

            for (Player onlinePlayer : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
                onlinePlayer.sendMessage(ChatColor.translate(color + "&l&m------&f&l&m------" + color + "&l&m------&f&l&m------" + color + "&l&m------&f&l&m------" + color + "&l&m------"));
                onlinePlayer.sendMessage(ChatColor.translate(color + "  &ki&4&l " + ChatColor.translate(currentlyOpening.getDisplayName()) + " " + color + "&ki"));
                onlinePlayer.sendMessage(ChatColor.translate("  &fOpened by " + color + player.getName()));
                onlinePlayer.sendMessage(ChatColor.translate(""));
                onlinePlayer.sendMessage(ChatColor.translate(color + "  &lREWARDS"));

                for (TreasureChestReward treasureChestReward : rewardsWon) {
                    final String item = treasureChestReward.getItemStack().getItemMeta().hasDisplayName() ? (treasureChestReward.getItemStack().getAmount() > 1 ?
                            treasureChestReward.getItemStack().getItemMeta().getDisplayName().replace(ChatColor.stripColor(treasureChestReward.getItemStack().getItemMeta().getDisplayName()), "")
                            + treasureChestReward.getItemStack().getAmount() + "x " : "") + treasureChestReward.getItemStack().getItemMeta().getDisplayName() :
                            treasureChestReward.getItemStack().getAmount() + "x " + ItemUtils.getName(treasureChestReward.getItemStack());

                    onlinePlayer.sendMessage(ChatColor.translate("   " + item));
                }

                onlinePlayer.sendMessage(ChatColor.translate(""));
                onlinePlayer.sendMessage(ChatColor.translate("  &fUnlocked at " + color + link));
                onlinePlayer.sendMessage(ChatColor.translate(color + "&l&m------&f&l&m------" + color + "&l&m------&f&l&m------" + color + "&l&m------&f&l&m------" + color + "&l&m------"));
            }

            rewardsWon.clear();

            final Location centralLocation = currentlyOpening.getCentralLocation().clone();

            final Hologram hologram = Proton.getInstance().getHologramHandler().createHologram().addLines(ChatColor.translate("&f&ki&4&l TREASURE CHESTS &f&ki"), ChatColor.translate("&f&ostore.cavepvp.org")).at(centralLocation.getBlock().getLocation().add(0.5, 0.4, 0.5)).build();
            hologram.send();

            centralLocation.getBlock().setType(Material.ENDER_CHEST);

            currentlyOpening = null;
        }, 20*5);
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onPickup(PlayerPickupItemEvent event) {
        if (event.getItem().hasMetadata("TREASURE_REWARD")) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onInteractChest(PlayerInteractEvent event) {
        final Player player = event.getPlayer();

        if (event.getAction() == Action.PHYSICAL) {
            return;
        }

        final Block block = event.getClickedBlock();

        if (block == null || !DTRBitmask.SAFE_ZONE.appliesAt(block.getLocation())) {
            return;
        }

        if (block.getType() != Material.ENDER_CHEST || block.getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).getType() != Material.DAYLIGHT_DETECTOR) {
            return;
        }

        if (currentlyOpening != null && currentlyOpening.getChests().contains(block.getLocation())) {
            return;
        }

        if (!event.getAction().name().contains("RIGHT") && !event.getAction().name().contains("LEFT")) {
            return;
        }

        if (currentPlayer != null && !currentPlayer.toString().equalsIgnoreCase(player.getUniqueId().toString())) {
            player.sendMessage(ChatColor.RED + "The Treasure Chest is already being used by someone!");
            return;
        }

        new TreasureChestMenu().openMenu(player);

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onMove(PlayerMoveEvent event) {
        final Player player = event.getPlayer();

        if (event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockY() == event.getTo().getBlockY() && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        if (currentlyOpening == null || currentlyOpening.getCentralLocation() == null || currentPlayer == null || !currentPlayer.toString().equalsIgnoreCase(player.getUniqueId().toString())) {
            return;
        }

        final Location location = currentlyOpening.getCentralLocation().clone();

        if (player.getLocation().distance(location.clone()) <= 3) {
            return;
        }

        location.setYaw(player.getLocation().getYaw());
        location.setPitch(player.getLocation().getPitch());

        event.setTo(location);
    }
}
