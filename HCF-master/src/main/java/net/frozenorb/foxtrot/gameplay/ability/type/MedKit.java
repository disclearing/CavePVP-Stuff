package net.frozenorb.foxtrot.gameplay.ability.type;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.Ability;
import net.frozenorb.foxtrot.gameplay.ability.Category;
import net.frozenorb.foxtrot.gameplay.ability.listener.events.AbilityUseEvent;
import net.frozenorb.foxtrot.gameplay.armorclass.ArmorClass;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class MedKit extends Ability {
    @Override
    public Category getCategory() {
        return Category.SEASONAL_CRATE;
    }

    @Override
    public String getDescription() {
        return "You have been given health effects for 5 seconds!";
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public Material getMaterial() {
        return Material.PAPER;
    }

    @Override
    public String getDisplayName() {
        return ChatColor.GREEN.toString() + ChatColor.BOLD + "Med Kit";
    }

    @Override
    public List<String> getLore() {
        final List<String> toReturn = new ArrayList<>();

        toReturn.add("");
        if (Foxtrot.getInstance().getMapHandler().isKitMap()) {
            toReturn.add(ChatColor.translate("&6❙ &fRight Click to get fully healed"));
            toReturn.add(ChatColor.translate("&6❙ &fand receive Resistance 3, 4 Absorption hearts!"));
            toReturn.add("");
            toReturn.add(ChatColor.translate("&fCan be found in an &b&lAirdrop&f!"));
        } else {
            toReturn.add(ChatColor.translate("&6❙ &fRight Click to receive Resistance 3,"));
            toReturn.add(ChatColor.translate("&6❙ &fRegeneration 3, and 4 Absorption"));
            toReturn.add(ChatColor.translate("&6❙ &fHearts for &e&l5 seconds&f!"));
            toReturn.add("");
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

    @EventHandler(priority = EventPriority.NORMAL)
    public void onInteract(PlayerInteractEvent event) {
        if (!event.getAction().name().contains("RIGHT")) {
            return;
        }

        final Player player = event.getPlayer();

        final Location blockAt = player.getLocation();

        if (!this.isSimilar(event.getItem())) {
            return;
        }

        final Block belowBlock = blockAt.getBlock().getRelative(BlockFace.DOWN);

        if (!Foxtrot.getInstance().getMapHandler().isKitMap() && !player.isOnGround() && belowBlock.getType() == Material.AIR && belowBlock.getRelative(BlockFace.DOWN).getType() == Material.AIR && belowBlock.getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).getType() == Material.AIR) {
            player.sendMessage(ChatColor.RED + "You may not use the " + this.getDisplayName() + ChatColor.RED + " in the air!");
            return;
        }

        final AbilityUseEvent abilityUseEvent = new AbilityUseEvent(player, null, player.getLocation(), this, false);
        Foxtrot.getInstance().getServer().getPluginManager().callEvent(abilityUseEvent);

        if (abilityUseEvent.isCancelled()) {
            return;
        }

        int seconds = 6;

        final ArmorClass armorClass = Foxtrot.getInstance().getArmorClassHandler().findWearing(player);

        if (armorClass != null && armorClass.getId().equalsIgnoreCase("Trapper")) {
            seconds = 8;
        }

        event.setCancelled(true);

        if (player.getItemInHand().getAmount() == 1) {
            player.setItemInHand(null);
        } else {
            player.getItemInHand().setAmount(player.getItemInHand().getAmount()-1);
        }

        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, seconds*20, 2), true);
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, seconds*20, 2), true);
        player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 30*20, 1), true);

        this.applyCooldown(player);
    }
}