package net.frozenorb.foxtrot.gameplay.kitmap.game.impl.minestrike;

import cc.fyre.neutron.Neutron;
import cc.fyre.proton.util.ItemBuilder;
import lombok.Getter;
import mkremins.fanciful.FancyMessage;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.kitmap.game.Game;
import net.frozenorb.foxtrot.gameplay.kitmap.game.GameState;
import net.frozenorb.foxtrot.gameplay.kitmap.game.GameType;
import net.frozenorb.foxtrot.gameplay.kitmap.game.arena.GameArena;
import net.frozenorb.foxtrot.listener.LunarClientListener;
import net.frozenorb.foxtrot.util.InventoryUtils;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

public class MineStrikeGame extends Game {
    public MineStrikeGame(UUID host, List<GameArena> arenaOptions) {
        super(host, GameType.MINE_STRIKE, arenaOptions);
    }

    @Getter private List<UUID> redTeam = new ArrayList<>();
    @Getter private List<UUID> blueTeam = new ArrayList<>();
    @Getter private int redStartedOutWith;
    @Getter private int blueStartedOutWith;
    private Set<UUID> playerGems = new HashSet<>();

    @Getter private ItemStack shotGun = ItemBuilder.of(Material.GOLD_BARDING).name("&6Shotgun Kit &7(Right Click)").enchant(Enchantment.ARROW_INFINITE, 1).build();
    @Getter private ItemStack smg = ItemBuilder.of(Material.IRON_BARDING).name("&6SMG Kit &7(Right Click)").enchant(Enchantment.ARROW_INFINITE, 1).build();
    @Getter private ItemStack rifle = ItemBuilder.of(Material.DIAMOND_BARDING).name("&6Rifle Kit &7(Right Click)").enchant(Enchantment.ARROW_INFINITE, 1).build();
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
                    startMineStrike();
                    cancel();
                }
            }
        }.runTaskTimer(Foxtrot.getInstance(), 10L, 10L);
    }

    @Override
    public void endGame() {
        state = GameState.ENDED;
        Foxtrot.getInstance().getMapHandler().getGameHandler().setOngoingGame(null);

        String team = ChatColor.RED + "Red Team";

        if (redTeam.isEmpty() && !blueTeam.isEmpty()) {
            team = ChatColor.BLUE + "Blue Team";
        }

        String finalTeam = team;
        new BukkitRunnable() {
            private int i = 5;

            @Override
            public void run() {
                i--;

                if (i <= 3 && i > 0) {
                    Bukkit.broadcastMessage(finalTeam + ChatColor.YELLOW + " has " + ChatColor.GREEN + "won" + ChatColor.YELLOW + " the event!");
                }

                if (i <= 0) {
                    cancel();

                    new BukkitRunnable() {
                        @Override
                        public void run() {

                            blueTeam.clear();
                            redTeam.clear();

                            for (Player player : getPlayers()) {
                                removePlayer(player);
                            }

                            for (Player spectator : getSpectators()) {
                                removeSpectator(spectator);
                            }
                        }
                    }.runTask(Foxtrot.getInstance());

                    Foxtrot.getInstance().getMapHandler().getGameHandler().endGame();
                }
            }
        }.runTaskTimerAsynchronously(Foxtrot.getInstance(), 20L, 20L);

        this.started = false;

        if (getVotedArena().getBounds() != null) {
            Bukkit.getScheduler().runTaskLater(Foxtrot.getInstance(), getVotedArena()::restoreSnapshot, 5 * 20L);
        }
    }

    private void startMineStrike() {
        for (Player player : getPlayers()) {
            InventoryUtils.resetInventoryNow(player);
            player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0));

            Foxtrot.getInstance().getCsUtility().giveWeapon(player, "Rifle", 1);
            Foxtrot.getInstance().getCsUtility().giveWeapon(player, "SMG", 1);
        }

        int midIndex = (getPlayers().size() - 1) / 2;
        List<List<Player>> split = new ArrayList<>(
                getPlayers().stream()
                        .collect(Collectors.partitioningBy(s -> getPlayers().indexOf(s) > midIndex))
                        .values()
        );

        final List<Player> red = split.get(0);
        final List<Player> blue = split.get(1);

        red.forEach(player -> {
            player.getInventory().setHelmet(ItemBuilder.of(Material.REDSTONE_BLOCK).build());
            player.getInventory().setChestplate(ItemBuilder.of(Material.LEATHER_CHESTPLATE).color(Color.RED).build());
            player.getInventory().setLeggings(ItemBuilder.of(Material.LEATHER_LEGGINGS).color(Color.RED).build());
            player.getInventory().setBoots(ItemBuilder.of(Material.LEATHER_BOOTS).color(Color.RED).build());

            redTeam.add(player.getUniqueId());
            player.teleport(getVotedArena().getPointA());
        });

        blue.forEach(player -> {
            player.getInventory().setHelmet(ItemBuilder.of(Material.LAPIS_BLOCK).build());
            player.getInventory().setChestplate(ItemBuilder.of(Material.LEATHER_CHESTPLATE).color(Color.BLUE).build());
            player.getInventory().setLeggings(ItemBuilder.of(Material.LEATHER_LEGGINGS).color(Color.BLUE).build());
            player.getInventory().setBoots(ItemBuilder.of(Material.LEATHER_BOOTS).color(Color.BLUE).build());

            blueTeam.add(player.getUniqueId());
            player.teleport(getVotedArena().getPointB());
        });

        for (Player player : getPlayers()) {
            LunarClientListener.updateNametag(player);
        }

        this.redStartedOutWith = this.redTeam.size();
        this.blueStartedOutWith = this.blueTeam.size();

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
    public void eliminatePlayer(Player player, Player killer) {
        super.eliminatePlayer(player, killer);

        this.sendMessages(player.getDisplayName() + " &ewas shot to death by &4" + killer.getDisplayName());

        addSpectator(player);

        blueTeam.remove(player.getUniqueId());
        redTeam.remove(player.getUniqueId());

        if (blueTeam.isEmpty() || redTeam.isEmpty()) {
            endGame();
        }

        LunarClientListener.updateTeammates(player);
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
            lines.add("&6&lTeams:");
            lines.add(" &cRed: &f" + redTeam.size() + "&7/&f" + getRedStartedOutWith());
            lines.add(" &9Blue: &f" + blueTeam.size() + "&7/&f" + getBlueStartedOutWith());
            return;
        }

        if (winningPlayer == null) {
            lines.add(" &6Winner: &fNone");
        } else {
            lines.add(" &6Winner: &f" + winningPlayer.getName());
        }
    }

    @Override
    public List<FancyMessage> createHostNotification() {
        return Arrays.asList(
                new FancyMessage("███████").color(ChatColor.GRAY),
                new FancyMessage("")
                        .then("█").color(ChatColor.GRAY)
                        .then("█").color(ChatColor.DARK_RED)
                        .then("███").color(ChatColor.GRAY)
                        .then("█").color(ChatColor.DARK_RED)
                        .then("█").color(ChatColor.GRAY)
                        .then(" " + getGameType().getDisplayName() + " Event").color(ChatColor.DARK_RED).style(ChatColor.BOLD),
                new FancyMessage("")
                        .then("█").color(ChatColor.GRAY)
                        .then("██").color(ChatColor.DARK_RED)
                        .then("█").color(ChatColor.GRAY)
                        .then("██").color(ChatColor.DARK_RED)
                        .then("█").color(ChatColor.GRAY),
                new FancyMessage("")
                        .then("█").color(ChatColor.GRAY)
                        .then("█").color(ChatColor.DARK_RED)
                        .then("█").color(ChatColor.GRAY)
                        .then("█").color(ChatColor.DARK_RED)
                        .then("█").color(ChatColor.GRAY)
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
                        .then("█").color(ChatColor.DARK_RED)
                        .then("███").color(ChatColor.GRAY)
                        .then("█").color(ChatColor.DARK_RED)
                        .then("█").color(ChatColor.GRAY)
                        .then(" Click to join the event").color(ChatColor.GREEN)
                        .command("/game join")
                        .formattedTooltip(new FancyMessage("Click here to join the event").color(ChatColor.YELLOW)),
                new FancyMessage("███████").color(ChatColor.GRAY)
        );
    }
}