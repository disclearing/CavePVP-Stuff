package net.frozenorb.foxtrot.gameplay.loot.battlepass.listener;

import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class BattlePassListeners implements Listener {

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        if (Foxtrot.getInstance().getBattlePassHandler().isAdminDisabled()) {
            return;
        }

        Bukkit.getServer().getScheduler().runTaskAsynchronously(Foxtrot.getInstance(), () -> Foxtrot.getInstance().getBattlePassHandler().loadProgress(event.getPlayer().getUniqueId()));
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        if (Foxtrot.getInstance().getBattlePassHandler().isAdminDisabled()) {
            return;
        }

        Bukkit.getServer().getScheduler().runTaskAsynchronously(Foxtrot.getInstance(), () -> Foxtrot.getInstance().getBattlePassHandler().unloadProgress(event.getPlayer().getUniqueId()));
    }

}
