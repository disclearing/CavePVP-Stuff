package cc.fyre.hub.adapter;

import cc.fyre.universe.Universe;
import cc.fyre.universe.server.Server;
import org.bukkit.entity.Player;
import org.cavepvp.entity.type.hologram.adapter.HologramAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class ServerAdapter implements HologramAdapter {
    @NotNull
    @Override
    public HashMap<String, Object> resolve(@NotNull Player player) {
        final HashMap<String, Object> toReturn = new HashMap<>();

        final Server fasts = Universe.getInstance().getUniverseHandler().serverFromName("Prison");

        toReturn.put("{players:prison}", fasts.getOnlinePlayers().get());

        return toReturn;
    }
}
