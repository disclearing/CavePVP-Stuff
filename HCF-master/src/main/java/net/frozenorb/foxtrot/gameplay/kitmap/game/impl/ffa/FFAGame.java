package net.frozenorb.foxtrot.gameplay.kitmap.game.impl.ffa;

import cc.fyre.neutron.Neutron;
import lombok.Getter;
import mkremins.fanciful.FancyMessage;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.kitmap.game.Game;
import net.frozenorb.foxtrot.gameplay.kitmap.game.GameState;
import net.frozenorb.foxtrot.gameplay.kitmap.game.GameType;
import net.frozenorb.foxtrot.gameplay.kitmap.game.arena.GameArena;
import net.frozenorb.foxtrot.gameplay.kitmap.kits.DefaultKit;
import net.frozenorb.foxtrot.util.InventoryUtils;
import net.frozenorb.foxtrot.util.ItemUtils;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public class FFAGame extends Game {
    @Getter private boolean started;

    private Map<UUID, Integer> kills = new HashMap<>();

    public FFAGame(UUID host, List<GameArena> arenaOptions) {
        super(host, GameType.FFA, arenaOptions);
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
                    startFFA();
                    cancel();
                }
            }
        }.runTaskTimer(Foxtrot.getInstance(), 10L, 10L);
    }

    @Override
    public void endGame() {
        super.endGame();

        this.started = false;

        if (winningPlayer != null) {
            sendMessages(winningPlayer.getDisplayName() + ChatColor.YELLOW + " won the FFA with " + ChatColor.DARK_RED + kills.get(winningPlayer.getUniqueId()) + ChatColor.YELLOW + " kills.");
        }

        if (getVotedArena().getBounds() != null) {
            List<Chunk> arenaChunks = getVotedArena().getBounds().getChunks();
            arenaChunks.stream().filter(chunk -> !chunk.isLoaded()).forEach(Chunk::load);
            arenaChunks.forEach(chunk -> {
                for (Entity entity : chunk.getEntities()) {
                    if (entity instanceof Item && getVotedArena().getBounds().contains(entity.getLocation())) {
                        entity.remove();
                    }
                }
            });
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                kills.clear();
            }
        }.runTaskLater(Foxtrot.getInstance(), 100L);
    }

    private void startFFA() {
        DefaultKit pvpKit = Foxtrot.getInstance().getMapHandler().getKitManager().getDefaultKit("PvP");
        for (Player player : getPlayers()) {
            InventoryUtils.resetInventoryNow(player);
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));
            pvpKit.apply(player);
        }

        // split players into 2 groups and tp both to separate spawn point
        int midIndex = (getPlayers().size() - 1) / 2;
        List<List<Player>> split = new ArrayList<>(
                getPlayers().stream()
                        .collect(Collectors.partitioningBy(s -> getPlayers().indexOf(s) > midIndex))
                        .values()
        );

        split.get(0).forEach(player -> player.teleport(getVotedArena().getPointA()));
        split.get(1).forEach(player -> player.teleport(getVotedArena().getPointB()));

        this.started = false;
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
    public void addPlayer(Player player) throws IllegalStateException {
        super.addPlayer(player);
        kills.putIfAbsent(player.getUniqueId(), 0);
    }

    @Override
    public void removePlayer(Player player) {
        super.removePlayer(player);
    }

    @Override
    public void eliminatePlayer(Player player, Player killer) {
        super.eliminatePlayer(player, killer);

        if (killer != null) {
            sendMessages(ChatColor.DARK_RED.toString() + player.getName() + ChatColor.YELLOW + " has been eliminated by " + ChatColor.DARK_RED + killer.getName() + ChatColor.YELLOW + "! " + ChatColor.GRAY + "(" + getPlayers().size() + "/" + getStartedWith() + " players remaining)");
            kills.put(killer.getUniqueId(), kills.get(killer.getUniqueId()) + 1);

            ItemStack crapples = new ItemStack(Material.GOLDEN_APPLE, 2);
            if (killer.getInventory().firstEmpty() == -1 &&
                    Stream.of(killer.getInventory().getContents()).filter(Objects::nonNull).noneMatch(item -> item.isSimilar(crapples))) {
                killer.getInventory().setItem(7, crapples);
            } else {
                player.getInventory().addItem(crapples);
            }

            killer.setHealth(killer.getMaxHealth());
            killer.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 8*20, 0));
            killer.updateInventory();
        }

        removeSpectator(player);

        if (players.size() == 1) {
            endGame();
        }
    }

    public boolean isGracePeriod() {
        return getStartedAt() == null || System.currentTimeMillis() <= getStartedAt() + 6_000L;
    }

    @Override
    public void handleDamage(Player victim, Player damager, EntityDamageByEntityEvent event) {
        if (isGracePeriod()) {
            event.setCancelled(true);
            return;
        }

        if (state == GameState.RUNNING) {
            if (!isPlaying(victim.getUniqueId()) && !isPlaying(damager.getUniqueId())) {
                event.setCancelled(true);
            }
        } else {
            event.setCancelled(true);
        }
    }

    public List<Player> getTopThree() {
        return getPlayers().stream()
                .sorted(Comparator.comparingInt(p -> kills.get(((Player) p).getUniqueId())).reversed())
                .limit(3)
                .collect(Collectors.toList());
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

            int potions = ItemUtils.countStacksMatching(player.getInventory().getContents(), ItemUtils.INSTANT_HEAL_POTION_PREDICATE);
            lines.add(" &6Pots: &f" + potions);
            lines.add(" &6Kills: &f" + kills.get(player.getUniqueId()));
            return;
        }

        if (winningPlayer == null) {
            lines.add("&6Winner: &fNone");
        } else {
            lines.add("&6Winner: &f" + winningPlayer.getName());
        }

        lines.add("&6Kills: &f" + kills.get(player.getUniqueId()));
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
                        .then("████").color(ChatColor.DARK_RED)
                        .then("██").color(ChatColor.GRAY)
                        .then(ChatColor.translate(" &7Players: &f" + this.getPlayers().size() + "/" + this.getMaxPlayers())),
                new FancyMessage("")
                        .then("█").color(ChatColor.GRAY)
                        .then("█").color(ChatColor.DARK_RED)
                        .then("█████").color(ChatColor.GRAY)
                        .then(ChatColor.translate(" &7Hosted By: &f" + Neutron.getInstance().getProfileHandler().findDisplayName(this.getHost()))),
                new FancyMessage("")
                        .then("█").color(ChatColor.GRAY)
                        .then("█").color(ChatColor.DARK_RED)
                        .then("█████").color(ChatColor.GRAY)
                        .then(" Click to join the event").color(ChatColor.GREEN)
                        .command("/game join")
                        .formattedTooltip(new FancyMessage("Click here to join the event").color(ChatColor.YELLOW))
                        .then("]").color(ChatColor.GRAY),
                new FancyMessage("███████").color(ChatColor.GRAY)
        );
    }
}
