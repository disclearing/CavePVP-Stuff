package net.frozenorb.foxtrot.gameplay.events.citadel.listeners;

import cc.fyre.proton.event.HourEvent;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.events.citadel.events.CitadelActivatedEvent;
import net.frozenorb.foxtrot.gameplay.events.events.EventActivatedEvent;
import net.frozenorb.foxtrot.gameplay.events.events.EventCapturedEvent;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class CitadelListener implements Listener {

    @EventHandler
    public void onKOTHActivated(EventActivatedEvent event) {
        if (event.getEvent().getName().contains("Citadel")) {
            Foxtrot.getInstance().getServer().getPluginManager().callEvent(new CitadelActivatedEvent());
        }
    }

    @EventHandler
    public void onHour(HourEvent event) {
        if (event.getHour() % 6 == 0) {
            Foxtrot.getInstance().getCitadelHandler().respawnCitadelChests();
        }
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onKOTHCaptured(EventCapturedEvent event) {
        if (event.getEvent().getName().contains("Citadel")) {
            Team playerTeam = Foxtrot.getInstance().getTeamHandler().getTeam(event.getPlayer());

            if (playerTeam != null) {
                playerTeam.setCitadelsCapped(playerTeam.getCitadelsCapped() + 1);
                Foxtrot.getInstance().getCitadelHandler().addCapper(playerTeam.getUniqueId());
            }
        }
    }
}