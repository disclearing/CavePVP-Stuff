package net.frozenorb.foxtrot.gameplay.ability.type;

import cc.fyre.proton.Proton;
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
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class SoulStone extends Ability {
    @Override
    public Category getCategory() {
        return Category.TREASURE_CHEST;
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public Material getMaterial() {
        return Material.GOLD_INGOT;
    }

    @Override
    public String getDisplayName() {
        return ChatColor.YELLOW.toString() + ChatColor.BOLD + "Soul Stone";
    }

    @Override
    public String getDescription() {
        return "Upon going below 2 hearts you will be healed and given effects.";
    }

    @Override
    public List<String> getLore() {
        final List<String> toReturn = new ArrayList<>();

        toReturn.add("");
        toReturn.add(ChatColor.translate("&6❙ &fUpon going below 2 hearts, you will be instantly"));
        toReturn.add(ChatColor.translate("&6❙ &fhealed and given Resistance III for &e&l5 seconds&f."));
        toReturn.add("");
        toReturn.add(ChatColor.translate("&fCan be found in the &6&lOctober Mystery Box&f!"));

        return toReturn;
    }

    @Override
    public Boolean isAllowedAtLocation(Location location) {
        final Team teamAt = LandBoard.getInstance().getTeam(location);

        if (teamAt != null && teamAt.getOwner() != null && teamAt.getOwner().toString().equals("dad8441f-dece-499d-a894-74cf3bd63d4a")) {
            return false;
        }

        if (Foxtrot.getInstance().getMapHandler().isKitMap()) {
            return !DTRBitmask.KOTH.appliesAt(location) && !DTRBitmask.CONQUEST.appliesAt(location) && !DTRBitmask.CITADEL.appliesAt(location) && !DTRBitmask.DTC.appliesAt(location) && !DTRBitmask.SAFE_ZONE.appliesAt(location);
        }

        return !Foxtrot.getInstance().getServerHandler().isWarzone(location) && location.getWorld().getEnvironment() == World.Environment.NORMAL && !DTRBitmask.KOTH.appliesAt(location) && !DTRBitmask.CONQUEST.appliesAt(location) && !DTRBitmask.CITADEL.appliesAt(location) && !DTRBitmask.DTC.appliesAt(location) && !DTRBitmask.SAFE_ZONE.appliesAt(location);
    }

    @Override
    public long getCooldown() {
        return 150_000L;
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

        final Block belowBlock = blockAt.getBlock().getRelative(BlockFace.DOWN);

        if (!player.isOnGround() && belowBlock.getType() == Material.AIR && belowBlock.getRelative(BlockFace.DOWN).getType() == Material.AIR && belowBlock.getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).getType() == Material.AIR) {
            player.sendMessage(ChatColor.RED + "You may not use the " + this.getDisplayName() + ChatColor.RED + " in the air!");
            return;
        }

        if (Proton.getInstance().getAutoRebootHandler().isRebooting()) {
            player.sendMessage(ChatColor.RED + "You may not use this whilst the server is rebooting!");
            return;
        }

        final AbilityUseEvent abilityUseEvent = new AbilityUseEvent(player, null, player.getLocation(), this, false);
        Foxtrot.getInstance().getServer().getPluginManager().callEvent(abilityUseEvent);

        if (abilityUseEvent.isCancelled()) {
            return;
        }

        event.setCancelled(true);

        if (player.getItemInHand().getAmount() == 1) {
            player.setItemInHand(null);
        } else {
            player.getItemInHand().setAmount(player.getItemInHand().getAmount()-1);
        }

        player.setMetadata("SOUL_STONE", new FixedMetadataValue(Foxtrot.getInstance(), true));

        this.applyCooldown(player);
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        final Player player = (Player) event.getEntity();

        if (!player.hasMetadata("SOUL_STONE")) {
            return;
        }

        if (player.getHealth() > 4) {
            return;
        }

        player.setHealth(player.getMaxHealth());
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*6, 2));
        player.removeMetadata("SOUL_STONE", Foxtrot.getInstance());

        player.sendMessage("");
        player.sendMessage(ChatColor.GREEN + "Your " + this.getDisplayName() + ChatColor.GREEN + " has activated!");
        player.sendMessage(ChatColor.GRAY + "You have been fully healed and received Resistance III for 5 seconds.");
        player.sendMessage("");
    }
}