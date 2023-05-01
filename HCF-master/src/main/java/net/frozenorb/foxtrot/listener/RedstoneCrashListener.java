package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.Foxtrot;
import net.valorhcf.ThreadingManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RedstoneCrashListener implements Listener {

    private Foxtrot instance;
    private Map<UUID, Integer> alerts = new HashMap<>();

    public RedstoneCrashListener(Foxtrot instance) {
        this.instance = instance;

        this.instance.getServer().getScheduler().runTaskTimer(this.instance, () -> alerts.clear(), 20*300L, 20*300L);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        final Block block = event.getClickedBlock();
        final Player player = event.getPlayer();

        final Integer[] ticksPerSecond = ThreadingManager.getTickCounter().getTicksPerSecond();
        final Integer finalTicks = ticksPerSecond[ticksPerSecond.length - 1];

        if (finalTicks > 17 || block.getType() != Material.LEVER) {
            return;
        }

        int uses = alerts.getOrDefault(player.getUniqueId(), 0);
        uses++;

        alerts.put(player.getUniqueId(), uses);

        event.setCancelled(true);
        player.sendMessage(ChatColor.RED + "Server is currently lagging, you may not use levers at this time.");

        System.out.println(player.getName() + " flicked a lever while server was lagging");
    }

}