package net.frozenorb.foxtrot.gameplay.lettingIn.listener;

import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.lettingIn.LettingInHandler;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.event.TeamRaidableEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

@AllArgsConstructor
public class LettingInListener implements Listener {
    private Foxtrot instance;
    private LettingInHandler lettingInHandler;

    @EventHandler(priority = EventPriority.LOW)
    private void onQuit(PlayerQuitEvent event) {
        final Player player = event.getPlayer();

        final Team team = this.instance.getTeamHandler().getTeam(player);

        if (team == null) {
            return;
        }

        if (team.getOnlineMemberAmount() == 0 || team.getOnlineMemberAmount()-1 == 0) {
            this.lettingInHandler.getCache().remove(team.getUniqueId());
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onRaidable(TeamRaidableEvent event) {
        this.lettingInHandler.getCache().remove(event.getTeam().getUniqueId());
    }

}
