package cc.fyre.proton.border;

import cc.fyre.proton.Proton;
import cc.fyre.proton.border.listener.BorderListener;
import cc.fyre.proton.border.listener.InternalBorderListener;
import cc.fyre.proton.border.runnable.EnsureInsideRunnable;
import lombok.Getter;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;

public class BorderHandler {

    @Getter private final Map<World,Border> borderMap = new HashMap<>();

    public BorderHandler() {
        Proton.getInstance().getServer().getPluginManager().registerEvents(new BorderListener(),Proton.getInstance());
        Proton.getInstance().getServer().getPluginManager().registerEvents(new InternalBorderListener(),Proton.getInstance());

        new EnsureInsideRunnable().runTaskTimer(Proton.getInstance(), 5L, 5L);
    }

    public Border getBorderForWorld(World world) {
        return this.borderMap.get(world);
    }

    void addBorder(Border border) {
        this.borderMap.put(border.getOrigin().getWorld(), border);
    }


}
