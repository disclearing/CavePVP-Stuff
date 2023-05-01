package cc.fyre.proton.nametag.listener;

import cc.fyre.proton.Proton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;

public final class NameTagListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().setMetadata("ProtonNametag-LoggedIn", new FixedMetadataValue(Proton.getInstance(), true));

        Proton.getInstance().getNameTagHandler().initiatePlayer(event.getPlayer());
        Proton.getInstance().getNameTagHandler().reloadPlayer(event.getPlayer());
        Proton.getInstance().getNameTagHandler().reloadOthersFor(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.getPlayer().removeMetadata("ProtonNametag-LoggedIn", Proton.getInstance());
        Proton.getInstance().getNameTagHandler().getTeamMap().remove(event.getPlayer().getName());
    }

}