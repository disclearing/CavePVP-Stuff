package net.frozenorb.foxtrot.gameplay.loot.voteparty.listener;

import com.vexsoftware.votifier.model.VotifierEvent;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class VoteListener implements Listener {
    @EventHandler(priority = EventPriority.LOW)
    private void onVote(VotifierEvent event) {
        Foxtrot.getInstance().getVotePartyHandler().addVote();
    }

}
