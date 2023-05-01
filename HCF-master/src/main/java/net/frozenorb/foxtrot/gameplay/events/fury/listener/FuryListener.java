package net.frozenorb.foxtrot.gameplay.events.fury.listener;

import cc.fyre.proton.util.UUIDUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.events.conquest.ConquestHandler;
import net.frozenorb.foxtrot.gameplay.events.conquest.enums.ConquestCapzone;
import net.frozenorb.foxtrot.gameplay.events.events.EventCapturedEvent;
import net.frozenorb.foxtrot.gameplay.events.fury.FuryCapZone;
import net.frozenorb.foxtrot.gameplay.events.fury.FuryHandler;
import net.frozenorb.foxtrot.gameplay.events.koth.events.EventControlTickEvent;
import net.frozenorb.foxtrot.gameplay.events.koth.events.KOTHControlLostEvent;
import net.frozenorb.foxtrot.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;

@AllArgsConstructor
public class FuryListener implements Listener {

    @Getter private Foxtrot instance;
    @Getter private FuryHandler furyHandler;

    @EventHandler(priority = EventPriority.LOW)
    private void onCapture(EventCapturedEvent event) {
        if (!event.getEvent().getName().startsWith("Fury_")) {
            return;
        }

        final Team team = Foxtrot.getInstance().getTeamHandler().getTeam(event.getPlayer());
        final FuryCapZone capZone = FuryCapZone.valueOf(event.getEvent().getName().replace("Fury_", "").toUpperCase());

        if (team == null) {
            this.instance.getServer().getScheduler().runTaskLater(this.instance, () -> {
                if (Foxtrot.getInstance().getFuryHandler().isActive()) {
                    event.getEvent().activate();
                }
            }, 10);
            return;
        }

        this.furyHandler.getTeamPoints().put(team.getUniqueId(), this.furyHandler.getTeamPoints().getOrDefault(team.getUniqueId(), 0)+1);

        this.furyHandler.setTeamPoints(this.furyHandler.sortByValues(this.furyHandler.getTeamPoints()));

        this.instance.getServer().broadcastMessage(FuryHandler.PREFIX + " " + ChatColor.YELLOW + team.getName() + ChatColor.GOLD + " captured " + capZone.getChatColor() + capZone.getDisplayName() + ChatColor.GOLD + " and earned " + ChatColor.WHITE + "1 point" + ChatColor.GOLD + "!" + ChatColor.RED + " (" + this.furyHandler.getTeamPoints().get(team.getUniqueId()) + "/" + 150 + ")");

        if (this.furyHandler.getTeamPoints().get(team.getUniqueId()) >= 150) {
            this.furyHandler.endGame(team);
        } else {
            new BukkitRunnable() {
                public void run() {
                    if (Foxtrot.getInstance().getFuryHandler().isActive()) {
                        event.getEvent().activate();
                    }
                }

            }.runTaskLater(Foxtrot.getInstance(), 10L);
        }
    }


    @EventHandler
    public void onKOTHControlLost(KOTHControlLostEvent event) {
        if (!event.getKOTH().getName().startsWith("Fury_")) {
            return;
        }

        final Team team = Foxtrot.getInstance().getTeamHandler().getTeam(UUIDUtils.uuid(event.getKOTH().getCurrentCapper()));
        final FuryCapZone furyCapZone = this.furyHandler.getFuryCapZone();

        if (team == null) {
            return;
        }

        team.sendMessage(FuryHandler.PREFIX + ChatColor.YELLOW + " " + event.getKOTH().getCurrentCapper() + ChatColor.GOLD + " was knocked off of " + furyCapZone.getChatColor() + furyCapZone.getDisplayName() + ChatColor.GOLD + "!");
    }
    @EventHandler
    public void onKOTHControlTick(EventControlTickEvent event) {

        if (!event.getKOTH().getName().startsWith("Fury_") || event.getKOTH().getRemainingCapTime() % 5 != 0) {
            return;
        }

        final Player capper = Foxtrot.getInstance().getServer().getPlayerExact(event.getKOTH().getCurrentCapper());
        final FuryCapZone furyCapZone = this.furyHandler.getFuryCapZone();

        if (capper != null) {
            capper.sendMessage(FuryHandler.PREFIX + " " + ChatColor.GOLD + "Attempting to capture " + furyCapZone.getChatColor() + furyCapZone.getDisplayName() + ChatColor.GOLD + "!" + ChatColor.RED + " (" + event.getKOTH().getRemainingCapTime() + "s)");
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (Foxtrot.getInstance().getDeathbanArenaHandler().isDeathbanArena(event.getEntity())) {
            return;
        }
        this.furyHandler.death(event.getEntity());
    }


}
