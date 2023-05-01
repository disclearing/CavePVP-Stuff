package net.frozenorb.foxtrot.gameplay.events.cavenite;

import cc.fyre.proton.scoreboard.construct.ScoreFunction;
import cc.fyre.proton.util.ItemBuilder;
import cc.fyre.proton.util.PlayerUtils;
import cc.fyre.proton.util.TimeUtils;
import lombok.Getter;
import lombok.Setter;
import mkremins.fanciful.FancyMessage;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.events.cavenite.listener.CaveNiteListener;
import net.frozenorb.foxtrot.server.voucher.VoucherCommand;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.Claim;
import net.frozenorb.foxtrot.util.CuboidRegion;
import net.frozenorb.foxtrot.util.PersistableLocation;
import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class CaveNiteHandler {
    private Foxtrot instance;

    @Getter @Setter private long started = 0;
    @Getter private final List<UUID> playersRemaining = new ArrayList<>();
    @Getter private final List<UUID> spectators = new ArrayList<>();
    @Getter private final List<Location> locations = new ArrayList<>();
    @Getter @Setter private int scatterIn = 0;
    @Getter @Setter private int startIn = 0;
    @Getter private int startedWith = 0;
    @Getter @Setter private CaveNiteState gameState = CaveNiteState.INACTIVE;
    @Getter private final List<Location> chestLocations = new ArrayList<>();
    @Getter private final List<ItemStack> chestLoot = new ArrayList<>();

    @Getter private File file;
    @Getter private FileConfiguration data;

    public CaveNiteHandler(Foxtrot instance) {
        this.instance = instance;

        this.instance.getServer().getPluginManager().registerEvents(new CaveNiteListener(this.instance, this), this.instance);

        new GameTask(this.instance, this).runTaskTimer(this.instance, 60, 60);

        this.loadLocations();
    }

    public void loadLocations() {
        this.file = new File(Foxtrot.getInstance().getDataFolder(), "data/cavenite.yml");
        this.data = YamlConfiguration.loadConfiguration(this.file);

        if (!this.file.exists()) {
            try {
                this.file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        if (this.data.get("locations") == null) {
            return;
        }

        this.instance.getServer().getScheduler().runTaskLater(this.instance, () -> this.data.getConfigurationSection("locations").getKeys(false).forEach(it ->
            this.locations.add(((PersistableLocation) data.get("locations." + it)).getLocation())), 30L);
    }

    public void saveLocations() {
        this.data.getValues(false).forEach((key, value) -> this.data.set(key, null));

        int i = 0;

        for (Location location : this.locations) {
            i++;
            this.data.set("locations.location_" + i, new PersistableLocation(location));
        }

        try {
            this.data.save(this.file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        final Server server = Foxtrot.getInstance().getServer();

        this.scatterIn = 90;
        this.gameState = CaveNiteState.WAITING;

        this.respawnChests();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (scatterIn <= 0) {
                    scatter();

                    server.broadcastMessage(ChatColor.GREEN + "Cave Nite has started!");

                    startIn = 30;
                    gameState = CaveNiteState.SCATTERING;
                    startedWith = getOnlinePlayers().size();

                    this.cancel();
                    return;
                }

                scatterIn--;

                if (scatterIn % 10 == 0 || scatterIn <= 5) {
                    for (Player onlinePlayer : instance.getServer().getOnlinePlayers()) {
                        onlinePlayer.sendMessage("");
                        onlinePlayer.sendMessage(ChatColor.translate("&7███████"));
                        new FancyMessage(ChatColor.translate("&7█" + "&4█████" + "&7█ &4&lCave Nite")).tooltip(ChatColor.GREEN + "Click to join").command("/cavenite join").send(onlinePlayer);
                        new FancyMessage(ChatColor.translate("&7█" + "&4█" + "&7█████ &f")).tooltip(ChatColor.GREEN + "Click to join").command("/cavenite join").send(onlinePlayer);
                        new FancyMessage(ChatColor.translate("&7█" + "&4█" + "&7█████ &7Players: &f" + getOnlinePlayers().size() + "/" + getLocations().size())).tooltip(ChatColor.GREEN + "Click to join").command("/cavenite join").send(onlinePlayer);
                        new FancyMessage(ChatColor.translate("&7█" + "&4█" + "&7█████ &7Starts In: &f" + scatterIn + " seconds")).tooltip(ChatColor.GREEN + "Click to join").command("/cavenite join").send(onlinePlayer);
                        new FancyMessage(ChatColor.translate("&7█" + "&4█████" + "&7█ &aClick here to join the event")).tooltip(ChatColor.GREEN + "Click to join").command("/cavenite join").send(onlinePlayer);
                        onlinePlayer.sendMessage(ChatColor.translate("&7███████"));
                        onlinePlayer.sendMessage("");
                    }
                }
            }
        }.runTaskTimer(this.instance, 20, 20);
    }

    public void scatter() {
        final Server server = this.instance.getServer();

        new BukkitRunnable() {
            @Override
            public void run() {
                if (startIn <= 0) {
                    this.cancel();
                    gameState = CaveNiteState.RUNNING;
                    started = System.currentTimeMillis();

                    server.broadcastMessage("");
                    server.broadcastMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Cave Nite");
                    server.broadcastMessage(ChatColor.GRAY + "Cave Nite has started! Good luck have fun!");
                    server.broadcastMessage("");

                    getOnlinePlayers().forEach(it -> {
                        it.closeInventory();
                        it.playSound(it.getLocation(), Sound.WITHER_SPAWN, 1F, 1F);
                        it.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0));
                        it.removePotionEffect(PotionEffectType.SLOW);
                        it.removePotionEffect(PotionEffectType.BLINDNESS);
                    });

                    getOnlinePlayers().forEach(it -> it.playSound(it.getLocation(), Sound.LEVEL_UP, 1, 1));
                    return;
                }

                startIn--;

                if (startIn == 20 || startIn == 10 || startIn <= 5) {
                    getOnlinePlayers().forEach(it -> it.playSound(it.getLocation(), Sound.NOTE_PLING, 1, 1));
                    server.broadcastMessage("");
                    server.broadcastMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Cave Nite");
                    server.broadcastMessage(ChatColor.GRAY + "Cave Nite begins in " + ChatColor.WHITE + startIn + " seconds" + ChatColor.GRAY + ".");
                    server.broadcastMessage("");
                }
            }
        }.runTaskTimer(this.instance, 20, 20);

        final List<Player> toTeleport = new ArrayList<>(this.getOnlinePlayers());
        final List<Location> toUse = new ArrayList<>(this.getLocations());

        for (Player onlinePlayer : this.getOnlinePlayers()) {

            toTeleport.remove(onlinePlayer);

            onlinePlayer.playSound(onlinePlayer.getLocation(), Sound.PORTAL_TRAVEL, 1, 1);
            onlinePlayer.sendMessage(ChatColor.GREEN + "You have been scattered across the map!");
            onlinePlayer.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, Integer.MAX_VALUE, 255));
            onlinePlayer.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 255));
            onlinePlayer.teleport(toUse.get(0));
            onlinePlayer.getInventory().clear();

            server.dispatchCommand(server.getConsoleSender(), "kit apply Diamond2 " + onlinePlayer.getName());
        }

    }

    public void addPlayer(Player player) {
        PlayerUtils.resetInventory(player, GameMode.ADVENTURE);

        this.getPlayersRemaining().add(player.getUniqueId());

        this.getOnlinePlayers().forEach(it -> {
            it.playSound(it.getLocation(), Sound.NOTE_PLING, 1, 1);
            it.sendMessage(ChatColor.translate(player.getDisplayName() + " &chas joined the game! &7[" + this.getOnlinePlayers().size() + "/" + this.getLocations().size() + "]"));
        });
    }

    public void addSpectator(Player player) {
        spectators.add(player.getUniqueId());

        PlayerUtils.resetInventory(player, GameMode.ADVENTURE);
        player.setAllowFlight(true);
        player.setFlying(true);
        player.getInventory().setItem(4, ItemBuilder.of(Material.WATCH).name(ChatColor.GOLD + ChatColor.BOLD.toString() + "Spectate Menu").build());
//        player.teleport(this.getOnlinePlayers().get(ThreadLocalRandom.current().nextInt(this.getOnlinePlayers().size())));

        player.spigot().setCollidesWithEntities(false);

        this.getOnlinePlayers().forEach(it -> it.hidePlayer(player));
    }

    public void disqualify(Player player) {
        this.playersRemaining.remove(player.getUniqueId());

        if (this.getOnlinePlayers().size() <= 1) {
            this.endGame(false);
        }
    }

    public void endGame(boolean force) {

        final Server server = this.instance.getServer();
        server.broadcastMessage(ChatColor.RED + "Game over!");

        this.gameState = CaveNiteState.ENDING;

        server.dispatchCommand(server.getConsoleSender(), "reboot 15s");

        if (force) {
            server.broadcastMessage("");
            server.broadcastMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Cave Nite");
            server.broadcastMessage(ChatColor.WHITE + "The game is now over!");
            server.broadcastMessage(ChatColor.GRAY + "The game has been forcefully cancelled");
            server.broadcastMessage("");
            return;
        }

        this.getOnlinePlayers().stream().findFirst().ifPresent(it -> {

            this.getOnlineSpectators().forEach(spectator -> spectator.teleport(it.getLocation().clone()));

            VoucherCommand.spawnFireworks(it.getLocation(), 3, 2, Color.RED, FireworkEffect.Type.BALL_LARGE);

            server.broadcastMessage("");
            server.broadcastMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Cave Nite");
            server.broadcastMessage(ChatColor.WHITE + "The game is now over!");
            server.broadcastMessage(ChatColor.translate(it.getName() + " &7has won the game!"));
            server.broadcastMessage("");
        });
    }

    public void scanLoot() {
        this.chestLocations.clear();

        for (Team team : Foxtrot.getInstance().getTeamHandler().getTeams()) {
            if (team.getOwner() != null) {
                continue;
            }

            if (team.getName().equalsIgnoreCase("CaveNite")) {
                for (Claim claim : team.getClaims()) {
                    for (Location location : new CuboidRegion("CaveNite", claim.getMinimumPoint(), claim.getMaximumPoint())) {
                        if (location.getBlock().getType() == Material.CHEST) {
                            this.chestLocations.add(location);
                        }
                    }
                }
            }
        }
    }

    public void respawnChests() {
        for (Location location : this.chestLocations) {
            if (!(location.getBlock().getState() instanceof Chest)) {
                continue;
            }

            final Chest chest = (Chest) location.getBlock().getState();

            chest.getInventory().clear();

            for (int i = 0; i < 4; i++) {
                final ItemStack randomItem = Foxtrot.getInstance().getCaveNiteHandler().getChestLoot().get(ThreadLocalRandom.current().nextInt(Foxtrot.getInstance().getCaveNiteHandler().getChestLoot().size()));

                chest.getInventory().addItem(randomItem.clone());
            }
        }
    }

    public List<Player> getOnlinePlayers() {
        return this.playersRemaining.stream().filter(it -> this.instance.getServer().getPlayer(it) != null).map(it -> this.instance.getServer().getPlayer(it)).collect(Collectors.toList());
    }

    public List<Player> getOnlineSpectators() {
        return this.spectators.stream().filter(it -> this.instance.getServer().getPlayer(it) != null).map(it -> this.instance.getServer().getPlayer(it)).collect(Collectors.toList());
    }

    public List<String> getScoreboardLines() {
        final List<String> toReturn = new ArrayList<>();

        toReturn.add("&4&lCave Nite");

        if (this.gameState == CaveNiteState.ENDING) {
            final Player lastPlayer = this.getOnlinePlayers().get(0);

            toReturn.add("&7Winner: &f" + (lastPlayer == null ? "N/A" : lastPlayer.getDisplayName()));
            toReturn.add("&7Spectators: &f" + this.getOnlineSpectators().size());
            toReturn.add("");
            toReturn.add(ChatColor.RED + "The game is now over!");
        }

        if (this.gameState == CaveNiteState.RUNNING) {
            long difference = System.currentTimeMillis()-this.started;

            toReturn.add("&7Remaining: &f" + this.getOnlinePlayers().size() + "/" + this.startedWith);
            toReturn.add("&7Time Elapsed: &f" + ScoreFunction.TIME_FANCY.apply((float) (difference/1000)));
            toReturn.add("");
            toReturn.add("&a/cavenite spectate");
        }

        if (this.gameState == CaveNiteState.SCATTERING) {
            toReturn.add("&7Queued: &f" + this.getOnlinePlayers().size());
            toReturn.add("&7Starting In: &f" + TimeUtils.formatIntoMMSS(this.startIn));
            toReturn.add("");
            toReturn.add("&a/cavenite spectate");
        }

        if (this.gameState  == CaveNiteState.WAITING) {
            toReturn.add("&7Queued: &f" + this.getOnlinePlayers().size());
            toReturn.add("&7Scatter In: &f" + TimeUtils.formatIntoMMSS(this.scatterIn));
            toReturn.add("");
            toReturn.add("&a/cavenite join");
        }

        return toReturn;
    }

}