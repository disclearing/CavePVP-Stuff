package cc.fyre.universe.runnable;

import cc.fyre.universe.Universe;
import cc.fyre.universe.packet.ServerUpdatePacket;
import cc.fyre.universe.server.Server;

import cc.fyre.universe.server.fetch.ServerStatus;
import org.bukkit.scheduler.BukkitRunnable;
/**
 * @author xanderume@gmail (JavaProject)
 */
public class UniverseRunnable extends BukkitRunnable {

    @Override
    public void run() {

        final Server server = new Server(Universe.getInstance().getServerName(),Universe.getInstance().getGroup(),Universe.getInstance(), false, Universe.fakePlayers);

        if (Universe.getInstance().isStopping()) {
            server.setStatus(ServerStatus.OFFLINE);
        }

        server.cache(Universe.getInstance().getPidginHandler().getPool());

        Universe.getInstance().getPidginHandler().sendPacket(new ServerUpdatePacket(server.toJsonObject()));
    }

}
