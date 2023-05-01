package net.frozenorb.foxtrot.gameplay.kitmap.game;

import cc.fyre.proton.Proton;
import net.minecraft.util.com.google.common.base.Charsets;
import net.minecraft.util.com.google.common.io.Files;
import lombok.Getter;
import lombok.Setter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.kitmap.game.arena.GameArena;
import net.frozenorb.foxtrot.gameplay.kitmap.game.impl.ffa.FFAGame;
import net.frozenorb.foxtrot.gameplay.kitmap.game.impl.minestrike.MineStrikeGame;
import net.frozenorb.foxtrot.gameplay.kitmap.game.impl.oitq.OITQGame;
import net.frozenorb.foxtrot.gameplay.kitmap.game.impl.parkour.ParkourGame;
import net.frozenorb.foxtrot.gameplay.kitmap.game.impl.shuffle.ShuffleGame;
import net.frozenorb.foxtrot.gameplay.kitmap.game.impl.spleef.SpleefGame;
import net.frozenorb.foxtrot.gameplay.kitmap.game.impl.sumo.SumoGame;
import net.frozenorb.foxtrot.gameplay.kitmap.game.impl.tntrun.TNTRunGame;
import net.frozenorb.foxtrot.gameplay.kitmap.game.impl.tnttag.TNTTagGame;
import net.frozenorb.foxtrot.util.ItemUtils;
import org.bukkit.*;
import org.bukkit.craftbukkit.libs.com.google.gson.reflect.TypeToken;
import org.bukkit.entity.Player;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Getter
public class GameHandler {

    private static final long GAME_COOLDOWN = TimeUnit.MINUTES.toMillis(3L);

    private static final File CONFIG_FILE = new File(Foxtrot.getInstance().getDataFolder(), "data/kitmap-games-config.json");
    private static final Type CONFIG_DATA_TYPE = new TypeToken<GameHandlerConfig>() {
    }.getType();

    private GameHandlerConfig config = new GameHandlerConfig();

    @Setter private Game ongoingGame;
    private Long nextRuntime = System.currentTimeMillis() - GAME_COOLDOWN;

    @Setter
    private boolean disabled;

    private World world;

    public GameHandler() {
        loadWorld();
        loadConfig();

        Proton.getInstance().getCommandHandler().registerParameterType(GameArena.class, new GameArena.Type());
        Proton.getInstance().getCommandHandler().registerParameterType(GameType.class, new GameType.Type());
    }

    private void loadWorld() {
        WorldCreator worldCreator = new WorldCreator("kits_events");
        worldCreator.generator(new ChunkGenerator() {
            @Override
            public byte[] generate(World world, Random random, int x, int z) {
                return new byte[32768];
            }

            @Override
            public boolean canSpawn(World world, int x, int z) {
                return false;
            }

            @Override
            public List<BlockPopulator> getDefaultPopulators(World world) {
                return new ArrayList<>();
            }

            @Override
            public Location getFixedSpawnLocation(World world, Random random) {
                return new Location(world, 0.5, 100.0, 0.5);
            }
        });

        world = Bukkit.createWorld(worldCreator);
    }

    private void loadConfig() {
        if (CONFIG_FILE.exists()) {
            try (Reader reader = Files.newReader(CONFIG_FILE, Charsets.UTF_8)) {
                config = Proton.PLAIN_GSON.fromJson(reader, CONFIG_DATA_TYPE);
            } catch (IOException e) {
                e.printStackTrace();
                Foxtrot.getInstance().getLogger().severe(ChatColor.RED + "Failed to import kitmap-games-config.json!");
            }
        }
    }

