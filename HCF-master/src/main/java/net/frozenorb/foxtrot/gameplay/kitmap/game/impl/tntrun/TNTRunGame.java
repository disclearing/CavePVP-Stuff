package net.frozenorb.foxtrot.gameplay.kitmap.game.impl.tntrun;

import cc.fyre.neutron.Neutron;
import lombok.Getter;
import mkremins.fanciful.FancyMessage;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.kitmap.game.Game;
import net.frozenorb.foxtrot.gameplay.kitmap.game.GameState;
import net.frozenorb.foxtrot.gameplay.kitmap.game.GameType;
import net.frozenorb.foxtrot.gameplay.kitmap.game.arena.GameArena;
import net.frozenorb.foxtrot.util.InventoryUtils;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class TNTRunGame extends Game {

    @Getter private boolean started = false;

    public TNTRunGame(UUID host, List<GameArena> arenaOptions) {
        super(host, GameType.TNT_RUN, arenaOptions);
    }

    @Override
    public void startGame() {
        super.startGame();

        started = false;

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
                    startTNTRun();
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

    private void startTNTRun() {
        this.started = false;

        for (Player player : this.getPlayers()) {
            InventoryUtils.resetInventoryNow(player);
            player.teleport(this.getVotedArena().getPointA());
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

        new BukkitRunnable() {
            @Override
            public void run() {

                if (!started) {
                    return;
                }

                if (state == GameState.ENDED) {
                    cancel();
                    return;
                }

                for (Player player : getPlayers()) {
                    ArrayList<BlockState> block = getBlocksBelow(player);

                    Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
                        if (!block.isEmpty()) {
                            block.get(0).getLocation().getBlock().setType(Material.AIR);
                        }
                    }, 6);
                }
            }
        }.runTaskTimer(Foxtrot.getInstance(), 0, 2L);
    }

    public ArrayList<BlockState> getBlocksBelow(Player player){
        ArrayList<BlockState> blocksBelow = new ArrayList<>();
        Location location = player.getLocation();
        World world = player.getWorld();
        double x = location.getX();
        double z = location.getZ();

        // Add the block below the player to the List.
        Location block = new Location(world, x, location.getY() - 1, z);
        if (block.getBlock().getType() == Material.TNT) blocksBelow.add(block.getBlock().getState());

        // If the player is on the edge of the block, add the necessary blocks to the List.
        if(x - Math.floor(x) <= 0.3){
            block.setX(Math.floor(x) - 0.5);
            if (block.getBlock().getType() == Material.TNT) blocksBelow.add(block.getBlock().getState());
            block = new Location(world, x, location.getY() - 1, z);
        } else if(x - Math.floor(x) >= 0.7){
            block.setX(Math.ceil(x) + 0.5);
            if (block.getBlock().getType() == Material.TNT) blocksBelow.add(block.getBlock().getState());
            block = new Location(world, x, location.getY() - 1, z);
        }

        if(z - Math.floor(z) <= 0.3){
            block.setZ(Math.floor(z) - 0.5);
            if (block.getBlock().getType() == Material.TNT) blocksBelow.add(block.getBlock().getState());
        } else if(z - Math.floor(z) >= 0.7){
            block.setZ(Math.ceil(z) + 0.5);
            if (block.getBlock().getType() == Material.TNT) blocksBelow.add(block.getBlock().getState());
        }

        if(x - Math.floor(x) <= 0.3 && z - Math.floor(z) <= 0.3){
            block.setX(Math.floor(x) - 0.5);
            block.setZ(Math.floor(z) - 0.5);

            if (block.getBlock().getType() == Material.TNT) blocksBelow.add(block.getBlock().getState());
        } else if(x - Math.floor(x) >= 0.7 && z - Math.floor(x) >= 0.7){
            block.setX(Math.ceil(x) + 0.5);
            block.setZ(Math.ceil(z) + 0.5);

            if (block.getBlock().getType() == Material.TNT) blocksBelow.add(block.getBlock().getState());
        } else if(x - Math.floor(x) <= 0.3 && z - Math.floor(z) >= 0.7){
            block.setX(Math.floor(x) - 0.5);
            block.setZ(Math.ceil(z) + 0.5);

            if (block.getBlock().getType() == Material.TNT) blocksBelow.add(block.getBlock().getState());
        } else if(x - Math.floor(x) >= 0.7 && z - Math.floor(z) <= 0.3){
            block.setX(Math.ceil(x) + 0.5);
            block.setZ(Math.floor(z) - 0.5);

            if (block.getBlock().getType() == Material.TNT) blocksBelow.add(block.getBlock().getState());
        }

        // Return the List.
        return blocksBelow;
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
                        .then("█████").color(ChatColor.DARK_RED)
                        .then("█").color(ChatColor.GRAY)
                        .then(" " + getGameType().getDisplayName() + " Event").color(ChatColor.DARK_RED).style(ChatColor.BOLD),
                new FancyMessage("")
                        .then("███").color(ChatColor.GRAY)
                        .then("█").color(ChatColor.DARK_RED)
                        .then("███").color(ChatColor.GRAY),
                new FancyMessage("")
                        .then("███").color(ChatColor.GRAY)
                        .then("█").color(ChatColor.DARK_RED)
                        .then("███").color(ChatColor.GRAY)
                        .then(ChatColor.translate(" &7Players: &f" + this.getPlayers().size() + "/" + this.getMaxPlayers())),
                new FancyMessage("")
                        .then("███").color(ChatColor.GRAY)
                        .then("█").color(ChatColor.DARK_RED)
                        .then("███").color(ChatColor.GRAY)
                        .then(ChatColor.translate(" &7Hosted By: &f" + Neutron.getInstance().getProfileHandler().findDisplayName(this.getHost()))),
                new FancyMessage("")
                        .then("███").color(ChatColor.GRAY)
                        .then("█").color(ChatColor.DARK_RED)
                        .then("███").color(ChatColor.GRAY)
                        .then(" Click to join the event").color(ChatColor.GREEN)
                        .command("/game join")
                        .formattedTooltip(new FancyMessage("Click here to join the event").color(ChatColor.YELLOW)),
                new FancyMessage("███████").color(ChatColor.GRAY)
        );
    }
}
