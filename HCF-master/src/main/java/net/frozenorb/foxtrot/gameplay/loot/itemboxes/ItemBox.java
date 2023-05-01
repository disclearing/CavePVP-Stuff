package net.frozenorb.foxtrot.gameplay.loot.itemboxes;

import lombok.Getter;
import lombok.Setter;
import net.frozenorb.foxtrot.Foxtrot;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class ItemBox implements Listener {
    @Getter @Setter private Entity entity;
    
    public abstract String getId();
    public abstract String getDisplayName();
    public abstract List<String> getLore();
    public abstract Material getMaterial();

    public ItemBox() {
        Foxtrot.getInstance().getServer().getPluginManager().registerEvents(this, Foxtrot.getInstance());
    }

    public boolean redeem(Player player, PlayerInteractEvent event) {
        if (!event.getAction().name().contains("RIGHT")) {
            return false;
        }

        final ItemStack itemStack = player.getItemInHand();

        if (!this.isSimilar(itemStack)) {
            return false;
        }

        if (itemStack.getAmount() == 1) {
            player.setItemInHand(null);
        } else {
            itemStack.setAmount(itemStack.getAmount()-1);
        }

        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
        player.playSound(player.getLocation(), Sound.CHEST_OPEN, 1, 1);
        return true;
    }

    public boolean isSimilar(ItemStack itemStack) {

        if (itemStack == null || itemStack.getItemMeta() == null || itemStack.getType() == Material.AIR) {
            return false;
        }

        if (itemStack.getItemMeta().getDisplayName() == null) {
            return false;
        }

        if (itemStack.getItemMeta().getLore() == null || itemStack.getItemMeta().getLore().isEmpty()) {
            return false;
        }

        return itemStack.getType() == this.getMaterial() && ChatColor.stripColor(itemStack.getItemMeta().getDisplayName()).startsWith(ChatColor.stripColor(this.getDisplayName())) && itemStack.getItemMeta().getLore().get(0).equals(this.getLore().get(0));
    }
}
