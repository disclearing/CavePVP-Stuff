package net.frozenorb.foxtrot.gameplay.cavesays.type;

import lombok.Getter;
import net.frozenorb.foxtrot.gameplay.cavesays.Task;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.team.event.TeamEnterClaimEvent;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.*;

public class GoKoth extends Task {
    @Override
    public String getTaskDisplayName() {
        return "Visit all Overworld KOTHs";
    }

    @Override
    public String getTaskID() {
        return "GoKOTH";
    }

    @Override
    public int getPointsToWin() {
        return 3;
    }

    @Getter private Map<UUID, List<Team>> enteredClaims = new HashMap<>();

    @EventHandler
    private void onClaimEnter(TeamEnterClaimEvent event) {
        final Player player = event.getPlayer();
        final Team toTeam = event.getToTeam();

        if (!DTRBitmask.KOTH.appliesAt(event.getTo())) {
            return;
        }

        if (player.getWorld().getEnvironment() != World.Environment.NORMAL) {
            player.sendMessage(ChatColor.RED + "You can only visit a Overworld KOTH for this task!");
            return;
        }

        final List<Team> teamsVisited = this.enteredClaims.getOrDefault(player.getUniqueId(), Collections.emptyList());

        if (teamsVisited.contains(toTeam)) {
            player.sendMessage(ChatColor.RED + "You've already visited this KOTH!");
            return;
        }
        teamsVisited.add(toTeam);

        this.enteredClaims.put(player.getUniqueId(), teamsVisited);

        this.addProgress(player);
    }
}