package net.frozenorb.foxtrot.gameplay.ability;

import cc.fyre.proton.util.ItemBuilder;
import cc.fyre.proton.util.TimeUtils;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.events.mini.MiniEvent;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class Ability implements Listener {

    public Ability() {
        final List<String> finalLore = new ArrayList<>(this.getLore());

        if (this.inPartnerPackage() && Foxtrot.getInstance().getMapHandler().isKitMap()) {
            this.getLore().stream().filter(it -> it.contains(ChatColor.WHITE + "Can be found in")).forEach(it -> {
                int index = this.getLore().indexOf(it);

                finalLore.remove(it);
                finalLore.add(index, ChatColor.translate("&fCan be found in a &d&lPartner Package&f!"));
            });
        }

        this.hassanStack = ItemBuilder.of(this.getMaterial())
                .name(this.getDisplayName() == null ? this.getName() : this.getDisplayName())
                .setLore(this.getLore() == null ? new ArrayList<>() : finalLore)
                .build();

        this.fullDescription = this.getDescription();

        Foxtrot.getInstance().getServer().getPluginManager().registerEvents(this, Foxtrot.getInstance());
    }

    public abstract String getName();
    public abstract Material getMaterial();
    public abstract String getDisplayName();
    public abstract List<String> getLore();
    public abstract Boolean isAllowedAtLocation(Location location);
    public abstract long getCooldown();
    public abstract Category getCategory();
    public abstract String getDescription();

    public boolean inPartnerPackage() {
        return false;
    }

    public ItemStack hassanStack;
    public String fullDescription;

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

        return itemStack.getType() == this.getMaterial() && ChatColor.stripColor(itemStack.getItemMeta().getDisplayName()).startsWith(ChatColor.stripColor(this.hassanStack.getItemMeta().getDisplayName())) && itemStack.getItemMeta().getLore().get(0).equals(this.getLore().get(0));
    }

    public void removeCooldown(Player player) {
        AbilityHandler.getCooldown().remove(player.getUniqueId(), this);
    }

    public void applyCooldown(Player player) {
        player.sendMessage("");
        player.sendMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Ability Items");
        player.sendMessage(ChatColor.translate("&7You have activated the &f" + this.getDisplayName() + "&7!"));
        player.sendMessage(ChatColor.RED + ChatColor.translate(this.fullDescription));
        player.sendMessage("");

        if (player.hasMetadata("DEBUG")) {
            player.sendMessage(ChatColor.RED + "Skipped cooldown as you had Debug mode enabled!");
            return;
        }

        Foxtrot.getInstance().getMapHandler().getAbilityHandler().applyCooldown(this, player);
    }

    public void applyCooldown(Team team, Player target) {
        team.sendMessage(ChatColor.translate(target.getName() + " &chas used &f" + this.getDisplayName() + " &cand put your entire team on cooldown for &l" + TimeUtils.formatIntoDetailedString((int) (this.getCooldown() / 1000))));

        target.sendMessage("");
        target.sendMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Ability Items");
        target.sendMessage(ChatColor.translate("&7You have activated the &f" + this.getDisplayName() + "&7!"));
        target.sendMessage(ChatColor.RED + ChatColor.translate(this.fullDescription));
        target.sendMessage("");

        for (Player onlineMember : team.getOnlineMembers()) {
            if (onlineMember.hasMetadata("DEBUG")) {
                onlineMember.sendMessage(ChatColor.RED + "Skipped cooldown as you had Debug mode enabled!");
                return;
            }

            Foxtrot.getInstance().getMapHandler().getAbilityHandler().applyCooldown(this, onlineMember);
        }
    }

    public void applyCooldown(Team team, Player target, long time) {
        team.sendMessage(ChatColor.translate(target.getName() + " &chas used &f" + this.getDisplayName() + " &cand put your entire team on cooldown for &l" + TimeUtils.formatIntoDetailedString((int) (time / 1000))));

        target.sendMessage("");
        target.sendMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Ability Items");
        target.sendMessage(ChatColor.translate("&7You have activated the &f" + this.getDisplayName() + "&7!"));
        target.sendMessage(ChatColor.RED + ChatColor.translate(this.fullDescription));
        target.sendMessage("");

        for (Player onlineMember : team.getOnlineMembers()) {
            if (onlineMember.hasMetadata("DEBUG")) {
                onlineMember.sendMessage(ChatColor.RED + "Skipped cooldown as you had Debug mode enabled!");
                return;
            }

            Foxtrot.getInstance().getMapHandler().getAbilityHandler().applyCooldown(this, onlineMember, time);
        }
    }

    public boolean hasCooldown(Player player) {
        return this.hasCooldown(player, true);
    }

    public boolean hasCooldown(Player player, boolean sendMessage) {

        final long current = Foxtrot.getInstance().getMapHandler().getAbilityHandler().getRemaining(this, player);

        if (current <= 0) {
            return false;
        }

        if (sendMessage) {
            player.sendMessage(ChatColor.RED + "You cannot use the " + this.getDisplayName() + ChatColor.RED + " for another " + ChatColor.BOLD + TimeUtils.formatIntoDetailedString((int) (current / 1000)) + ChatColor.RED + ".");
        }

        return true;
    }

    public long getRemaining(Player player) {
        return Foxtrot.getInstance().getMapHandler().getAbilityHandler().getRemaining(this, player);
    }

}