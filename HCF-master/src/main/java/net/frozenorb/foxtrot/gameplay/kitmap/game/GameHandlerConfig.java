package net.frozenorb.foxtrot.gameplay.kitmap.game;

import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.kitmap.game.arena.GameArena;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

@Getter
public class GameHandlerConfig {

    private Location lobbySpawn;
    private final List<GameArena> arenas = new ArrayList<>();

    public GameArena getArenaByName(String name) {
        for (GameArena arena : arenas) {
            if (arena.getName().equalsIgnoreCase(name)) {
                return arena;
            }
        }
        return null;
    }

    public void setLobbySpawnLocation(Location location) {
        this.lobbySpawn = location;
        Foxtrot.getInstance().getMapHandler().getGameHandler().saveConfig();
    }

    public void trackArena(GameArena arena) {
        arenas.add(arena);
        Foxtrot.getInstance().getMapHandler().getGameHandler().saveConfig();
    }

    public void forgetArena(GameArena arena) {
        arenas.remove(arena);
        Foxtrot.getInstance().getMapHandler().getGameHandler().saveConfig();
    }

}
