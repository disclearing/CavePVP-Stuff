package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class FlyListener implements Listener {

    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();

        if (!player.getAllowFlight()) {
            return;
        }

        if (player.hasPermission("command.fly")) {
            return;
        }

        player.setAllowFlight(false);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onThankMove(PlayerMoveEvent event) {
        if (event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockY() == event.getTo().getBlockY() && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }

        final Player player = event.getPlayer();

        if (!player.getAllowFlight() || player.getGameMode().equals(GameMode.CREATIVE)) {
            return;
        }

        if (player.hasPermission("command.fly")) {
            return;
        }

        if (CustomTimerCreateCommand.isSOTWTimer()) {
            return;
        }

        player.setAllowFlight(false);
        player.sendMessage(ChatColor.RED + "Your flight mode has been disabled as SOTW Timer has been enabled!");
    }

}
