package net.frozenorb.foxtrot.gameplay.ability.type;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.Ability;
import net.frozenorb.foxtrot.gameplay.ability.Category;
import net.frozenorb.foxtrot.gameplay.ability.listener.events.AbilityUseEvent;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.ExplosiveMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TNTMinecart extends Ability {
    @Override
    public Category getCategory() {
        return Category.AIRDROPS;
    }

    @Override
    public String getDescription() {
        return "A TNT Minecart has been spawned 50 blocks above you.";
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public Material getMaterial() {
        return Material.EXPLOSIVE_MINECART;
    }

    @Override
    public String getDisplayName() {
        return ChatColor.DARK_RED.toString() + ChatColor.BOLD + "TNT Minecart";
    }

    @Override
    public List<String> getLore() {
        final List<String> toReturn = new ArrayList<>();

        toReturn.add("");
        toReturn.add(ChatColor.translate("&6❙ &fRight Click to spawn a TNT Minecart"));
        toReturn.add(ChatColor.translate("&6❙ &c&l50 blocks &fabove your head!"));
        toReturn.add("");
        toReturn.add(ChatColor.translate("&fCan be found in an &b&lAirdrop&f!"));

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
        return 2_500L;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onInteract(PlayerInteractEvent event) {

        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        final Player player = event.getPlayer();

        final Location blockAt = player.getLocation();

        if (!this.isSimilar(event.getItem())) {
            return;
        }

        if (Foxtrot.getInstance().getMapHandler().isKitMap()) {
            player.sendMessage(ChatColor.RED + "This item has been disabled!");
            return;
        }

        if (!Foxtrot.getInstance().getMapHandler().isKitMap() && Foxtrot.getInstance().getServerHandler().isWarzone(player.getLocation())) {
            player.sendMessage(ChatColor.RED + "You may not use this in WarZone!");
            return;
        }

        final AbilityUseEvent abilityUseEvent = new AbilityUseEvent(player, null, player.getLocation(), this, false);
        Foxtrot.getInstance().getServer().getPluginManager().callEvent(abilityUseEvent);

        if (abilityUseEvent.isCancelled()) {
            return;
        }

        event.setCancelled(true);

        if (Foxtrot.getInstance().getPvPTimerMap().hasTimer(player.getUniqueId())) {
            player.sendMessage(CC.translateAlternateColorCodes("&cYou may not deploy a &c&lTNT Minecart &cwhile your &a&lPvP Timer &cis active!"));
            return;
        }

        blockAt.setY(blockAt.getY() + 50);
        final Entity entity = player.getWorld().spawn(blockAt, ExplosiveMinecart.class);
        entity.setMetadata("TNT_MINECART", new FixedMetadataValue(Foxtrot.getInstance(), player.getUniqueId().toString()));

        ItemStack itemStack = player.getItemInHand();
        itemStack.setAmount(itemStack.getAmount() - 1);

        player.setItemInHand(itemStack);
        player.updateInventory();

        applyCooldown(player);
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof ExplosiveMinecart) || !(event.getEntity() instanceof Player)) {
            return;
        }

        final Player victim = (Player) event.getEntity();

        if (Foxtrot.getInstance().getPvPTimerMap().hasTimer(victim.getUniqueId())) {
            event.setCancelled(true);
            return;
        }

        if (!Foxtrot.getInstance().getMapHandler().isKitMap()) {
            event.setDamage(event.getDamage()*0.8);
            return;
        }

        if (Arrays.stream(victim.getInventory().getArmorContents()).anyMatch(it -> it.containsEnchantment(Enchantment.PROTECTION_EXPLOSIONS))) {
            event.setDamage(event.getDamage()*1.8);
            return;
        }

        event.setDamage(event.getDamage()*1.3);
    }
}
