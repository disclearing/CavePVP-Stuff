package net.frozenorb.foxtrot.gameplay.kitmap.game.impl.oitq;

import cc.fyre.neutron.Neutron;
import cc.fyre.proton.util.UUIDUtils;
import lombok.Getter;
import mkremins.fanciful.FancyMessage;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.kitmap.game.Game;
import net.frozenorb.foxtrot.gameplay.kitmap.game.GameState;
import net.frozenorb.foxtrot.gameplay.kitmap.game.GameType;
import net.frozenorb.foxtrot.gameplay.kitmap.game.arena.GameArena;
import net.frozenorb.foxtrot.util.InventoryUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

public class OITQGame extends Game {
    public OITQGame(UUID host, List<GameArena> arenaOptions) {
        super(host, GameType.OITQ, arenaOptions);
    }

    @Getter private Map<UUID, Integer> killsCount = new HashMap<>();

    @Getter private boolean started = false;

    @Override
    public void startGame() {
        super.startGame();

        // just in case whoever made the arena forgets to set bounds, null check so no npe
        if (getVotedArena().getBounds() != null) {
            getVotedArena().createSnapshot();
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if (state == GameState.ENDED) {
                    cancel();
                    return;
                }

                if (state == GameState.RUNNING) {
                    startOITQ();
                    cancel();
                }
            }
        }.runTaskTimer(Foxtrot.getInstance(), 10L, 10L);
    }

    @Override
    public void endGame() {
        super.endGame();

        this.started = false;

        if (getVotedArena().getBounds() != null) {
            Bukkit.getScheduler().runTaskLater(Foxtrot.getInstance(), getVotedArena()::restoreSnapshot, 5 * 20L);
        }
    }

    private void startOITQ() {
        this.started = false;

        for (Player player : this.getPlayers()) {
            InventoryUtils.resetInventoryNow(player);
            player.getInventory().addItem(new ItemStack(Material.STONE_AXE, 1));
            player.getInventory().addItem(new ItemStack(Material.BOW, 1));
            player.getInventory().addItem(new ItemStack(Material.ARROW, 1));
            player.teleport(getVotedArena().getPointA());
        }
        
        this.setStartedAt(System.currentTimeMillis());

        new BukkitRunnable() {
            private int i = 6;

            @Override
            public void run() {
                if (state == GameState.ENDED || started) {
                    cancel();
                    return;
                }

                i--;

                sendSound(Sound.NOTE_PLING, 1F, i == 0 ? 2F : 1F);
                started = i == 0;

                if (i == 0) {
                    sendMessages("&6&l" + getGameType().getDisplayName() + " &eevent has begun!");
                } else {
                    sendMessages("&6&l" + getGameType().getDisplayName() + " &eevent begins in &f" + i + " second" + (i == 1 ? "" : "s") + "&e...");
                }
            }
        }.runTaskTimer(Foxtrot.getInstance(), 20L, 20L);
    }

    @Override
    public void eliminatePlayer(Player player, Player killer) {
        super.eliminatePlayer(player, killer);
        addSpectator(player);

        if (players.size() == 1) {
            endGame();
        }
    }

    public double getDeathHeight() {
        return Math.min(getVotedArena().getPointA().getBlockY(), getVotedArena().getPointB().getBlockY()) - 2.9;
    }

    @Override
    public Player findWinningPlayer() {
        if (players.size() == 1) {
            return Bukkit.getPlayer(players.iterator().next());
        }

        return null;
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
            lines.add("");
            lines.add(" &6Kills: &f" + this.killsCount.getOrDefault(player.getUniqueId(), 0) + "/20");
            lines.add("");

            if (this.killsCount.isEmpty()) {
                lines.add(" &6Top Player: &fN/A");
                lines.add(" &6Top Kills: &f0/20");
                return;
            }

            final Map.Entry<UUID, Integer> topKills = this.killsCount.entrySet().stream().sorted((o1, o2) -> o2.getValue()).collect(Collectors.toList()).get(0);

            lines.add(" &6Top Player: &f" + UUIDUtils.name(topKills.getKey()));
            lines.add(" &6Top Kills: &f" + topKills.getValue() + "/20");
            return;
        }
        
        if (winningPlayer == null) {
            lines.add(" &6Winner: &fNone");
        } else {
            lines.add("&6Winner: &f" + winningPlayer.getName());
        }
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
                        .then("███").color(ChatColor.GRAY)
                        .then("█").color(ChatColor.DARK_RED)
                        .then("█").color(ChatColor.GRAY),
                new FancyMessage("")
                        .then("█").color(ChatColor.GRAY)
                        .then("█").color(ChatColor.DARK_RED)
                        .then("███").color(ChatColor.GRAY)
                        .then("█").color(ChatColor.DARK_RED)
                        .then("█").color(ChatColor.GRAY)
                        .then(ChatColor.translate(" &7Players: &f" + this.getPlayers().size() + "/" + this.getMaxPlayers())),
                new FancyMessage("")
                        .then("█").color(ChatColor.GRAY)
                        .then("█").color(ChatColor.DARK_RED)
                        .then("███").color(ChatColor.GRAY)
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
