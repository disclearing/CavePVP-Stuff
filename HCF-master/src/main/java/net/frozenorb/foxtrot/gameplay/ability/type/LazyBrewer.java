package net.frozenorb.foxtrot.gameplay.ability.type;

import com.sk89q.worldedit.CuboidClipboard;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.schematic.MCEditSchematicFormat;
import com.sk89q.worldedit.world.DataException;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.Ability;
import net.frozenorb.foxtrot.gameplay.ability.Category;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.util.DirectionUtil;
import net.frozenorb.foxtrot.util.SafetyUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LazyBrewer extends Ability {

    final File file = new File(System.getProperty("user.dir") + "/plugins/WorldEdit/schematics/lazybrewer.schematic");

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public Material getMaterial() {
        return Material.BREWING_STAND_ITEM;
    }

    @Override
    public String getDisplayName() {
        return ChatColor.LIGHT_PURPLE.toString() + ChatColor.BOLD + "Lazy Brewer";
    }

    @Override
    public List<String> getLore() {
        final List<String> toReturn = new ArrayList<>();

        toReturn.add("");
        toReturn.add(ChatColor.translate("&6❙ &fSpawn in 3 double chests of chests with pots."));
        toReturn.add("");
        toReturn.add(ChatColor.translate("&fCan be found in the &6&lOctober Mystery Box&f!"));

        return toReturn;
    }

    @Override
    public Boolean isAllowedAtLocation(Location location) {
        return !DTRBitmask.KOTH.appliesAt(location) && !DTRBitmask.CONQUEST.appliesAt(location) && !DTRBitmask.CITADEL.appliesAt(location) && !DTRBitmask.DTC.appliesAt(location) && !DTRBitmask.SAFE_ZONE.appliesAt(location);
    }

    @Override
    public long getCooldown() {
        return 0L;
    }

    @Override
    public Category getCategory() {
        return Category.AIRDROPS;
    }

    @Override
    public String getDescription() {
        return "Spawned a bunch of potions!";
    }

    @EventHandler
    private void onInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();

        if (!this.isSimilar(player.getItemInHand()) || event.getAction() != Action.RIGHT_CLICK_AIR) {
            return;
        }

        if (this.hasCooldown(player)) {
            event.setCancelled(true);
            return;
        }

        List<Location> surroundingLocations = SafetyUtils.getSurroundingBlocks(player.getLocation());

        final Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player.getUniqueId());

        boolean notSafe = surroundingLocations.stream().anyMatch(loc -> !SafetyUtils.isPassable(loc.getBlock()) || team == null || LandBoard.getInstance().getTeam(player.getLocation()) != team);

        if (notSafe) {
            player.sendMessage(ChatColor.RED + "You may not do that here.");
            return;
        }

        if (player.getItemInHand().getAmount() == 1) {
            player.setItemInHand(null);
        } else {
            player.getItemInHand().setAmount(player.getItemInHand().getAmount()-1);
        }
        player.updateInventory();

        pasteSchematic(player.getLocation(), DirectionUtil.getDirection(player), file);
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onPlace(BlockPlaceEvent event) {
        final Player player = event.getPlayer();

        if (!this.isSimilar(event.getItemInHand())) {
            return;
        }

        if (this.hasCooldown(player)) {
            event.setCancelled(true);
            return;
        }

        event.setCancelled(true);

        List<Location> surroundingLocations = SafetyUtils.getSurroundingBlocks(player.getLocation());
        final Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player.getUniqueId());

        boolean allow = surroundingLocations.stream().anyMatch(loc -> !SafetyUtils.isPassable(loc.getBlock()) || team == null || LandBoard.getInstance().getTeam(player.getLocation()) != team);

        if (allow) {
            player.sendMessage(ChatColor.RED + "You may not do that here.");
            return;
        }

        if (player.getItemInHand().getAmount() == 1) {
            player.setItemInHand(null);
        } else {
            player.getItemInHand().setAmount(player.getItemInHand().getAmount()-1);
        }
        player.updateInventory();

        pasteSchematic(player.getLocation(), DirectionUtil.getDirection(player), file);
    }

    private void pasteSchematic(Location location, DirectionUtil directionUtils, File file) {
        BukkitWorld world = new BukkitWorld(location.getWorld());
        try {
            CuboidClipboard cuboidSelection = MCEditSchematicFormat.getFormat(file).load(file);
            cuboidSelection.rotate2D(directionUtils.getDegrees());
            cuboidSelection.paste(Foxtrot.getInstance().getWorldEdit().getWorldEdit().getEditSessionFactory().getEditSession(world, 100), BukkitUtil.toVector(location), true);
        } catch (IOException | DataException | MaxChangedBlocksException e) {
            e.printStackTrace();
        }
    }

}
