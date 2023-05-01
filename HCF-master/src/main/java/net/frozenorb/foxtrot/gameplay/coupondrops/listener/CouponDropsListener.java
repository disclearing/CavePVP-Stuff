package net.frozenorb.foxtrot.gameplay.coupondrops.listener;

import cc.fyre.proton.event.HourEvent;
import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.object.LCWaypoint;
import lombok.AllArgsConstructor;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.coupondrops.CouponDropsHandler;
import net.frozenorb.foxtrot.gameplay.coupondrops.tasks.CouponDropsTask;
import org.bukkit.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@AllArgsConstructor
public class CouponDropsListener implements Listener {
    private Foxtrot instance;
    private CouponDropsHandler couponDropsHandler;

    public static LCWaypoint couponDropWaypoint;

    @EventHandler
    private void onPickupCoupon(final PlayerPickupItemEvent event) {
        final ItemStack itemStack = event.getItem().getItemStack();
        if (itemStack != null && itemStack.getType() == Material.BOOK_AND_QUILL && itemStack.hasItemMeta()) {
            final ItemMeta itemMeta = itemStack.getItemMeta();
            final String displayName = itemMeta.getDisplayName();
            if (displayName != null) {
                final boolean contains = CouponDropsTask.ACTIVE_COUPONS.contains(displayName);

                if (contains && event.getPlayer().getGameMode() == GameMode.CREATIVE) {
                    event.setCancelled(true);
                    return;
                }

                final boolean removed = CouponDropsTask.ACTIVE_COUPONS.remove(displayName);

                Foxtrot.getInstance().getCouponDropsHandler().setDropsIn(0);
                Foxtrot.getInstance().getCouponDropsHandler().setLocation(null);

                if (removed) {
                    Bukkit.getOnlinePlayers().forEach(player -> {
                        player.sendMessage(" ");
                        player.sendMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Discount Drops");
                        player.sendMessage(ChatColor.translate("&7The book containing a coupon has been picked up!"));
                        player.sendMessage(ChatColor.translate("&cPicked up By: &f" + event.getPlayer().getDisplayName()));
                        player.sendMessage(" ");

                        LunarClientAPI.getInstance().removeWaypoint(player, couponDropWaypoint);
                    });
                }
            }
        }
    }

    @EventHandler
    private void onHour(HourEvent event) {
        if (event.getHour() % 3 != 0) {
            return;
        }

        new CouponDropsTask(this.instance, this.couponDropsHandler.createCoupon()).runTask(Foxtrot.getInstance());
    }

}
