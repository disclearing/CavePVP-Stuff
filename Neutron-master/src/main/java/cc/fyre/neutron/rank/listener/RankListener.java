package cc.fyre.neutron.rank.listener;

import cc.fyre.neutron.rank.Rank;
import cc.fyre.neutron.rank.menu.editor.menu.RankModifyAttributesMenu;
import cc.fyre.neutron.rank.menu.editor.menu.attribute.RankModifyPermissionsButton;
import cc.fyre.neutron.rank.menu.editor.menu.attribute.RankModifyPrefixButton;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 * @author xanderume@gmail (JavaProject)
 */
public class RankListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncChat(AsyncPlayerChatEvent event) {

        boolean cancel = false;

        if (RankModifyPrefixButton.getModifyingPrefix().containsKey(event.getPlayer().getUniqueId())) {

            final Rank rank = RankModifyPrefixButton.getModifyingPrefix().get(event.getPlayer().getUniqueId());

            rank.setPrefix(ChatColor.translateAlternateColorCodes('&',event.getMessage()));
            rank.save();

            new RankModifyAttributesMenu(rank).openMenu(event.getPlayer());

            RankModifyPrefixButton.getModifyingPrefix().remove(event.getPlayer().getUniqueId());

            cancel = true;
        }

        if (cancel) {
            event.getRecipients().clear();
            event.setCancelled(true);
        }

    }

}
