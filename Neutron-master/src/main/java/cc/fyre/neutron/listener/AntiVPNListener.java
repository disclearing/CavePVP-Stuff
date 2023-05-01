package cc.fyre.neutron.listener;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.util.AntiVPNUtil;
import cc.fyre.proton.Proton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.io.IOException;

import static org.bukkit.ChatColor.RED;

public class AntiVPNListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        String name = event.getName();
        String uuid = event.getUniqueId().toString();
        String ip = event.getAddress().getHostAddress();

        if (Proton.getInstance().runBackboneRedisCommand(jedis -> jedis.hexists("vpnWhitelist", uuid))) {
            Neutron.getInstance().getLogger().warning("[iprisk.info] " + name + " is whitelisted");
            return;
        }

        try {
            AntiVPNUtil.Result result = AntiVPNUtil.getResult(ip);

            if (result.isBad()) {
                event.disallow(
                        AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                        RED + "\n\nYou are not allowed to connect using a VPN or proxy.\n" +
                                "Join our TeamSpeak at " + Neutron.getInstance().getTeamspeak() + " if you believe this is an error."
                );

                Neutron.getInstance().getLogger().info("[iprisk.info] " + name + " was denied from logging in: " + ip);
            }
        } catch (IOException e) {
            Neutron.getInstance().getLogger().warning("[iprisk.info] An error occurred:");
            e.printStackTrace();
        }
    }
}
