package net.frozenorb.foxtrot.gameplay.boosters;

import cc.fyre.proton.util.UUIDUtils;
import lombok.Getter;
import lombok.Setter;
import mkremins.fanciful.FancyMessage;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.type.kitmap.FiftyFifty;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public abstract class Booster implements Listener {
    @Getter @Setter private long activatedAt = 0;
    @Getter @Setter private UUID activatedBy = null;
    @Getter @Setter private long lastDeactivateAt = 0;
    @Getter @Setter private boolean active;
    
    public abstract String getId();
    public abstract String getDisplayName();
    public abstract int getSlot();
    public abstract List<String> getDescription();
    public abstract ItemStack getItemDisplay();

    public boolean isOnCooldown() {

        if (this.isActive() && activatedBy != null) {
            return false;
        }

        return this.lastDeactivateAt + TimeUnit.HOURS.toHours(1) >= System.currentTimeMillis();
    }

    public int inQueue() {
        return (int) Foxtrot.getInstance().getNetworkBoosterHandler().getBoostersQueued().values().stream().filter(it -> it.contains(this)).count();
    }

    public void activate(UUID target) {
        final Foxtrot instance = Foxtrot.getInstance();

        activatedAt = System.currentTimeMillis();
        activatedBy = target;
        active = true;

        final List<Booster> queued = instance.getNetworkBoosterHandler().getBoostersQueued().getOrDefault(target, new ArrayList<>());
        queued.remove(this);
        instance.getNetworkBoosterHandler().getBoostersQueued().put(target, queued);

        for (Player onlinePlayer : instance.getServer().getOnlinePlayers()) {
            onlinePlayer.sendMessage("");
            onlinePlayer.sendMessage(ChatColor.translate("&7███████"));
            onlinePlayer.sendMessage(ChatColor.translate("&7█" + "&4████" + "&7██ &4&l" + ChatColor.stripColor(this.getDisplayName()) + " Booster"));
            onlinePlayer.sendMessage(ChatColor.translate("&7█" + "&4█" + "&7███&4█&7█ &7" + this.getDescription().get(0)));
            onlinePlayer.sendMessage(ChatColor.translate("&7█" + "&4████" + "&7██ &7" + this.getDescription().get(1)));
            onlinePlayer.sendMessage(ChatColor.translate("&7█" + "&4█" + "&7███&4█&7█"));
            onlinePlayer.sendMessage(ChatColor.translate("&7█" + "&4████" + "&7██ &cActivated By: " + ChatColor.WHITE + UUIDUtils.name(target)));
            onlinePlayer.sendMessage(ChatColor.translate("&7███████"));
            onlinePlayer.sendMessage("");
            onlinePlayer.sendMessage(ChatColor.GREEN + "You can purchase a booster at " + ChatColor.WHITE +  "https://store.cavepvp.org/boosters" + ChatColor.GREEN + "!");
        }
    }

    public void deactivate() {
        final Foxtrot instance = Foxtrot.getInstance();
        final NetworkBoosterHandler networkBoosterHandler = instance.getNetworkBoosterHandler();

        lastDeactivateAt = System.currentTimeMillis();
        active = false;
        activatedBy = null;

        final UUID uuid = networkBoosterHandler.getActiveBoosters().remove(this);
        HandlerList.unregisterAll(this);

        String name = UUIDUtils.name(uuid);

        for (Player onlinePlayer : instance.getServer().getOnlinePlayers()) {
            onlinePlayer.sendMessage("");
            onlinePlayer.sendMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Network Boosters");
            onlinePlayer.sendMessage(ChatColor.RED + "The " + this.getDisplayName() + " Booster " + ChatColor.RED + " has expired!");
            onlinePlayer.sendMessage(ChatColor.GRAY + "Thanks to " + ChatColor.WHITE + name + ChatColor.GRAY + " for activating this booster!");
            onlinePlayer.sendMessage("");
        }

        final Player player = Foxtrot.getInstance().getServer().getPlayer(uuid);

        if (player != null) {
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
            player.sendMessage(ChatColor.GREEN + "Your booster has been deactivated! Thank you for activating this booster!");
        }
    }
}
