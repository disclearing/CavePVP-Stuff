package cc.fyre.universe.util;

import cc.fyre.universe.Universe;
import cc.fyre.universe.UniverseAPI;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import lombok.experimental.UtilityClass;

import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;

/**
 * @author xanderume@gmail (JavaProject)
 */
@UtilityClass
public class BungeeUtil {

    public static void sendToServer(Player player,String server) {
        sendToServer(Collections.singletonList(player),server);
    }

    public static void sendToServer(Collection<? extends Player> players,String server) {

        try {

            final ByteArrayDataOutput out = ByteStreams.newDataOutput();

            out.writeUTF("Connect");
            out.writeUTF(server);

            players.forEach(loopPlayer -> {
                loopPlayer.sendMessage(UniverseAPI.getServerMessage(server));
                loopPlayer.sendPluginMessage(Universe.getInstance(), "BungeeCord", out.toByteArray());
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
