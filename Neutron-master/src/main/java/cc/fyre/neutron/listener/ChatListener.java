package cc.fyre.neutron.listener;

import cc.fyre.neutron.Neutron;
import cc.fyre.neutron.command.profile.chatColor.ChatColorCommand;
import cc.fyre.neutron.profile.Profile;
import cc.fyre.neutron.profile.attributes.punishment.impl.RemoveAblePunishment;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChatListener implements Listener {

    @Getter private Map<UUID,Long> lastMessageCache = new HashMap<>();

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncChat(AsyncPlayerChatEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final Player player = event.getPlayer();

        final Profile profile = Neutron.getInstance().getProfileHandler().fromUuid(player.getUniqueId());

        if (profile.getChatColor() != null && profile.getChatColor() != ChatColor.WHITE && ChatColorCommand.disallowedChatColors.contains(profile.getChatColor())) {
            event.getPlayer().sendMessage(ChatColor.RED + "Deactivated your Chat Color as that chat color is no longer supported!");

            profile.setChatColor(ChatColor.WHITE);
            profile.save();
        }

        if (profile.getChatColor() != null && profile.getChatColor() != ChatColor.WHITE && !player.hasPermission("color." + profile.getChatColor().name().toLowerCase()) && !player.hasPermission("color.*")) {
            event.getPlayer().sendMessage(ChatColor.RED + "Deactivated your Chat Color as you no longer had permission to use it.");

            profile.setChatColor(ChatColor.WHITE);
            profile.save();
        }

        if (!player.hasPermission("command.customrank") && profile.getCustomPrefix() != null) {
            profile.setCustomPrefix(null);
            profile.save();
            player.sendMessage(ChatColor.RED + "You must purchase the Custom Rank to use this at https://store.cavepvp.org!");
        }

        if (profile.getHolidayType() != null && !player.hasPermission("command.holidayprefix")) {
            profile.setHolidayType(null);
            profile.save();
        }

        if (profile.getActivePunishment(RemoveAblePunishment.Type.MUTE) != null) {

            final RemoveAblePunishment punishment = profile.getActivePunishment(RemoveAblePunishment.Type.MUTE);

            event.getPlayer().sendMessage(ChatColor.RED + "You are currently muted.");
            event.getPlayer().sendMessage(ChatColor.RED + "Expires: " + ChatColor.YELLOW + punishment.getRemainingString());
            event.getPlayer().sendMessage(ChatColor.RED + "Reason: " + ChatColor.YELLOW + punishment.getExecutedReason());

            event.setCancelled(true);
        }
    }

}
