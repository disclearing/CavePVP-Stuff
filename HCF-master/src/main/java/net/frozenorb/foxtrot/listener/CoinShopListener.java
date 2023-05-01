package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.cavepvp.coinshop.listener.events.PurchaseItemsEvent;

public class CoinShopListener implements Listener {
    
    @EventHandler
    private void onPurchase(PurchaseItemsEvent event) {
        final Player player = Foxtrot.getInstance().getServer().getPlayer(event.getBuyer());
        
        if (player == null) {
            return;
        }

        for (Player onlinePlayer : Foxtrot.getInstance().getServer().getOnlinePlayers()) {
            onlinePlayer.sendMessage(ChatColor.translate("&4&lStore &8» &c{name} &7has purchased &f" + event.getProduct().getDisplayName() + " &7through &f/coinshop&7!"));
        }
    }
    
}
