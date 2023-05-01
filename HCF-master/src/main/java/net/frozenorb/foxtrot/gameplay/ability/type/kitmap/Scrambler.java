package net.frozenorb.foxtrot.gameplay.ability.type.kitmap;

import cc.fyre.proton.util.ItemBuilder;
import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.Ability;
import net.frozenorb.foxtrot.gameplay.ability.Category;
import net.frozenorb.foxtrot.gameplay.ability.listener.events.AbilityUseEvent;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Scrambler extends Ability {

    public Scrambler() {
        this.hassanStack = ItemBuilder.copyOf(this.hassanStack).data((byte)2).build();
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public Material getMaterial() {
        return Material.RAW_FISH;
    }

    @Override
    public String getDisplayName() {
        return ChatColor.BLUE.toString() + ChatColor.BOLD + "Scrambler";
    }

    @Override
    public List<String> getLore() {
        final List<String> toReturn = new ArrayList<>();

        toReturn.add("");
        toReturn.add(ChatColor.translate("&6❙ &fHit a player 3 times to"));
        toReturn.add(ChatColor.translate("&6❙ &fscramble their hotbar!"));
        toReturn.add("");
        toReturn.add(ChatColor.translate("&fCan be found in an &9&lHeaded Crate&f!"));

        return toReturn;
    }

    @Override
    public Boolean isAllowedAtLocation(Location location) {
        if (Foxtrot.getInstance().getMapHandler().isKitMap()) {
            return !DTRBitmask.KOTH.appliesAt(location) && !DTRBitmask.CONQUEST.appliesAt(location) && !DTRBitmask.CITADEL.appliesAt(location) && !DTRBitmask.DTC.appliesAt(location) && !DTRBitmask.SAFE_ZONE.appliesAt(location);
        }

        return !Foxtrot.getInstance().getServerHandler().isWarzone(location) && location.getWorld().getEnvironment() == World.Environment.NORMAL && !DTRBitmask.KOTH.appliesAt(location) && !DTRBitmask.CONQUEST.appliesAt(location) && !DTRBitmask.CITADEL.appliesAt(location) && !DTRBitmask.DTC.appliesAt(location) && !DTRBitmask.SAFE_ZONE.appliesAt(location);
    }

    @Override
    public long getCooldown() {
        return 200_000L;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onDamage(EntityDamageByEntityEvent event) {
        if (event.isCancelled() || !(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Player)) {
            return;
        }

        final Player target = (Player) event.getEntity();
        final Player damager = (Player) event.getDamager();

        if (damager.getItemInHand() == null || !this.isSimilar(damager.getItemInHand())) {
            return;
        }

        final AbilityUseEvent abilityUseEvent = new AbilityUseEvent(damager, target, damager.getLocation(), this, false);

        int value = target.hasMetadata("SCRAMBLER") ? (target.getMetadata("SCRAMBLER").get(0).asInt() + 1) : 1;

        if (value != 3) {
            abilityUseEvent.setOneHit(true);
        }

        Foxtrot.getInstance().getServer().getPluginManager().callEvent(abilityUseEvent);

        if (abilityUseEvent.isCancelled()) {
            return;
        }

        target.setMetadata("SCRAMBLER", new FixedMetadataValue(Foxtrot.getInstance(), value));

        if (value != 3) {
            damager.sendMessage(CC.translate("&cYou have to hit &f" + target.getName() + " &c" + (3 - value) + " more time" + (3 - value == 1 ? "" : "s") + "!"));
            return;
        }

        target.removeMetadata("SCRAMBLER", Foxtrot.getInstance());

        if (damager.getItemInHand().getAmount() == 1) {
            damager.setItemInHand(null);
        } else {
            damager.getItemInHand().setAmount(damager.getItemInHand().getAmount() - 1);
        }

        List<ItemStack> hi = new ArrayList<>();

        for (int i = 0; i < 9; i++) {
            hi.add(target.getInventory().getItem(i));
        }

        Collections.shuffle(hi);

        for (int i = 0; i < 9; i++) {
            target.getInventory().setItem(i, hi.get(i));
        }

        this.fullDescription = "You have scrambled the inventory of " + target.getName() + "!";

        target.sendMessage("");
        target.sendMessage(CC.translate("&4" + damager.getName() + " &chas hit you with " + this.getDisplayName() + "&c!"));
        target.sendMessage(CC.translate("&cYour inventory has been scrambled."));
        target.sendMessage("");

        this.applyCooldown(damager);
    }

    @Override
    public Category getCategory() {
        return Category.KIT_MAP;
    }

    @Override
    public String getDescription() {
        return "";
    }


}