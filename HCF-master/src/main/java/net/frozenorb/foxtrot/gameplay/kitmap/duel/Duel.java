package net.frozenorb.foxtrot.gameplay.kitmap.duel;

import cc.fyre.modsuite.mod.ModHandler;
import cc.fyre.proton.util.PlayerUtils;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.kitmap.duel.arena.DuelArena;
import net.frozenorb.foxtrot.gameplay.kitmap.kits.DefaultKit;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import net.frozenorb.foxtrot.util.ItemUtils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Getter
public class Duel {

    @NonNull private UUID player1;
    @NonNull private UUID player2;
    @NonNull private DuelArena arena;
    private final int wager;

    private DuelState state;
    private long startedAt = -1;
    private long endedAt = -1;

    private UUID winner;

    public void setup() {
        state = DuelState.COUNTDOWN;

        // this isn't smart but will have to do for now
        for (Player other : Bukkit.getOnlinePlayers()) {
            for (Player player : getPlayers()) {
                if (!contains(other)) {
                    player.hidePlayer(other);
                    other.hidePlayer(player);
                }
            }
        }

        for (Player player : getPlayers()) {
            player.teleport(player.getUniqueId().equals(player1) ? arena.getPointA() : arena.getPointB());
            PlayerUtils.resetInventory(player, GameMode.SURVIVAL);

            DefaultKit pvpKit = Foxtrot.getInstance().getMapHandler().getKitManager().getDefaultKit("Diamond");

            if (pvpKit != null) {
                pvpKit.apply(player);
            } else {
                player.sendMessage(ChatColor.RED + "Failed to find Diamond kit.");
            }
        }

        new BukkitRunnable() {
            int timer = 5;

            @Override
            public void run() {
                if (state == DuelState.FINISHED) {
                    cancel();
                    return;
                }

                if (timer == 0) {
                    cancel();
                    playSound(Sound.NOTE_PLING, 2F);
                    start();
                    return;
                }

                sendMessage(ChatColor.GRAY + "Match will begin in " + ChatColor.RED + timer + ChatColor.GRAY + " second" + (timer != 1 ? "s" : "") + "!");
                playSound(Sound.NOTE_PLING, 1F);
                timer--;
            }
        }.runTaskTimer(Foxtrot.getInstance(), 20L, 20L);
    }

    public void start() {
        state = DuelState.FIGHTING;
        startedAt = System.currentTimeMillis();

        sendMessage(ChatColor.GREEN + "The match has begun.");
    }

    public void end(Player winner) {
        if (state == DuelState.FINISHED) return;
        state = DuelState.FINISHED;
        endedAt = System.currentTimeMillis();

        this.winner = winner.getUniqueId();
        Player loser = Bukkit.getPlayer(getOpponent(winner.getUniqueId()));

        // give winner gems here
        Foxtrot.getInstance().getGemMap().addGems(winner.getUniqueId(), wager * 2, true); // multiply 2 so they get their gems back too

        Foxtrot.getInstance().getServer().broadcastMessage("");
        Foxtrot.getInstance().getServer().broadcastMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Duels");
        Foxtrot.getInstance().getServer().broadcastMessage(winner.getDisplayName() + ChatColor.RED + " has won a duel against " + ChatColor.WHITE + loser.getDisplayName() + ChatColor.RED + " for " + ChatColor.GREEN + ChatColor.BOLD + wager + " Gems" + ChatColor.RED + "!");
        Foxtrot.getInstance().getServer().broadcastMessage(ChatColor.GRAY + "Duel a player by typing /duel <player>");
        Foxtrot.getInstance().getServer().broadcastMessage("");

        int winnerPots = ItemUtils.countStacksMatching(winner.getInventory().getContents(), ItemUtils.INSTANT_HEAL_POTION_PREDICATE);
        int loserPots = ItemUtils.countStacksMatching(loser.getInventory().getContents(), ItemUtils.INSTANT_HEAL_POTION_PREDICATE);

        winner.sendMessage("");
        winner.sendMessage(ChatColor.GREEN + loser.getName() + " had " + loserPots + " potions left.");
        winner.sendMessage("");

        loser.sendMessage("");
        loser.sendMessage(ChatColor.GREEN + winner.getName() + " had " + winnerPots + " potions left.");
        loser.sendMessage("");

        Bukkit.getScheduler().runTaskLater(Foxtrot.getInstance(), this::finish, 3 * 20L);
    }

    private void finish() {
        DuelHandler duelHandler = Foxtrot.getInstance().getMapHandler().getDuelHandler();

        for (Player online : Bukkit.getOnlinePlayers()) {
            for (Player player : getPlayers()) {
                if (!ModHandler.INSTANCE.isInVanish(player.getUniqueId()))
                    player.showPlayer(online);

                online.showPlayer(player);
            }
        }

        duelHandler.removeDuel(this);

        getPlayers().forEach(player -> {
            SpawnTagHandler.removeTag(player);
            PlayerUtils.resetInventory(player);
            player.teleport(Foxtrot.getInstance().getServerHandler().getSpawnLocation());
        });
    }

    public void disconnect(Player player) {
        end(Bukkit.getPlayer(getOpponent(player.getUniqueId())));

        PlayerUtils.resetInventory(player);
        player.teleport(Foxtrot.getInstance().getServerHandler().getSpawnLocation());
    }

    public void eliminate(Player player) {
        end(Bukkit.getPlayer(getOpponent(player.getUniqueId())));

        PlayerUtils.resetInventory(player);
    }

    public void sendMessage(String message) {
        getPlayers().forEach(player -> player.sendMessage(message));
    }

    public void playSound(Sound sound, float pitch) {
        getPlayers().forEach(player -> player.playSound(player.getLocation(), sound, 1F, pitch));
    }

    public UUID getOpponent(UUID uuid) {
        return uuid.equals(player1) ? player2 : player1;
    }

    public boolean contains(Player player) {
        return player.getUniqueId().equals(player1) || player.getUniqueId().equals(player2);
    }

    public List<Player> getPlayers() {
        return Stream.of(Bukkit.getPlayer(player1), Bukkit.getPlayer(player2))
                .filter(Objects::nonNull).collect(Collectors.toList());
    }
}
