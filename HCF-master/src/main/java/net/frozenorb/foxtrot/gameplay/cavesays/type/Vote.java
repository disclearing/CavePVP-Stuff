
package net.frozenorb.foxtrot.gameplay.cavesays.type;

import com.vexsoftware.votifier.model.VotifierEvent;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.cavesays.Task;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerChangedWorldEvent;

public class Vote extends Task {
    @Override
    public String getTaskDisplayName() {
        return "Vote for the Server (/vote)";
    }

    @Override
    public String getTaskID() {
        return "Vote";
    }

    @Override
    public int getPointsToWin() {
        return 1;
    }

    @EventHandler
    private void onWorldChange(VotifierEvent event) {
        final Player player = Foxtrot.getInstance().getServer().getPlayer(event.getVote().getUsername());

        if (player != null) {
            addProgress(player);
        }
    }
}