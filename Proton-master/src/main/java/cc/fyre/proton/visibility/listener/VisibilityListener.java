package cc.fyre.proton.visibility.listener;

import cc.fyre.proton.Proton;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Collection;

public class VisibilityListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Proton.getInstance().getVisibilityHandler().update(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onTabComplete(PlayerChatTabCompleteEvent event) {

        final String token = event.getLastToken();
        final Collection<String> completions = event.getTabCompletions();

        completions.clear();

        for (Player target : Proton.getInstance().getServer().getOnlinePlayers()) {

            if (Proton.getInstance().getVisibilityHandler().treatAsOnline(target,event.getPlayer()) && StringUtils.startsWithIgnoreCase(target.getName(),token)) {
                completions.add(target.getName());
            }

        }

    }
}
