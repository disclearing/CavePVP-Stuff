package net.frozenorb.foxtrot.gameplay.ability.type;

import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.Ability;
import net.frozenorb.foxtrot.gameplay.ability.Category;
import net.frozenorb.foxtrot.gameplay.ability.listener.events.AbilityUseEvent;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ItemCounter extends Ability {
    @Override
    public Category getCategory() {
        return Category.PARTNER_CRATE;
    }

    @Override
    public String getDescription() {
        return "COOLDOWN";
    }

    @Getter
    public static List<UUID> cache = new ArrayList<>();

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public Material getMaterial() {
        return Material.COMPASS;
    }

    @Override
    public String getDisplayName() {
        return ChatColor.RED.toString() + ChatColor.BOLD + "Item Counter";
    }

    @Override
    public List<String> getLore() {
        final List<String> toReturn = new ArrayList<>();

        toReturn.add("");
        toReturn.add(ChatColor.translate("&6❙ &fHit an enemy to discover how many"));
        toReturn.add(ChatColor.translate("&6❙ &fhealth potions and ability items they have!"));
        toReturn.add("");
        toReturn.add(ChatColor.translate("&fCan be found in the &4&lCave Crate&f!"));

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
        return 35_000L;
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
        Foxtrot.getInstance().getServer().getPluginManager().callEvent(abilityUseEvent);

        if (abilityUseEvent.isCancelled()) {
            return;
        }

        final Map<Ability, Integer> abilities = new HashMap<>();

        int crapples = 0;
        int godApples = 0;

        for (ItemStack content : target.getInventory().getContents()) {

            if (content == null || content.getType() == Material.AIR) {
                continue;
            }

            if (content.getType() == Material.GOLDEN_APPLE && content.getData().getData() == 0) {
                crapples += content.getAmount();
                continue;
            }

            if (content.getType() == Material.GOLDEN_APPLE && content.getData().getData() == 1) {
                godApples += content.getAmount();
                continue;
            }

            final Ability ability = Foxtrot.getInstance().getMapHandler().getAbilityHandler().getAbilities().values().stream().filter(it -> it.isSimilar(content)).findFirst().orElse(null);

            if (ability == null) {
                continue;
            }

            int amount = abilities.getOrDefault(ability, 0)+content.getAmount();

            abilities.put(ability, amount);
        }

        int amount = (int) Arrays.stream(target.getInventory().getContents()).filter(it -> it != null && it.getType() == Material.POTION && it.getDurability() == 16421).count();

        damager.sendMessage("");
        damager.sendMessage(ChatColor.translate(target.getName() + " &chas &f" + amount + " &chealth potions in their inventory."));
        damager.sendMessage(ChatColor.translate(target.getName() + " &chas &f" + crapples + " &ccrapples in their inventory."));
        damager.sendMessage(ChatColor.translate(target.getName() + " &chas &f" + godApples + " &cgod apples in their inventory."));
        if (!abilities.isEmpty()) {
            damager.sendMessage(ChatColor.translate("&4&lAbilities:"));
        }

        for (Map.Entry<Ability, Integer> abilityIntegerEntry : abilities.entrySet()) {
            damager.sendMessage(ChatColor.translate("&4| &f" + abilityIntegerEntry.getKey().getDisplayName() + ": &c" + abilityIntegerEntry.getValue()));
        }

        damager.sendMessage("");

        final ItemStack itemStack = damager.getItemInHand();

        if (itemStack.getAmount() == 1) {
            damager.setItemInHand(null);
        } else {
            itemStack.setAmount(itemStack.getAmount()-1);
        }

        this.applyCooldown(damager);
    }
}