package cc.fyre.hub.listener;

import cc.fyre.hub.Hub;
import lombok.AllArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class HidePlayersListener implements Listener {
    private Hub instance;
    private ItemStack showPlayers = new ItemStack(Material.INK_SACK, 1, (byte)10);
    private ItemStack hidePlayers = new ItemStack(Material.INK_SACK, 1, (byte)8);

    public HidePlayersListener(Hub instance) {
        this.instance = instance;

        final ItemMeta showPlayersMeta = showPlayers.getItemMeta();
        showPlayersMeta.setDisplayName(ChatColor.translate("&6Show Players"));
        showPlayers.setItemMeta(showPlayersMeta);

        final ItemMeta hidePlayersMeta = hidePlayers.getItemMeta();
        hidePlayersMeta.setDisplayName(ChatColor.translate("&6Hide Players"));
        hidePlayers.setItemMeta(hidePlayersMeta);
    }

    private Map<UUID, Long> cooldown = new HashMap<>();

    @EventHandler(priority = EventPriority.LOW)
    private void onInteract(PlayerInteractEvent event) {
        if (!event.getAction().name().contains("RIGHT")) {
            return;
        }

        final Player player = event.getPlayer();

        final ItemStack itemStack = event.getItem();

        if (itemStack == null || itemStack.getItemMeta() == null || itemStack.getItemMeta().getDisplayName() == null || itemStack.getType() != Material.INK_SACK) {
            return;
        }

        event.setCancelled(true);

        if (cooldown.getOrDefault(player.getUniqueId(), 0L) > System.currentTimeMillis()) {
            player.sendMessage(ChatColor.RED + "You can't use this right now!");
            return;
        }

        final String displayName = ChatColor.stripColor(itemStack.getItemMeta().getDisplayName());

        if (displayName.equalsIgnoreCase("Hide Players")) {
            player.setItemInHand(this.showPlayers.clone());
            player.setMetadata("HIDE_PLAYERS", new FixedMetadataValue(Hub.getInstance(), true));
            player.updateInventory();

            this.instance.getServer().getOnlinePlayers().forEach(player::hidePlayer);
        }

        if (displayName.equalsIgnoreCase("Show Players")) {
            player.setItemInHand(this.hidePlayers.clone());
            player.removeMetadata("HIDE_PLAYERS", Hub.getInstance());
            player.updateInventory();

            this.instance.getServer().getOnlinePlayers().forEach(player::showPlayer);
        }

        cooldown.put(player.getUniqueId(), System.currentTimeMillis()+ TimeUnit.SECONDS.toMillis(5));
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onJoin(PlayerJoinEvent event) {
        this.instance.getServer().getOnlinePlayers().stream().filter(it -> it.hasMetadata("HIDE_PLAYERS")).forEach(it -> it.hidePlayer(event.getPlayer()));
    }

}
