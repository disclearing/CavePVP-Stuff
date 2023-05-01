package net.frozenorb.foxtrot.deathmessage.listeners;

import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.minecraft.util.com.google.common.collect.Maps;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.commands.CustomTimerCreateCommand;
import net.frozenorb.foxtrot.commands.EOTWCommand;
import net.frozenorb.foxtrot.commands.LastInvCommand;
import net.frozenorb.foxtrot.deathmessage.DeathMessageHandler;
import net.frozenorb.foxtrot.deathmessage.event.CustomPlayerDamageEvent;
import net.frozenorb.foxtrot.deathmessage.event.PlayerKilledEvent;
import net.frozenorb.foxtrot.deathmessage.objects.Damage;
import net.frozenorb.foxtrot.deathmessage.objects.PlayerDamage;
import net.frozenorb.foxtrot.deathmessage.util.UnknownDamage;
import net.frozenorb.foxtrot.gameplay.kitmap.killstreaks.Killstreak;
import net.frozenorb.foxtrot.gameplay.kitmap.killstreaks.PersistentKillstreak;
import net.frozenorb.foxtrot.gameplay.extra.stats.StatsEntry;
import net.frozenorb.foxtrot.listener.event.PlayerIncreaseKillEvent;
import net.frozenorb.foxtrot.server.pearl.EnderpearlCooldownHandler;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.cavepvp.profiles.Profiles;
import org.cavepvp.profiles.playerProfiles.PlayerProfileAPI;
import org.cavepvp.profiles.playerProfiles.impl.stats.StatisticType;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class DamageListener implements Listener {

    // kit-map only
    private Map<UUID, UUID> lastKilled = Maps.newHashMap();
    private Map<UUID, Integer> boosting = Maps.newHashMap();

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            CustomPlayerDamageEvent customEvent = new CustomPlayerDamageEvent(event, new UnknownDamage(player.getName(), event.getDamage()));

            Foxtrot.getInstance().getServer().getPluginManager().callEvent(customEvent);
            DeathMessageHandler.addDamage(player, customEvent.getTrackerDamage());
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        DeathMessageHandler.clearDamage(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.setDeathMessage(null);

        LastInvCommand.recordInventory(event.getEntity());
        EnderpearlCooldownHandler.clearEnderpearlTimer(event.getEntity());

        if (Foxtrot.getInstance().getDeathbanArenaHandler().isDeathbanArena(event.getEntity()) || event.getEntity().getKiller() != null && Foxtrot.getInstance().getDeathbanArenaHandler().isDeathbanArena(event.getEntity().getKiller())) {
            return;
        }

        final List<Damage> record = DeathMessageHandler.getDamage(event.getEntity());
        final Player victim = event.getEntity();
        final Team victimTeam = Foxtrot.getInstance().getTeamHandler().getTeam(victim);

        String deathMessage;

        boolean killBoosting = false;

        if (record != null && !record.isEmpty()) {
            Damage deathCause = record.get(record.size() - 1);

            if (deathCause instanceof PlayerDamage && deathCause.getTimeDifference() < TimeUnit.MINUTES.toMillis(1)) {
                String killerName = ((PlayerDamage) deathCause).getDamager();
                Player killer = Foxtrot.getInstance().getServer().getPlayerExact(killerName);

                if (killer != null && !(Foxtrot.getInstance().getInDuelPredicate().test(event.getEntity()) || Foxtrot.getInstance().getInEventPredicate().test(event.getEntity()))) {
                    ((CraftPlayer) event.getEntity()).getHandle().killer = ((CraftPlayer) killer).getHandle();

                    Foxtrot.getInstance().getServer().getPluginManager().callEvent(new PlayerKilledEvent(killer, victim));

                    if (lastKilled.containsKey(killer.getUniqueId()) && lastKilled.get(killer.getUniqueId()) == victim.getUniqueId()) {
                        boosting.putIfAbsent(killer.getUniqueId(), 0);
                        boosting.put(killer.getUniqueId(), boosting.get(killer.getUniqueId()) + 1);
                    } else {
                        boosting.put(killer.getUniqueId(), 0);
                    }

                    final StatsEntry victimStats = Foxtrot.getInstance().getMapHandler().getStatsHandler().getStats(victim);
                    victimStats.addDeath();

                    Profiles.getInstance().getReputationHandler().takeReputation(victim.getUniqueId(), victim.getName(), 1);

                    if (killer.equals(victim)) {
                        killBoosting = true;
                    } else if (killer.getAddress().getAddress().getHostAddress().equalsIgnoreCase(victim.getAddress().getAddress().getHostAddress())) {
                        killBoosting = true;

                        killer.sendMessage(ChatColor.RED + "Boost Check: You've killed a player on the same IP address as you.");
                    } else if (boosting.containsKey(killer.getUniqueId()) && boosting.get(killer.getUniqueId()) > 1) {
                        killBoosting = true;

                        killer.sendMessage(ChatColor.RED + "Boost Check: You've killed " + victim.getName() + " " + boosting.get(killer.getUniqueId()) + " times.");
                    } else if (victimTeam != null && victimTeam.isRaidable()) {
                        killBoosting = true;

                        killer.sendMessage(ChatColor.RED + "Boost Check: This player is raidable!");
                    } else if (Foxtrot.getInstance().getServerHandler().isEOTW()) {
                        killBoosting = true;

                        killer.sendMessage(ChatColor.RED + "Boost Check: You can't gain Kills during EOTW!");
                    } else {
                        final StatsEntry killerStats = Foxtrot.getInstance().getMapHandler().getStatsHandler().getStats(killer);
                        killerStats.addKill();

                        lastKilled.put(killer.getUniqueId(), victim.getUniqueId());

                        if (Foxtrot.getInstance().getMapHandler().isKitMap()) {
                            long gemAmount = Foxtrot.getInstance().getGemMap().addGems(killer.getUniqueId(), 1);

                            killer.sendMessage(CC.GREEN + "You earned " + CC.DARK_GREEN + "+" + gemAmount + CC.GREEN + " gem" + (gemAmount == 1 ? "" : "s") + "!");

                            int killStreakCount = Foxtrot.getInstance().getKillstreakMap().getKillstreak(killer.getUniqueId());
                            final Killstreak killstreak = Foxtrot.getInstance().getMapHandler().getKillstreakHandler().check(killStreakCount);

                            if (killstreak != null) {
                                killstreak.apply(killer);
                                killstreak.apply(killer, killStreakCount);

                                if (killstreak.getName() != null) {
                                    Bukkit.broadcastMessage(killer.getDisplayName() + ChatColor.YELLOW + " has gotten the " + ChatColor.RED + killstreak.getName() + ChatColor.YELLOW + " killstreak!");
                                }

                                final List<PersistentKillstreak> persistent = Foxtrot.getInstance().getMapHandler().getKillstreakHandler().getPersistentKillstreaks(killer, killStreakCount);

                                for (PersistentKillstreak persistentStreak : persistent) {
                                    if (persistentStreak.matchesExactly(killStreakCount)) {
                                        if (killstreak.getName() != null) {
                                            Bukkit.broadcastMessage(killer.getName() + ChatColor.YELLOW + " has gotten the " + ChatColor.RED + killstreak.getName() + ChatColor.YELLOW + " killstreak!");
                                        }
                                    }

                                    persistentStreak.apply(killer);
                                }

                                if (killStreakCount >= 100) {
                                    Foxtrot.getInstance().getKillstreakMap().setKillstreak(killer.getUniqueId(), 0);
                                }
                            }
                        }

                        Foxtrot.getInstance().getKillsMap().setKills(killer.getUniqueId(), Foxtrot.getInstance().getKillsMap().getKills(killer.getUniqueId())+1);
                        Foxtrot.getInstance().getKillstreakMap().setKillstreak(killer.getUniqueId(), Foxtrot.getInstance().getKillstreakMap().getKillstreak(killer.getUniqueId())+1);
                        Foxtrot.getInstance().getDeathsMap().setDeaths(victim.getUniqueId(), Foxtrot.getInstance().getDeathsMap().getDeaths(victim.getUniqueId())+1);
                        Profiles.getInstance().getReputationHandler().addReputation(killer.getUniqueId(), killer.getName(), 1);

                        if (!Foxtrot.getInstance().getServerHandler().isAu()) {
                            PlayerProfileAPI.addStatistic(killer.getUniqueId(), Foxtrot.getInstance().getStatisticServer(), StatisticType.KILLS, 1);
                            PlayerProfileAPI.addStatistic(victim.getUniqueId(), Foxtrot.getInstance().getStatisticServer(), StatisticType.DEATHS, 1);
                        }

                        Bukkit.getPluginManager().callEvent(new PlayerIncreaseKillEvent(killer));

                        if (Foxtrot.getInstance().getServerHandler().isTeams()) {
                            event.getDrops().add(Foxtrot.getInstance().getServerHandler().generateDeathSign(event.getEntity().getName(), killer.getName()));
                        }

                    }
                }
            }

            deathMessage = deathCause.getDeathMessage();
        } else {
            deathMessage = new UnknownDamage(event.getEntity().getName(), 1).getDeathMessage();
        }

        final Player killer = event.getEntity().getKiller();
        final Team killerTeam = killer == null ? null : Foxtrot.getInstance().getTeamHandler().getTeam(killer);

        if (killerTeam != null && !killBoosting) {
            killerTeam.setKills(killerTeam.getKills() + 1);
            killerTeam.recalculateGems();
            if (CustomTimerCreateCommand.isDoublePoints() || killerTeam.hasEndOutpost()) {
                killerTeam.setDoublePoints(killerTeam.getDoublePoints() + 1);
            }

            final Team teamAt = LandBoard.getInstance().getTeam(killer.getLocation());

            if (teamAt != null && teamAt.equals(killerTeam) && killerTeam.getDTR() <= 1.0) {
                killerTeam.setTrappingPoints(killerTeam.getTrappingPoints()+1);
            }
        }

        if (victimTeam != null) {
            victimTeam.setDeaths(victimTeam.getDeaths() + 1);
        }

        Bukkit.getScheduler().scheduleAsyncDelayedTask(Foxtrot.getInstance(), () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (Foxtrot.getInstance().getToggleDeathMessageMap().areDeathMessagesEnabled(player.getUniqueId())) {
                    player.sendMessage(deathMessage);
                    continue;
                }

                if (Foxtrot.getInstance().getTeamHandler().getTeam(player) == null) {
                    continue;
                }

                // send them the message if the player who died was on their team
                if (Foxtrot.getInstance().getTeamHandler().getTeam(event.getEntity()) != null && Foxtrot.getInstance().getTeamHandler().getTeam(player).equals(Foxtrot.getInstance().getTeamHandler().getTeam(event.getEntity()))) {
                    player.sendMessage(deathMessage);
                }

                if (killer == null) {
                    continue;
                }

                if (Foxtrot.getInstance().getTeamHandler().getTeam(killer) != null && Foxtrot.getInstance().getTeamHandler().getTeam(player).equals(Foxtrot.getInstance().getTeamHandler().getTeam(killer))) {
                    player.sendMessage(deathMessage);
                }
            }
        });

        DeathMessageHandler.clearDamage(event.getEntity());
        Foxtrot.getInstance().getDeathsMap().setDeaths(event.getEntity().getUniqueId(), Foxtrot.getInstance().getDeathsMap().getDeaths(event.getEntity().getUniqueId()) + 1);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        if (Foxtrot.getInstance().getMapHandler().isKitMap()) {
            event.getPlayer().setVelocity(new Vector(0, 0, 0));
            checkKillstreaks(event.getPlayer());
        }
    }

    private void checkKillstreaks(Player player) {
        Bukkit.getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
            int killstreak = Foxtrot.getInstance().getKillstreakMap().getKillstreak(player.getUniqueId());
            List<PersistentKillstreak> persistent = Foxtrot.getInstance().getMapHandler().getKillstreakHandler().getPersistentKillstreaks(player, killstreak);

            for (PersistentKillstreak persistentStreak : persistent) {
                persistentStreak.apply(player);
            }
        }, 5L);
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (!event.getAction().name().startsWith("RIGHT_CLICK")) {
            return;
        }

        ItemStack inHand = event.getPlayer().getItemInHand();
        if (inHand == null) {
            return;
        }

        if (inHand.getType() != Material.NETHER_STAR) {
            return;
        }

        if (!inHand.hasItemMeta()
                || !inHand.getItemMeta().hasDisplayName()
                || !inHand.getItemMeta().getDisplayName().equals(ChatColor.translateAlternateColorCodes('&', "&5&k! &d&lPotion Refill Token &5&k!"))) {
            return;
        }

        if (EOTWCommand.realFFAStarted()) {
            event.getPlayer().sendMessage(ChatColor.RED + "Potion Refill Tokens are disabled during FFA.");
            return;
        }

        if (inHand.getAmount() == 1) {
            event.getPlayer().setItemInHand(null);
        } else {
            inHand.setAmount(inHand.getAmount()-1);
        }


        ItemStack pot = new ItemStack(Material.POTION, 1, (short) 16421);

        while (event.getPlayer().getInventory().addItem(pot).isEmpty()) {
        }
    }

}
