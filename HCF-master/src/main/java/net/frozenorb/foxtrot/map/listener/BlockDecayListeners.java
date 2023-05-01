package net.frozenorb.foxtrot.map.listener;

import net.minecraft.util.com.google.common.collect.Lists;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.LeavesDecayEvent;

import java.util.List;

public class BlockDecayListeners implements Listener {

    private List<Material> PREVENTED_STATES = Lists.newArrayList(Material.DIRT, Material.GRASS, Material.WATER, Material.STATIONARY_WATER, Material.ICE, Material.SNOW_BLOCK, Material.SNOW);

    @EventHandler
    public void onBlockFadeEvent(BlockFadeEvent event) {
        if (PREVENTED_STATES.contains(event.getNewState().getType())) {
            event.setCancelled(true);
        }

        final BlockState newState = event.getBlock().getState();

        if (newState.getType().name().contains("SNOW")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPhysicsEvent(BlockPhysicsEvent event) {
        if (event.getChangedType() == Material.GRASS) {
            event.setCancelled(true);
        }

        final BlockState newState = event.getBlock().getState();

        if (newState.getType().name().contains("SNOW") || event.getChangedType().name().contains("SNOW")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onSpread(BlockSpreadEvent event) {
        final BlockState newState = event.getNewState();

        if (newState.getType().name().contains("SNOW")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onLeavesDecayEvent(LeavesDecayEvent event) {
        event.setCancelled(true);
    }

}
