package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.gameplay.ability.type.AntiBlockup;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.Elevator;
import net.minecraft.server.v1_7_R4.Vec3D;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ElevatorsListener implements Listener {

    private final List<String> elevatorDirections = Arrays.asList(Arrays.stream(Elevator.values()).map(Elevator::name).map(String::toLowerCase).collect(Collectors.joining()));
    private final List<Material> signMaterials = Arrays.asList(Material.SIGN_POST, Material.WALL_SIGN);

    @EventHandler(priority = EventPriority.NORMAL)
    public void onSignInteract(PlayerInteractEvent event) {

        final Player player = event.getPlayer();

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || !this.signMaterials.contains(event.getClickedBlock().getType())) {
            return;
        }

        final BlockState blockState = event.getClickedBlock().getState();

        if (!(blockState instanceof Sign)) {
            return;
        }

        final Sign sign = (Sign) blockState;

        if (!sign.getLine(0).contains("[Elevator]")) {
            return;
        }

        final Block block = player.getTargetBlock(null,(int)sign.getLocation().distance(player.getLocation()));

        if (block != null && !(block.getState() instanceof Sign)) {
            return;
        }

        Elevator elevator;

        try {
            elevator = Elevator.valueOf(ChatColor.stripColor(sign.getLine(1).toUpperCase()));
        } catch (IllegalArgumentException ex) {
            player.sendMessage(ChatColor.RED + "Invalid elevator direction, try UP or DOWN.");
            ex.printStackTrace();
            return;
        }

        if (player.hasMetadata("ANTI_ELEVATOR")) {
            player.sendMessage(ChatColor.translate("&cYou may not use elevator signs as someone used the &e&lElevator Jammer&c!"));
            return;
        }

        if (AntiBlockup.getCache().containsKey(player.getUniqueId())) {
            player.sendMessage(ChatColor.translate("&cYou may not use elevator signs whilst on &6&lAnti-Blockup&c!"));
            return;
        }

        if (elevator == null) {
            player.sendMessage(ChatColor.RED + "Invalid elevator direction, try UP or DOWN.");
            return;
        }

        final Location toTeleport = elevator.getCalculatedLocation(sign.getLocation(), Elevator.Type.SIGN);

        if (toTeleport == null) {
            player.sendMessage(ChatColor.RED + "There was an issue trying to find a valid location!");
            return;
        }

        toTeleport.setYaw(player.getLocation().getYaw());
        toTeleport.setPitch(player.getLocation().getPitch());
        player.teleport(toTeleport.add(0.5,0,0.5));
    }

    @EventHandler
    public void onSignUse(final PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getClickedBlock() != null && (event.getClickedBlock().getType() == Material.SIGN || event.getClickedBlock().getType() == Material.SIGN_POST || event.getClickedBlock().getType() == Material.WALL_SIGN)) {
            final Sign sign = (Sign)event.getClickedBlock().getState();
            if (!sign.getLine(0).equals(ChatColor.BLUE + "[Elevator]") || (!sign.getLine(1).equals("Up") && !sign.getLine(1).equals("Down"))) {
                return;
            }
            if (!this.canSee(event.getPlayer(), sign.getBlock().getLocation())) {
                return;
            }

            final Player player = event.getPlayer();

            if (player.hasMetadata("ANTI_ELEVATOR")) {
                player.sendMessage(ChatColor.translate("&cYou may not use elevator signs as someone used the &e&lElevator Jammer&c!"));
                return;
            }

            if (AntiBlockup.getCache().containsKey(player.getUniqueId())) {
                player.sendMessage(ChatColor.translate("&cYou may not use elevator signs whilst on &6&lAnti-Blockup&c!"));
                return;
            }

            final boolean up = sign.getLine(1).equals("Up");
            final int x = event.getClickedBlock().getX();
            final int z = event.getClickedBlock().getZ();
            final World world = event.getClickedBlock().getWorld();
            boolean foundFirst = false;
            Location location = null;
            for (int y = event.getClickedBlock().getY() + (up ? 1 : -1); y < world.getMaxHeight() && y > 0; y += (up ? 1 : -1)) {
                final Block block = world.getBlockAt(x, y, z);
                if (block != null && !this.canGoThrough(block.getType())) {
                    if (!up && !foundFirst) {
                        foundFirst = true;
                    }
                    else {
                        final Block up2 = world.getBlockAt(x, y + 1, z);
                        final Block up3 = world.getBlockAt(x, y + 2, z);
                        if (up2 != null && up3 != null && this.canGoThrough(up2.getType()) && this.canGoThrough(up3.getType())) {
                            location = new Location(event.getClickedBlock().getWorld(), x, y + 1, z);
                            break;
                        }
                    }
                }
            }
            if (location == null) {
                event.getPlayer().sendMessage(ChatColor.translate("&cCan't find position to teleport."));
            }
            else {
                location.setYaw(event.getPlayer().getLocation().getYaw());
                location.setPitch(event.getPlayer().getLocation().getPitch());
                location.setX(location.getX() + 0.5);
                location.setZ(location.getZ() + 0.5);
                event.getPlayer().teleport(location);
            }
        }
    }

    public boolean canGoThrough(final Material material) {
        if (material.isTransparent()) {
            return true;
        }
        switch (material) {
            case SIGN_POST:
            case WALL_SIGN:
            case SIGN: {
                return true;
            }
            default: {
                return false;
            }
        }
    }

    public boolean canSee(final Player player, final Location loc2) {
        final Location loc3 = player.getLocation();
        return ((CraftWorld)loc3.getWorld()).getHandle().a(Vec3D.a(loc3.getX(), loc3.getY() + player.getEyeHeight(), loc3.getZ()), Vec3D.a(loc2.getX(), loc2.getY(), loc2.getZ())) == null;
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        if (event.getLine(0).equalsIgnoreCase("[Elevator]") && event.getLine(1).equalsIgnoreCase("Up")) {
            event.setLine(0, CC.translate("&9[Elevator]"));
            event.setLine(1, "Up");
        }
        if (event.getLine(0).equalsIgnoreCase("[Elevator]") && event.getLine(1).equalsIgnoreCase("Down")) {
            event.setLine(0, CC.translate("&9[Elevator]"));
            event.setLine(1, "Down");
        }
    }
}