package net.frozenorb.foxtrot.listener;

import cc.fyre.proton.command.Command;
import cc.fyre.proton.command.param.Parameter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.type.TimeWarp;
import net.frozenorb.foxtrot.server.pearl.EnderpearlCooldownHandler;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.minecraft.server.v1_7_R4.EntityEnderPearl;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerPearlRefundEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Openable;

import java.util.Arrays;
import java.util.List;

public class PearlListener implements Listener {

    private List<Material> returnTypes = Arrays.asList(
            Material.LADDER, Material.BRICK_STAIRS, Material.SMOOTH_STAIRS, Material.WOOD_STAIRS, Material.SPRUCE_WOOD_STAIRS, Material.TORCH, Material.NETHER_BRICK_STAIRS,
            Material.QUARTZ_STAIRS, Material.BEDROCK, Material.CAKE, Material.STEP, Material.WOOD_STEP, Material.LEVER, Material.DAYLIGHT_DETECTOR, Material.COBBLE_WALL,
            Material.FENCE_GATE, Material.SIGN, Material.SIGN_POST, Material.WALL_SIGN, Material.STATIONARY_WATER, Material.WATER, Material.LAVA, Material.STATIONARY_LAVA,
            Material.STONE_PLATE, Material.WOOD_PLATE, Material.IRON_PLATE, Material.GOLD_PLATE
    );

    public static int ticks = 6;

    @EventHandler(priority = EventPriority.LOWEST)
    public void onRefund(PlayerPearlRefundEvent event) {
        final Player player = event.getPlayer();

        if (!player.isOnline()) {
            return;
        }

        final ItemStack itemStack = player.getItemInHand();

        if (itemStack != null && itemStack.getType() == Material.ENDER_PEARL && itemStack.getAmount() < 16) {
            itemStack.setAmount(itemStack.getAmount() + 1);
        } else {
            player.getInventory().addItem(new ItemStack(Material.ENDER_PEARL, 1));
        }

        player.updateInventory();

        EnderpearlCooldownHandler.removeCooldown(player);
    }

}