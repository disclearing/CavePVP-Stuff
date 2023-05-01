package cc.fyre.proton.bossbar;

import cc.fyre.proton.Proton;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class BossBarListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Proton.getInstance().getBossBarHandler().removeBossBar(event.getPlayer());
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {

        final Player player = event.getPlayer();

        if (!Proton.getInstance().getBossBarHandler().getDisplaying().containsKey(player.getUniqueId())) {
            return;
        }

        final BossBarData data = Proton.getInstance().getBossBarHandler().getDisplaying().get(player.getUniqueId());

        final String message = data.getMessage();
        final float health = data.getHealth();

        Proton.getInstance().getBossBarHandler().removeBossBar(player);
        Proton.getInstance().getBossBarHandler().setBossBar(player,message,health);

    }
}
