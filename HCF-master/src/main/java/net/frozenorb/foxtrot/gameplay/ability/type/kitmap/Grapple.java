package net.frozenorb.foxtrot.gameplay.ability.type.kitmap;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.Ability;
import net.frozenorb.foxtrot.gameplay.ability.Category;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

public class Grapple extends Ability {

    public static short DURABILITY_LOSS = 6;
    public static double VELOCITY_CHANGE = 0.25;

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public Material getMaterial() {
        return Material.FISHING_ROD;
    }

    @Override
    public String getDisplayName() {
        return ChatColor.GOLD.toString() + ChatColor.BOLD + this.getName();
    }

    @Override
    public List<String> getLore() {

        final List<String> toReturn = new ArrayList<>();

        toReturn.add(ChatColor.GRAY + "Hook the block you'd like to");
        toReturn.add(ChatColor.GRAY + "move towards and then release to get sent!");
        toReturn.add("");
        if (Foxtrot.getInstance().getMapHandler().isKitMap()) {
            toReturn.add(ChatColor.translate("&fCan be found in an &c&lMario Crate&f!"));
        } else {
            toReturn.add(ChatColor.translate("&fCan be found in an &b&lAir Drop&f!"));
        }

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
        return 30_000L;
    }

    @Override
    public Category getCategory() {
        if (!Foxtrot.getInstance().getMapHandler().isKitMap()) {
            return Category.AIRDROPS;
        }

        return Category.KIT_MAP;
    }

    @Override
    public String getDescription() {
        return "You have been boosted towards that block!";
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteract(PlayerInteractEvent event) {

        final Player player = event.getPlayer();

        if (!this.isSimilar(player.getItemInHand())) {
            return;
        }

        if (this.hasCooldown(player)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    private void onPlayerFish(PlayerFishEvent event) {

        if (event.isCancelled()) {
            return;
        }

        final Player player = event.getPlayer();

        if (!this.isSimilar(player.getItemInHand())) {
            return;
        }

        if (event.getState() == PlayerFishEvent.State.FISHING) {
            return;
        }

        final Entity hooked = event.getHook();

        if (hooked == null) {
            return;
        }

        if (event.getCaught() != null) {
            event.getHook().remove();
            return;
        }

        final Location location = player.getLocation();

        final Location hookedLocation = hooked.getLocation();

        if (!this.isAllowedAtLocation(hookedLocation)) {
            String teamName;

            final Team ownerTeam = LandBoard.getInstance().getTeam(hookedLocation);

            if (ownerTeam != null) {
                teamName = ownerTeam.getName(event.getPlayer());
            } else if (!Foxtrot.getInstance().getServerHandler().isWarzone(hookedLocation)) {
                teamName = ChatColor.GRAY + "The Wilderness";
            } else {
                teamName = ChatColor.DARK_RED + "WarZone";
            }

            event.getPlayer().sendMessage(ChatColor.RED + "You cannot use a " + this.getDisplayName() + ChatColor.RED + " in " + teamName + ChatColor.RED + ".");
            return;
        }

        if (!(this.canGrapple(hookedLocation.getBlock()) || this.canGrapple((hookedLocation.getBlock().getRelative(BlockFace.UP))) || this.canGrapple(hookedLocation.getBlock().getRelative(BlockFace.DOWN)))) {
            return;
        }

        player.getWorld().playSound(player.getPlayer().getLocation(), Sound.ZOMBIE_INFECT, 0.5F, 1.8F);

        final Vector velocity = new Vector(
                hookedLocation.getX() - location.getX(),
                hookedLocation.getY() - location.getY(),
                hookedLocation.getZ() - location.getZ()
        ).multiply(VELOCITY_CHANGE);

        player.setVelocity(velocity);
        player.setMetadata("noflag", new FixedMetadataValue(Foxtrot.getInstance(), true));

        Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
            if (player.isOnline()) {
                player.removeMetadata("noflag", Foxtrot.getInstance());
            }
        }, 20 * 3L);
        event.setExpToDrop(0);

        hooked.remove();

        if (event.getCaught() != null && !(event.getCaught() instanceof LivingEntity)) {
            event.getCaught().remove();
        }

        if ((player.getItemInHand().getDurability() + DURABILITY_LOSS) >= player.getItemInHand().getType().getMaxDurability()) {
            player.setItemInHand(null);
            player.getWorld().playSound(player.getLocation(), Sound.ITEM_BREAK, 1F, 1F);
        } else {
            player.getItemInHand().setDurability((short) (player.getItemInHand().getDurability() + DURABILITY_LOSS));
        }

        this.applyCooldown(player);
    }

    private boolean canGrapple(Block block) {
        return !(block.getTypeId() == 0 || block.getType() == Material.STATIONARY_WATER || block.getType() == Material.WATER);
    }
}