package net.frozenorb.foxtrot.gameplay.cavesays.type;

import net.frozenorb.foxtrot.gameplay.cavesays.Task;
import net.frozenorb.foxtrot.team.event.TeamRaidableEvent;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class MakeAFactionRaidable extends Task {
    @Override
    public String getTaskDisplayName() {
        return "Make a Faction Raidable";
    }

    @Override
    public String getTaskID() {
        return "Raidable";
    }

    @Override
    public int getPointsToWin() {
        return 1;
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onRaidable(TeamRaidableEvent event) {
        if (event.getKiller() != null) {
            return;
        }

        this.addProgress(event.getKiller());
    }
}
