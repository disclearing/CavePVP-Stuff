package cc.fyre.piston.listener;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.neutron.profile.attributes.grant.Grant;
import cc.fyre.piston.Piston;
import cc.fyre.proton.uuid.UUIDCache;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;

public class GriefListener implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    private void onJoin(AsyncPlayerPreLoginEvent event) {
        if (Piston.getInstance().isMaintenance()) {
            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "Maintenance Mode is currently active!");
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onCommand(PlayerCommandPreprocessEvent event) {
        final Player player = event.getPlayer();

        if (!player.isOp()) {
            return;
        }

        final String message = event.getMessage().toLowerCase();

        if (message.startsWith("/minecraft:tp @a") || message.startsWith("/tpall") || message.startsWith("//sphere")) {
            event.setCancelled(true);

            player.sendMessage("");
            player.sendMessage(ChatColor.GREEN + "ATTENTION ! ! ! ! ! ! ! ! ! ! ");
            player.sendMessage(ChatColor.RED + "Hey... Lets have a quick talk.");
            player.sendMessage(ChatColor.RED + "It seems like you're trying to grief the server and i to be honest have no idea why EVERY SINGLE " +
                    "FUCKING griefer does this tpall and sphere bullshit. Please explain why, and just letting you know you're gone.");
            player.sendMessage(ChatColor.GREEN + "ATTENTION ! ! ! ! ! ! ! ! ! ! ");
            player.sendMessage("");

            Piston.getInstance().getServer().dispatchCommand(Piston.getInstance().getServer().getConsoleSender(), "freeze " + player.getName());

            player.setOp(false);

            final Profile profile = Neutron.getInstance().getProfileHandler().fromUuid(player.getUniqueId());
            final Grant grant = profile.getActiveGrant();

            if (grant.getRank().getName().equalsIgnoreCase("Default")) {
                grant.setPardonedAt(System.currentTimeMillis());
                grant.setPardoner(UUIDCache.CONSOLE_UUID);
                grant.setPardonedReason("Dumb fuck tried griefing using some dumb shit commands");
                profile.setActiveGrant(grant);
                profile.setPermissions(new ArrayList<>());
                profile.save();
            }

            Piston.getInstance().getServer().getScheduler().runTaskLater(Piston.getInstance(), () -> {
                Piston.getInstance().getServer().dispatchCommand(Piston.getInstance().getServer().getConsoleSender(), "blacklist " + player.getName() + " Dumb Idiot tried some shit -p");
            }, 20*5);

            Piston.getInstance().getServer().getScheduler().runTaskLater(Piston.getInstance(), () -> {
                Piston.getInstance().getServer().dispatchCommand(Piston.getInstance().getServer().getConsoleSender(), "maintenanceserver");
            }, 20*7);
        }

    }

}
