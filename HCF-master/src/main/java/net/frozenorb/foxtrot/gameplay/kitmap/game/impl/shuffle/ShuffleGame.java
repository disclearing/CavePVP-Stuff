package net.frozenorb.foxtrot.gameplay.kitmap.game.impl.shuffle;

import cc.fyre.neutron.Neutron;
import cc.fyre.piston.command.admin.ScreenShareCommand;
import cc.fyre.proton.util.ItemBuilder;
import mkremins.fanciful.FancyMessage;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.kitmap.game.Game;
import net.frozenorb.foxtrot.gameplay.kitmap.game.GameState;
import net.frozenorb.foxtrot.gameplay.kitmap.game.GameType;
import net.frozenorb.foxtrot.gameplay.kitmap.game.arena.GameArena;
import net.frozenorb.foxtrot.util.Cuboid;
import net.minecraft.util.org.apache.commons.lang3.text.WordUtils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.bukkit.DyeColor.*;

// this code is aids
public class ShuffleGame extends Game {

    private List<Block> floor = new ArrayList<>();

    private List<DyeColor> colors = Arrays.asList(
            ORANGE,
            MAGENTA,
            LIGHT_BLUE,
            YELLOW,
            LIME,
            GRAY,
            CYAN,
            PURPLE,
            BLUE,
            GREEN,
            RED
    );

    private static SecureRandom RANDOM;

    static {
        try {
            RANDOM = SecureRandom.getInstance("SHA1PRNG", "SUN");
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            RANDOM = new SecureRandom();
        }
    }

    private DyeColor currentColor;

    public int currentRound;
    private long roundEndsAt;

    public ShuffleGame(UUID host, List<GameArena> arenaOptions) {
        super(host, GameType.WOOL_SHUFFLE, arenaOptions);
    }

