package net.frozenorb.foxtrot.gameplay.loot.battlepass.challenge.listener;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.listener.events.AbilityUseEvent;
import net.frozenorb.foxtrot.gameplay.events.Event;
import net.frozenorb.foxtrot.gameplay.events.EventType;
import net.frozenorb.foxtrot.gameplay.events.region.glowmtn.GlowHandler;
import net.frozenorb.foxtrot.gameplay.loot.battlepass.BattlePassProgress;
import net.frozenorb.foxtrot.listener.event.PlayerIncreaseKillEvent;
import net.frozenorb.foxtrot.persist.maps.event.SyncPlaytimeEvent;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.team.event.TeamRaidableEvent;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class ChallengeListeners implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerIncreaseKillEvent(PlayerIncreaseKillEvent event) {
        if (Foxtrot.getInstance().getBattlePassHandler().isAdminDisabled()) {
            return;
        }

        Foxtrot.getInstance().getBattlePassHandler().checkCompletionsAsync(event.getKiller());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreakEvent(BlockBreakEvent event) {
        if (Foxtrot.getInstance().getBattlePassHandler().isAdminDisabled()) {
            return;
        }

        Foxtrot.getInstance().getBattlePassHandler().useProgress(event.getPlayer(), progress -> {
            if (progress.isTrackingBlock(event.getBlock().getType())) {
                progress.incrementBlocksMined(event.getBlock().getType());
                Foxtrot.getInstance().getBattlePassHandler().checkCompletionsAsync(event.getPlayer());
            }
        });
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onSyncPlaytimeEvent(SyncPlaytimeEvent event) {
        if (Foxtrot.getInstance().getBattlePassHandler().isAdminDisabled()) {
            return;
        }

        Foxtrot.getInstance().getBattlePassHandler().checkCompletionsAsync(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerTeleportWorldEvent(PlayerTeleportEvent event) {
        if (Foxtrot.getInstance().getBattlePassHandler().isAdminDisabled()) {
            return;
        }

        if (event.getFrom().getWorld() != event.getTo().getWorld()) {
            if (event.getTo().getWorld().getEnvironment() == World.Environment.NETHER) {
                Foxtrot.getInstance().getBattlePassHandler().useProgress(event.getPlayer().getUniqueId(), progress -> {
                    progress.setVisitedNether(true);
                    progress.requiresSave();

                    Foxtrot.getInstance().getBattlePassHandler().checkCompletionsAsync(event.getPlayer());
                });
            } else if (event.getTo().getWorld().getEnvironment() == World.Environment.THE_END) {
                Foxtrot.getInstance().getBattlePassHandler().useProgress(event.getPlayer().getUniqueId(), progress -> {
                    progress.setVisitedEnd(true);
                    progress.requiresSave();

                    Foxtrot.getInstance().getBattlePassHandler().checkCompletionsAsync(event.getPlayer());
                });
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onRaidable(TeamRaidableEvent event) {
        final Player killer = event.getKiller();

        if (killer == null) {
            return;
        }

        Team killerTeam = Foxtrot.getInstance().getTeamHandler().getTeam(killer);

        if (killerTeam == null) {
            BattlePassProgress progress = Foxtrot.getInstance().getBattlePassHandler().getProgress(killer.getUniqueId());
            progress.setMadeFactionRaidable(true);
            progress.requiresSave();
        } else {
            for (Player teamPlayer : killerTeam.getOnlineMembers()) {
                Foxtrot.getInstance().getBattlePassHandler().useProgress(teamPlayer.getUniqueId(), progress -> {
                    progress.setMadeFactionRaidable(true);
                    progress.requiresSave();

                    Foxtrot.getInstance().getBattlePassHandler().checkCompletionsAsync(teamPlayer);
                });
            }
        }

    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMoveEvent(PlayerMoveEvent event) {
        if (Foxtrot.getInstance().getBattlePassHandler().isAdminDisabled()) {
            return;
        }

        Team team = LandBoard.getInstance().getTeam(event.getTo());
        if (team != null) {
            if (Foxtrot.getInstance().getGlowHandler() != null) {
                if (team.getName().equals(GlowHandler.getGlowTeamName())) {
                    Foxtrot.getInstance().getBattlePassHandler().useProgress(event.getPlayer().getUniqueId(), progress -> {
                        progress.setVisitGlowstoneMountain(true);
                        progress.requiresSave();

                        Foxtrot.getInstance().getBattlePassHandler().checkCompletionsAsync(event.getPlayer());
                    });

                    return;
                }
            }

            if (team.hasDTRBitmask(DTRBitmask.KOTH)) {
                for (Event gameEvent : Foxtrot.getInstance().getEventHandler().getEvents()) {
                    if (gameEvent.isActive() && gameEvent.getType() == EventType.KOTH && gameEvent.getName().equalsIgnoreCase(team.getName())) {
                        Foxtrot.getInstance().getBattlePassHandler().useProgress(event.getPlayer().getUniqueId(), progress -> {
                            progress.setVisitActiveKoth(true);
                            progress.requiresSave();

                            Foxtrot.getInstance().getBattlePassHandler().checkCompletionsAsync(event.getPlayer());
                        });
                        break;
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDeathEvent(EntityDeathEvent event) {
        if (Foxtrot.getInstance().getBattlePassHandler().isAdminDisabled()) {
            return;
        }

        if (event.getEntity().getKiller() != null) {
            Player player = event.getEntity().getKiller();

            Foxtrot.getInstance().getBattlePassHandler().useProgress(player.getUniqueId(), progress -> {
                if (progress.isTrackingKillsForEntity(event.getEntity().getType())) {
                    progress.incrementEntitiesKilled(event.getEntityType());
                    Foxtrot.getInstance().getBattlePassHandler().checkCompletionsAsync(player);
                }
            });
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onAbilityUse(AbilityUseEvent event) {
        if (event.isOneHit()) {
            return;
        }

        if (Foxtrot.getInstance().getBattlePassHandler() != null) {
            Foxtrot.getInstance().getBattlePassHandler().useProgress(event.getPlayer().getUniqueId(), progress -> {
                progress.setPartnerItemsUsed(progress.getPartnerItemsUsed() + 1);
                progress.requiresSave();

                Foxtrot.getInstance().getBattlePassHandler().checkCompletionsAsync(event.getPlayer());
            });
        }
    }

}
