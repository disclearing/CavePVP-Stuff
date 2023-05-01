package org.cavepvp.suge.kit.listener;

import cc.fyre.proton.util.TimeUtils;
import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.cavepvp.suge.Suge;
import org.cavepvp.suge.kit.data.Kit;
import org.cavepvp.suge.kit.event.KitUseEvent;

@AllArgsConstructor
public class KitListener implements Listener {

    private Suge instance;

    @EventHandler(priority = EventPriority.MONITOR)
    private void onKit(KitUseEvent event) {

        final Player player = event.getPlayer();
        final Kit kit = event.getKit();

        if (kit.getName().equalsIgnoreCase("Weekly")) {
            int playtimeTime = (int) Foxtrot.getInstance().getPlaytimeMap().getPlaytime(player.getUniqueId());
            playtimeTime += Foxtrot.getInstance().getPlaytimeMap().getCurrentSession(player.getUniqueId()) / 1000;

            if (playtimeTime <= (3600)) {
                player.sendMessage(ChatColor.RED + "You must have at-least 1 hour of playtime!");

                event.setCancelled(true);
                return;
            }
        }

        if (!player.isOp() && this.instance.getKitHandler().hasCooldown(player, kit)) {
            event.setCancelled(true);

            player.sendMessage(ChatColor.RED + "You may not use this kit as you are on cooldown for " + TimeUtils.formatIntoHHMMSS((int) (this.instance.getKitHandler().getRemaining(player, kit)/1000)));
        }
    }
}
