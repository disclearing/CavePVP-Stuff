package net.frozenorb.foxtrot.gameplay.ability.type;

import cc.fyre.neutron.Neutron;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.Ability;
import net.frozenorb.foxtrot.gameplay.ability.Category;
import net.frozenorb.foxtrot.gameplay.ability.listener.events.AbilityUseEvent;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ZeusHammer extends Ability {

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
        return ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "Zeus Hammer";
    }

    @Override
    public List<String> getLore() {
        final List<String> toReturn = new ArrayList<>();

        toReturn.add("");
        toReturn.add(ChatColor.translate("&6❙ &fClick to activate and for 10 seconds, all hits"));
        toReturn.add(ChatColor.translate("&6❙ &fdealt have a 10% chance of striking the enemy with lightning!"));
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
        return 195_000L;
    }

    @Override
    public Category getCategory() {
        return Category.AIRDROPS;
    }

    @Override
    public String getDescription() {
        return "Each hit has a chance of striking the enemy with lightning.";
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.LEFT_CLICK_AIR && event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        final Player player = event.getPlayer();

        if (!this.isSimilar(event.getItem())) {
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

        player.setMetadata("HAMMER", new FixedMetadataValue(Foxtrot.getInstance(), true));

        Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> player.removeMetadata("HAMMER", Foxtrot.getInstance()), 20*15);

        this.applyCooldown(player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onPlayerHit(EntityDamageByEntityEvent event) {

        if (event.isCancelled() || !(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Player)) {
            return;
        }

        final Player target = (Player) event.getEntity();
        final Player damager = (Player) event.getDamager();

        if (!damager.hasMetadata("HAMMER")) {
            return;
        }

        if (ThreadLocalRandom.current().nextInt(100) > 10) {
            return;
        }

        damager.playSound(damager.getLocation(), Sound.LEVEL_UP, 1, 1);
        damager.sendMessage(ChatColor.RED + "You have struck " + Neutron.getInstance().getProfileHandler().findDisplayName(target.getUniqueId()) + ChatColor.RED + " with lightning!");

        double damage = ThreadLocalRandom.current().nextBoolean() ? 2 : 3;

        target.setHealth(target.getHealth()-damage);
        target.getWorld().strikeLightningEffect(target.getLocation().clone());
        target.damage(0);
        target.playSound(target.getLocation(), Sound.ZOMBIE_WOODBREAK, 1, 1);
        target.sendMessage(ChatColor.RED + "You struck by lightning due to the " + this.getDisplayName() + ChatColor.RED + "!");
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamage(EntityDamageEvent event) {

        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        if (event.getCause() != EntityDamageEvent.DamageCause.LIGHTNING) {
            return;
        }

        event.setCancelled(true);
    }
}