    public void saveConfig() {
        try {
            Files.write(Proton.PLAIN_GSON.toJson(config, CONFIG_DATA_TYPE), CONFIG_FILE, Charsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            Foxtrot.getInstance().getLogger().severe(ChatColor.RED + "Failed to export kitmap-games-config.json!");
        }
    }

    public int getCooldownSeconds() {
        if (System.currentTimeMillis() < nextRuntime)
            return (int) ((nextRuntime - System.currentTimeMillis()) / 1000F);
        return 0;
    }

    public boolean isOngoingGame() {
        return ongoingGame != null;
    }

    public String getStatus() {
        if (this.isJoinable()) {
            return "Waiting";
        }

        if (this.isOngoingGame()) {
            return "Active";
        }

        return "Inactive";
    }

    public boolean isHostCooldown() {
        return System.currentTimeMillis() < nextRuntime;
    }

    public boolean isJoinable() {
        return ongoingGame != null && ongoingGame.getState() == GameState.WAITING;
    }

    public boolean canStartGame(Player player, GameType gameType) {
        if (disabled) {
            player.sendMessage(ChatColor.RED + "Events are currently disabled.");
            return false;
        }

        if (!gameType.canHost(player)) {
            player.sendMessage(ChatColor.RED + "You don't have permission to host " + gameType.getDisplayName() + " events.");
            return false;
        }

        if (isOngoingGame()) {
            player.sendMessage(ChatColor.RED + "There is an ongoing game, and only one game can run at a time.");
            return false;
        }

        if (findArenas(gameType).isEmpty()) {
            player.sendMessage(ChatColor.RED + "There are no arenas compatible with that game type!");
            return false;
        }

        if (!player.hasPermission("kitmap.game.host.cooldown-bypass") && isHostCooldown()) {
            double cooldown = ((double) Math.round(10.0D * ((double) (nextRuntime - System.currentTimeMillis()) / 1000F)) / 10.0D);
            player.sendMessage(ChatColor.RED + "Another game can't be hosted for another " + cooldown + "s.");
            return false;
        }

        if (!ItemUtils.hasEmptyInventory(player)) {
            player.sendMessage(ChatColor.RED + "You need to have an empty inventory to join the event.");
            return false;
        }

        return true;
    }

    public Game startGame(UUID host, GameType gameType) throws IllegalStateException {
        if (ongoingGame != null) {
            throw new IllegalStateException("There is an ongoing game!");
        }

        if (gameType == GameType.SUMO) {
            ongoingGame = new SumoGame(host, findArenas(gameType));
//        } else if (gameType == GameType.TAG) {
//            ongoingGame = new TagGame(host, findArenas(gameType));
        } else if (gameType == GameType.FFA) {
            ongoingGame = new FFAGame(host, findArenas(gameType));
        } else if (gameType == GameType.SPLEEF) {
            ongoingGame = new SpleefGame(host, findArenas(gameType));
        } else if (gameType == GameType.WOOL_SHUFFLE) {
            ongoingGame = new ShuffleGame(host, findArenas(gameType));
        } else if (gameType == GameType.OITQ) {
            ongoingGame = new OITQGame(host, findArenas(gameType));
        } else if (gameType == GameType.TNT_RUN) {
            ongoingGame = new TNTRunGame(host, findArenas(gameType));
        } else if (gameType == GameType.TNT_TAG) {
            ongoingGame = new TNTTagGame(host, findArenas(gameType));
        } else if (gameType == GameType.MINE_STRIKE) {
            ongoingGame = new MineStrikeGame(host, findArenas(gameType));
        } else if (gameType == GameType.PARKOUR) {
            ongoingGame = new ParkourGame(host, findArenas(gameType));
        } else {
            throw new IllegalStateException("Game type not supported yet!");
        }

        ongoingGame.startGame();

        return ongoingGame;
    }

    public Game startGame(Player host, GameType gameType) throws IllegalStateException {
        if (ongoingGame != null) {
            throw new IllegalStateException("There is an ongoing game!");
        }

        if (gameType == GameType.SUMO) {
            ongoingGame = new SumoGame(host.getUniqueId(), findArenas(gameType));
//        } else if (gameType == GameType.TAG) {
//            ongoingGame = new TagGame(host.getUniqueId(), findArenas(gameType));
        } else if (gameType == GameType.FFA) {
            ongoingGame = new FFAGame(host.getUniqueId(), findArenas(gameType));
        } else if (gameType == GameType.SPLEEF) {
            ongoingGame = new SpleefGame(host.getUniqueId(), findArenas(gameType));
        } else if (gameType == GameType.WOOL_SHUFFLE) {
            ongoingGame = new ShuffleGame(host.getUniqueId(), findArenas(gameType));
        } else if (gameType == GameType.OITQ) {
            ongoingGame = new OITQGame(host.getUniqueId(), findArenas(gameType));
        } else if (gameType == GameType.TNT_RUN) {
            ongoingGame = new TNTRunGame(host.getUniqueId(), findArenas(gameType));
        } else if (gameType == GameType.TNT_TAG) {
            ongoingGame = new TNTTagGame(host.getUniqueId(), findArenas(gameType));
        } else if (gameType == GameType.MINE_STRIKE) {
            ongoingGame = new MineStrikeGame(host.getUniqueId(), findArenas(gameType));
        } else if (gameType == GameType.PARKOUR) {
            ongoingGame = new ParkourGame(host.getUniqueId(), findArenas(gameType));
        } else {
            throw new IllegalStateException("Game type not supported yet!");
        }

        ongoingGame.startGame();

        return ongoingGame;
    }

    public void endGame() {
        if (ongoingGame == null || !ongoingGame.isHasExpired()) // Don't apply cooldown for events that expired
            nextRuntime = System.currentTimeMillis() + GAME_COOLDOWN;

        ongoingGame = null;
    }

    public List<GameArena> findArenas(GameType gameType) {
        List<GameArena> compatible = new ArrayList<>();
        for (GameArena arena : config.getArenas()) {
            if (arena.isSetup() && arena.getCompatibleGameTypes().contains(gameType.name().toLowerCase())) {
                compatible.add(arena);
            }
        }
        return compatible;
    }

}