    @Override
    public void startGame() {
        super.startGame();

        // just in case whoever made the arena forgets to set bounds, null check so no npe
        if (getVotedArena().getBounds() != null) {
            getVotedArena().createSnapshot();
        }

        scanFloor();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (state == GameState.ENDED) {
                    cancel();
                    return;
                }

                if (state == GameState.RUNNING) {
                    // split players into 2 groups and tp both to separate spawn point
                    int midIndex = (getPlayers().size() - 1) / 2;
                    List<List<Player>> split = new ArrayList<>(
                            getPlayers().stream()
                                    .collect(Collectors.partitioningBy(s -> getPlayers().indexOf(s) > midIndex))
                                    .values()
                    );

                    split.get(0).forEach(player -> player.teleport(getVotedArena().getPointA()));
                    split.get(1).forEach(player -> player.teleport(getVotedArena().getPointB()));

                    startRound();
                    cancel();
                }
            }
        }.runTaskTimer(Foxtrot.getInstance(), 10L, 10L);
    }

    @Override
    public void endGame() {
        super.endGame();

        if (getVotedArena().getBounds() != null) {
            Bukkit.getScheduler().runTaskLater(Foxtrot.getInstance(), getVotedArena()::restoreSnapshot, 5 * 20L);
        }
    }

    public void scanFloor() {
        for (Block block : getVotedArena().getBounds()) {
            if (block.getType() == Material.WOOL && block.getData() == 0) {
                floor.add(block);
            }
        }
    }

    public void shuffleFloor() {
        Location start = floor.get(0).getLocation();
        Location end = floor.get(floor.size() - 1).getLocation();

        int startX = start.getBlockX();
        int startZ = start.getBlockZ();
        int endX = end.getBlockX();
        int endZ = end.getBlockZ();

        List<DyeColor> colors = new ArrayList<>(this.colors);
        colors.remove(currentColor);

        for (int minX = startX; minX <= endX; minX += 5) {
            for (int minZ = startZ; minZ <= endZ; minZ += 5) {
                DyeColor color = colors.get(random(colors.size()));
                Location min = new Location(start.getWorld(), minX, start.getBlockY(), minZ);
                Location max = new Location(start.getWorld(), minX + 5, start.getBlockY(), minZ + 5);

                Cuboid cuboid = new Cuboid(min, max);
                for (Block block : cuboid.getBlocks()) {
                    if (!floor.contains(block)) continue;

                    block.setType(Material.WOOL);
                    block.setData(color.getWoolData());
                }
            }
        }

        for (int i = 0; i < 3; i++) {
            int tileMaxX = Math.abs(endX - startX) / 5;
            int tileMaxZ = Math.abs(endZ - startZ) / 5;
            int meowX = startX + ThreadLocalRandom.current().nextInt(tileMaxX) * 5;
            int meowZ = startZ + ThreadLocalRandom.current().nextInt(tileMaxZ) * 5;

            Location min = new Location(start.getWorld(), meowX, start.getBlockY(), meowZ);
            Location max = new Location(start.getWorld(), meowX + 4, start.getBlockY(), meowZ + 4);

            Cuboid cuboid = new Cuboid(min, max);

            for (Block block : cuboid.getBlocks()) {
                if (!floor.contains(block)) continue;

                block.setType(Material.WOOL);
                block.setData(currentColor.getWoolData());
            }
        }

        sendMessages(ChatColor.GOLD + "The floor has been shuffled!");
    }

    public void pickColor() {
        List<DyeColor> colors = new ArrayList<>(this.colors);
        colors.remove(currentColor);

        currentColor = colors.get(random(colors.size()));

        String colorName = WordUtils.capitalize(currentColor.name().toLowerCase().replace("_", " "));
        ItemStack colorItem = ItemBuilder.of(Material.WOOL)
                .name(dyeToChatColor(currentColor) + colorName)
                .data(currentColor.getWoolData()).enchant(Enchantment.KNOCKBACK, 1).build();

        for (Player player : getPlayers()) {
            for (int i = 0; i < 9; i++) {
                player.getInventory().setItem(i, colorItem);
            }

            player.sendMessage(ChatColor.GOLD + "The new color is " + dyeToChatColor(currentColor) + colorName + ChatColor.GOLD + ".");
            if (currentRound >= 10) {
                ScreenShareCommand.sendDangerSign(player, "", "", ChatColor.DARK_RED + ChatColor.BOLD.toString() + "WARNING", "", ChatColor.YELLOW + "PvP has been", ChatColor.YELLOW + "enabled for this match!", "", "");
            }
            sendSound(Sound.NOTE_PLING, 5F, 2F);
        }
    }

    public void dropFloor() {
        Location start = floor.get(0).getLocation();
        Location end = floor.get(floor.size() - 1).getLocation();

        Cuboid floor = new Cuboid(start, end);
        for (Block block : floor) {
            if (block.getData() != currentColor.getWoolData()) {
                block.setType(Material.AIR);
            }
        }

        for (Player player : getPlayers()) {
            boolean eliminate = true;

            for (int offset = 0; offset <= 3; offset++) {
                Block below = player.getLocation().getBlock().getRelative(BlockFace.DOWN, offset);
                if (below == null) continue;

                if (below.getData() == currentColor.getWoolData()) {
                    eliminate = false;
                    break;
                }
            }

            if (eliminate && players.size() > 1)
                eliminatePlayer(player, null);
        }

        sendMessages("", ChatColor.GOLD + "The floor has dropped!", "");

        if (players.size() <= 1)
            endGame();
    }

    public void startRound() {
        if (state == GameState.ENDED) return;

        currentRound++;
        setStartedAt(System.currentTimeMillis());

        long seconds = 11;

        if (currentRound > 12)
            seconds = 4;
        else if (currentRound > 7)
            seconds = 6;
        else if (currentRound > 3)
            seconds = 8;

        roundEndsAt = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(seconds);

        pickColor();
        Bukkit.getScheduler().runTask(Foxtrot.getInstance(), this::shuffleFloor);

        int secondsInt = (int) seconds;
        new BukkitRunnable() {
            private final List<Integer> announceAt = Arrays.asList(15, 10, 5, 4, 3, 2, 1);
            private final List<Integer> intervals = announceAt.stream()
                    .filter(t -> t <= secondsInt).collect(Collectors.toList());

            @Override
            public void run() {
                if (state == GameState.ENDED) {
                    cancel();
                    return;
                }

                if (state == GameState.RUNNING) {
                    if (System.currentTimeMillis() >= roundEndsAt) {
                        stopRound();
                        cancel();
                        return;
                    }

                    // prevent NPE
                    if (intervals.isEmpty()) {
                        return;
                    }

                    int first = intervals.get(0);

                    int secondsRemaining = (int) Math.round((roundEndsAt - System.currentTimeMillis()) / 1000.0D);
                    if (secondsRemaining > 0 && secondsRemaining <= first) {
                        intervals.remove(0);
                        sendMessages(ChatColor.GOLD + "The floor will drop in " + ChatColor.WHITE + secondsRemaining + " second" + (secondsRemaining == 1 ? "" : "s") + "&6!");
                    }
                }
            }
        }.runTaskTimerAsynchronously(Foxtrot.getInstance(), 10L, 10L);
    }

    public void stopRound() {
        Bukkit.getScheduler().runTask(Foxtrot.getInstance(), this::dropFloor);

        if (players.size() == 1) {
            endGame();
        } else {
            Bukkit.getScheduler().runTaskLater(Foxtrot.getInstance(), this::startRound, 5 * 20L);
        }
    }

    @Override
    public void eliminatePlayer(Player player, Player killer) {
        super.eliminatePlayer(player, killer);
        addSpectator(player);
    }

    public double getDeathHeight() {
        return Math.min(getVotedArena().getPointA().getBlockY(), getVotedArena().getPointB().getBlockY()) - 1.9;
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
            lines.add(" &6Round: &f" + currentRound);

            if (currentColor != null) {
                String colorName = WordUtils.capitalize(currentColor.name().toLowerCase().replace("_", " "));
                lines.add(" &6Color: &f" + dyeToChatColor(currentColor) + colorName);

                try {
                    int secondsRemaining = (int) Math.round((roundEndsAt - System.currentTimeMillis()) / 1000.0D);
                    if (secondsRemaining > 0)
                        lines.add(" &6Dropping in: &f" + secondsRemaining);
                } catch (Exception ignored) {
                }
            } else {
                lines.add("");
                lines.add("&aPicking new color...");
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

    public ChatColor dyeToChatColor(DyeColor color) {
        switch (color) {
            case ORANGE:
                return ChatColor.GOLD;
            case MAGENTA:
                return ChatColor.LIGHT_PURPLE;
            case LIGHT_BLUE:
                return ChatColor.AQUA;
            case YELLOW:
                return ChatColor.YELLOW;
            case LIME:
                return ChatColor.GREEN;
            case GRAY:
                return ChatColor.GRAY;
            case CYAN:
                return ChatColor.DARK_AQUA;
            case PURPLE:
                return ChatColor.DARK_PURPLE;
            case BLUE:
                return ChatColor.BLUE;
            case GREEN:
                return ChatColor.DARK_GREEN;
            case RED:
                return ChatColor.RED;
            default:
                return ChatColor.WHITE;
        }
    }

    private int random(int bound) {
        return RANDOM.nextInt(bound);
    }
}
