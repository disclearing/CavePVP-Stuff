package net.frozenorb.foxtrot.gameplay.ability.type;

import cc.fyre.proton.util.TimeUtils;
import lombok.Getter;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.Ability;
import net.frozenorb.foxtrot.gameplay.ability.Category;
import net.frozenorb.foxtrot.gameplay.ability.listener.events.AbilityUseEvent;
import net.frozenorb.foxtrot.listener.FoxListener;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.util.CC;
import net.frozenorb.foxtrot.util.InventoryUtils;
import net.frozenorb.foxtrot.util.PotionUtil;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.Potion;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.bukkit.Material.POTION;

public class AntiBlockup extends Ability {

    @Getter
    public static Map<UUID, Long> cache = new HashMap<>();

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public Material getMaterial() {
        return Material.BLAZE_ROD;
    }

    @Override
    public String getDisplayName() {
        return ChatColor.GOLD.toString() + ChatColor.BOLD + "Anti-Blockup";
    }

    @Override
    public List<String> getLore() {
        final List<String> toReturn = new ArrayList<>();

        toReturn.add("");
        toReturn.add(ChatColor.translate("&6❙ &fWhen you hit a player with this"));
        toReturn.add(ChatColor.translate("&6❙ &c&l3 times &fthey may not block up for &e&l15 seconds&f."));
        toReturn.add("");
        if (Foxtrot.getInstance().getMapHandler().isKitMap()) {
            toReturn.add(ChatColor.translate("&fCan be found in the &d&lAbility Crate&f!"));
        } else {
            toReturn.add(ChatColor.translate("&fCan be found in the &e&ki&6&lHalloween Crate&e&ki&f!"));
        }

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
        return 90_000L;
    }

    @Override
    public Category getCategory() {
        return Category.SEASONAL_CRATE;
    }

    @Override
    public String getDescription() {
        return "";
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

        if (cache.containsKey(target.getUniqueId())) {
            damager.sendMessage(ChatColor.translate("&c" + target.getName() + " already can't place blocks for &l" + TimeUtils.formatIntoMMSS((int) (cache.get(target.getUniqueId()) - System.currentTimeMillis()) / 1000) + "&c."));
            return;
        }

        int value = target.hasMetadata("ANTI_BUILD") ? (target.getMetadata("ANTI_BUILD").get(0).asInt() + 1) : 1;

        final AbilityUseEvent abilityUseEvent = new AbilityUseEvent(damager, target, damager.getLocation(), this, false);

        if (value != 3) {
            abilityUseEvent.setOneHit(true);
        }

        Foxtrot.getInstance().getServer().getPluginManager().callEvent(abilityUseEvent);

        if (abilityUseEvent.isCancelled()) {
            return;
        }

        target.setMetadata("ANTI_BUILD", new FixedMetadataValue(Foxtrot.getInstance(), value));

        if (value != 3) {
            damager.sendMessage(CC.translate("&cYou have to hit &f" + target.getName() + " &c" + (3 - value) + " more time" + (3 - value == 1 ? "" : "s") + "!"));
            return;
        }

        target.removeMetadata("ANTI_BUILD", Foxtrot.getInstance());

        cache.put(target.getUniqueId(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(15));

        if (damager.getItemInHand().getAmount() == 1) {
            damager.setItemInHand(null);
        } else {
            damager.getItemInHand().setAmount(damager.getItemInHand().getAmount() - 1);
        }

        fullDescription = target.getName() + " may not place blocks for 15 seconds!";

        target.sendMessage("");
        target.sendMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Ability Items");
        target.sendMessage(ChatColor.GRAY + "You have been hit with the " + this.getDisplayName() + ChatColor.GRAY + ".");
        target.sendMessage(ChatColor.RED + "You may not place/break/interact with blocks for the next 15 seconds!");
        target.sendMessage("");

        Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
            cache.remove(target.getUniqueId());

            if (damager.isOnline()) {
                damager.sendMessage("");
                damager.sendMessage(ChatColor.RED + "Your " + this.getDisplayName() + ChatColor.RED + " has expired!");
                damager.sendMessage("");
            }

            if (target.isOnline()) {
                target.sendMessage("");
                target.sendMessage(ChatColor.GREEN + "The " + this.getDisplayName() + ChatColor.GREEN + " on you has expired!");
                target.sendMessage("");
            }
        }, 20*15);

        this.applyCooldown(damager);

    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final Player player = event.getPlayer();

        if (!cache.containsKey(player.getUniqueId())) {
            return;
        }

        player.sendMessage(ChatColor.RED + "You may not block up as you have been hit by the " + this.getDisplayName() + ChatColor.RED + ".");
        event.setCancelled(true);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final Player player = event.getPlayer();

        if (!cache.containsKey(player.getUniqueId())) {
            return;
        }

        player.sendMessage(ChatColor.RED + "You may not break blocks as you have been hit by the " + this.getDisplayName() + ChatColor.RED + ".");

        event.setCancelled(true);
    }

    @EventHandler
    private void onPlate(PlayerInteractEvent event) {
        if ((event.getAction() != Action.PHYSICAL || event.getClickedBlock() == null || !event.getClickedBlock().getType().name().contains("PLATE"))) {
            return;
        }

        final Player player = event.getPlayer();

        if (!cache.containsKey(player.getUniqueId())) {
            return;
        }

        if (event.getItem() != null && event.getItem().getType() == POTION && event.getItem().getDurability() != 0) {
            Potion potion = Potion.fromItemStack(event.getItem());

            if (potion.isSplash()) {
                PotionUtil.splashPotion(player, event.getItem());
                if (player.getItemInHand() != null && player.getItemInHand().isSimilar(event.getItem())) {
                    player.setItemInHand(null);
                    player.updateInventory();
                } else {
                    InventoryUtils.removeAmountFromInventory(player.getInventory(), event.getItem(), 1);
                }
            }
        }

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getClickedBlock() == null) {
            return;
        }

        final Block clickedBlock = event.getClickedBlock();

        if (!FoxListener.NO_INTERACT.contains(clickedBlock.getType())) {
            return;
        }

        final Player player = event.getPlayer();

        if (!cache.containsKey(player.getUniqueId())) {
            return;
        }

        if (event.getItem() != null && event.getItem().getType() == POTION && event.getItem().getDurability() != 0) {
            Potion potion = Potion.fromItemStack(event.getItem());

            if (potion.isSplash()) {
                PotionUtil.splashPotion(player, event.getItem());
                if (player.getItemInHand() != null && player.getItemInHand().isSimilar(event.getItem())) {
                    player.setItemInHand(null);
                    player.updateInventory();
                } else {
                    InventoryUtils.removeAmountFromInventory(player.getInventory(), event.getItem(), 1);
                }
            }
        }

        event.setCancelled(true);
        player.sendMessage(ChatColor.RED + "You may not interact with " + event.getClickedBlock().getType().name().toLowerCase().replace("_", " ") + "s as you have been hit by the " + this.getDisplayName() + ChatColor.RED + ".");
    }


}