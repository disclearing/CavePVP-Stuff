package net.frozenorb.foxtrot.gameplay.kitmap.game.impl.sumo;

import cc.fyre.neutron.Neutron;
import lombok.Getter;
import mkremins.fanciful.FancyMessage;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.kitmap.game.*;
import net.frozenorb.foxtrot.gameplay.kitmap.game.arena.GameArena;
import net.frozenorb.foxtrot.util.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class SumoGame extends Game {

    private Player playerA;
    private Player playerB;
    private Map<UUID, Integer> roundsPlayed = new HashMap<>();
    private int currentRound = 0;

    public SumoGame(UUID host, List<GameArena> arenaOptions) {
        super(host, GameType.SUMO, arenaOptions);
    }

    @Override
    public void startGame() {
        super.startGame();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (state == GameState.ENDED) {
                    cancel();
                    return;
                }

                if (state == GameState.RUNNING) {
                    determineNextPlayers();
                    startRound();
                    cancel();
                }
            }
        }.runTaskTimer(Foxtrot.getInstance(), 10L, 10L);
    }

    @Override
    public void gameBegun() {
        for (Player player : getPlayers()) {
            player.teleport(getVotedArena().getSpectatorSpawn());
        }
    }

    public void startRound() {
        if (playerA == null || playerB == null) {
            throw new IllegalStateException("Cannot start round without both players");
        }

        currentRound++;
        setStartedAt(System.currentTimeMillis());

        InventoryUtils.resetInventoryNow(playerA);
        playerA.teleport(getVotedArena().getPointA());

        InventoryUtils.resetInventoryNow(playerB);
        playerB.teleport(getVotedArena().getPointB());

        new BukkitRunnable() {
            private int i = 6;

            @Override
            public void run() {
                if (state == GameState.ENDED) {
                    cancel();
                    return;
                }

                i--;

                if (i == 0) {
                    sendMessages(ChatColor.GREEN + "The round has started!");
                    sendSound(Sound.NOTE_PLING, 1F, 2F);
                } else {
                    sendMessages(ChatColor.GOLD + "The round is starting in " + ChatColor.WHITE + i + " second" + (i == 1 ? "" : "s") + "&6...");
                    sendSound(Sound.NOTE_PLING, 1F, 1F);
                }

                if (i <= 0) {
                    cancel();
                }
            }
        }.runTaskTimerAsynchronously(Foxtrot.getInstance(), 20L, 20L);
    }

    public void endRound() {
        if (players.size() == 1) {
            endGame();
        } else {
            if (playerA != null) {
                playerA.teleport(getVotedArena().getSpectatorSpawn());
                GameUtils.resetPlayer(playerA);
                playerA = null;
            }

            if (playerB != null) {
                playerB.teleport(getVotedArena().getSpectatorSpawn());
                GameUtils.resetPlayer(playerB);
                playerB = null;
            }

            Bukkit.getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
                determineNextPlayers();
                startRound();
            }, 50L);
        }
    }

    @Override
    public void eliminatePlayer(Player player, Player killer) {
        if (state == GameState.ENDED) return;

        super.eliminatePlayer(player, killer);

        if (killer != null) {
            sendMessages(player.getDisplayName() + ChatColor.GOLD + " has been eliminated by &f" + killer.getDisplayName()  + "&6[&f" + getPlayers().size() + "&7/&f" + getStartedWith() + "&6]");
        }

        if (isCurrentlyFighting(player)) {
            if (playerA.getUniqueId() == player.getUniqueId()) {
                playerA = null;
            } else if (playerB.getUniqueId() == player.getUniqueId()) {
                playerB = null;
            }

            endRound();
        }
    }

    public boolean isCurrentlyFighting(Player player) {
        return (playerA != null && playerA.getUniqueId() == player.getUniqueId()) || (playerB != null && playerB.getUniqueId() == player.getUniqueId());
    }

    public Player getOpponent(Player player) {
        if (playerA != null && playerA.getUniqueId() == player.getUniqueId()) {
            return playerB;
        }

        if (playerB != null && playerB.getUniqueId() == player.getUniqueId()) {
            return playerA;
        }

        return null;
    }

    public void determineNextPlayers() {
        List<Player> players = getPlayers().stream().sorted(Comparator.comparingInt(player -> roundsPlayed.getOrDefault(player.getUniqueId(), 0))).collect(Collectors.toList());

        playerA = players.get(0);
        playerB = players.get(1);

        roundsPlayed.putIfAbsent(playerA.getUniqueId(), 0);
        roundsPlayed.put(playerA.getUniqueId(), roundsPlayed.get(playerA.getUniqueId()) + 1);

        roundsPlayed.putIfAbsent(playerB.getUniqueId(), 0);
        roundsPlayed.put(playerB.getUniqueId(), roundsPlayed.get(playerB.getUniqueId()) + 1);

        sendMessages(ChatColor.DARK_RED.toString() + ChatColor.BOLD + "Next Round", playerA.getDisplayName() + ChatColor.GRAY + " vs. " + ChatColor.RESET + playerB.getDisplayName());
    }

    public double getDeathHeight() {
        return Math.min(getVotedArena().getPointA().getBlockY(), getVotedArena().getPointB().getBlockY()) - 2.9;
    }

    @Override
    public void handleDamage(Player victim, Player damager, EntityDamageByEntityEvent event) {
        if (state == GameState.RUNNING && victim.getWorld().getName().equalsIgnoreCase("kits_events")) {
            if (isPlaying(victim.getUniqueId()) && isPlaying(damager.getUniqueId())) {
                event.setDamage(0.0);

                if (!isCurrentlyFighting(victim) || !isCurrentlyFighting(damager)) {
                    event.setCancelled(true);
                } else {
                    victim.setHealth(victim.getMaxHealth());
                    victim.updateInventory();
                }
            } else {
                event.setCancelled(true);
            }
        } else {
            event.setCancelled(true);
        }
    }

    @Override
    public Player findWinningPlayer() {
        return playerA == null ? playerB : playerA;
    }

    @Override
    public void getScoreboardLines(Player player, LinkedList<String> lines) {
        lines.add("&6&l" + getGameType().getDisplayName() + ":");

        if (state == GameState.WAITING) {
            lines.add(" &6Players: &f" + players.size() + "&7/&f" + getMaxPlayers());

            if (getVotedArena() != null) {
                lines.add(" &6Map: &f" + getVotedArena().getName());
            } else {
                lines.add("");
                lines.add(" &6Map Vote");

                getArenaOptions().entrySet().stream().sorted((o1, o2) -> o2.getValue().get()).forEach(entry -> lines.add("&7» " + (getPlayerVotes().getOrDefault(player.getUniqueId(), null) == entry.getKey() ? "&l" : "") + entry.getKey().getName() + " &7(" + entry.getValue().get() + ")"));
            }

            if (getStartedAt() == null) {
                int playersNeeded = getGameType().getMinPlayers() - getPlayers().size();
                lines.add("");
                lines.add("&cWaiting for " + playersNeeded + " player" + (playersNeeded == 1 ? "" : "s"));
            } else {
                float remainingSeconds = (getStartedAt() - System.currentTimeMillis()) / 1000F;
                lines.add("&aStarting in " + ((double) Math.round(10.0D * (double) remainingSeconds) / 10.0D) + "s");
            }
            return;
        }

        if (state == GameState.RUNNING) {
            lines.add(" &6Remaining: &f" + players.size() + "&7/&f" + getStartedWith());
            lines.add(" &6Round: &f" + currentRound);

            if (playerA != null && playerB != null) {
                lines.add("");

                final int namesLength = playerA.getName().length() + playerB.getName().length();
                if (namesLength <= 20) {
                    lines.add(playerA.getName() + " &7vs. &r" + playerB.getName());
                } else {
                    lines.add(playerA.getName());
                    lines.add("&7vs.");
                    lines.add(playerB.getName());
                }
            } else {
                lines.add("");
                lines.add("&eSelecting new players...");
            }
            return;
        }

        if (winningPlayer == null) {
            lines.add(" &6Winner: &fNone");
        } else {
            lines.add(" &6Winner: &f" + winningPlayer.getName());
        }
        lines.add(" &6Rounds: &f" + currentRound);
    }

    @Override
    public List<FancyMessage> createHostNotification() {
        return Arrays.asList(
                new FancyMessage("███████").color(ChatColor.GRAY),
                new FancyMessage("")
                        .then("█").color(ChatColor.GRAY)
                        .then("█████").color(ChatColor.DARK_RED)
                        .then("█").color(ChatColor.GRAY)
                        .then(" " + getGameType().getDisplayName() + " Event").color(ChatColor.DARK_RED).style(ChatColor.BOLD),
                new FancyMessage("")
                        .then("█").color(ChatColor.GRAY)
                        .then("█").color(ChatColor.DARK_RED)
                        .then("█████").color(ChatColor.GRAY),
                new FancyMessage("")
                        .then("█").color(ChatColor.GRAY)
                        .then("█████").color(ChatColor.DARK_RED)
                        .then("█").color(ChatColor.GRAY)
                        .then(ChatColor.translate(" &7Players: &f" + this.getPlayers().size() + "/" + this.getMaxPlayers())),
                new FancyMessage("")
                        .then("█████").color(ChatColor.GRAY)
                        .then("█").color(ChatColor.DARK_RED)
                        .then("█").color(ChatColor.GRAY)
                        .then(ChatColor.translate(" &7Hosted By: &f" + Neutron.getInstance().getProfileHandler().findDisplayName(this.getHost()))),
                new FancyMessage("")
                        .then("█").color(ChatColor.GRAY)
                        .then("█████").color(ChatColor.DARK_RED)
                        .then("█").color(ChatColor.GRAY)
                        .then(" Click to join the event").color(ChatColor.GREEN)
                        .command("/game join")
                        .formattedTooltip(new FancyMessage("Click here to join the event").color(ChatColor.YELLOW)),
                new FancyMessage("███████").color(ChatColor.GRAY)
        );
    }

}
