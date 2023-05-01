package net.frozenorb.foxtrot.gameplay.ability.type;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.Ability;
import net.frozenorb.foxtrot.gameplay.ability.Category;
import net.frozenorb.foxtrot.team.Team;
import net.frozenorb.foxtrot.team.claims.LandBoard;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import net.frozenorb.foxtrot.util.CC;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class BallOfRage extends Ability {

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public Material getMaterial() {
        return Material.SNOW_BALL;
    }

    @Override
    public String getDisplayName() {
        return ChatColor.DARK_RED.toString() + ChatColor.BOLD + "Ball Of Rage";
    }

    @Override
    public List<String> getLore() {

        final List<String> toReturn = new ArrayList<>();

        toReturn.add("");
        toReturn.add(ChatColor.translate("&6❙ &fThrow to create a cloud of effects"));
        toReturn.add(ChatColor.translate("&6❙ &fwhere all teammates within 5 block"));
        toReturn.add(ChatColor.translate("&6❙ &fradius will be given positive effects."));
        toReturn.add("");
        toReturn.add(ChatColor.translate("&fCan be found in the &4&lSimplyTrash Crate&f!"));

        return toReturn;
    }

    @Override
    public boolean inPartnerPackage() {
        return true;
    }

    @Override
    public Category getCategory() {
        return Category.TREASURE_CHEST;
    }

    @Override
    public String getDescription() {
        return "Once this ball lands teammates effected will be given effects!";
    }

    @Override
    public Boolean isAllowedAtLocation(Location location) {
        if (location.getWorld().getName().equalsIgnoreCase("sg")) {
            return true;
        }
        if (Foxtrot.getInstance().getMapHandler().isKitMap()) {
            return !DTRBitmask.KOTH.appliesAt(location) && !DTRBitmask.CONQUEST.appliesAt(location) && !DTRBitmask.CITADEL.appliesAt(location) && !DTRBitmask.DTC.appliesAt(location) && !DTRBitmask.SAFE_ZONE.appliesAt(location);
        }

        return !Foxtrot.getInstance().getServerHandler().isWarzone(location) && location.getWorld().getEnvironment() == World.Environment.NORMAL && !DTRBitmask.KOTH.appliesAt(location) && !DTRBitmask.CONQUEST.appliesAt(location) && !DTRBitmask.CITADEL.appliesAt(location) && !DTRBitmask.DTC.appliesAt(location) && !DTRBitmask.SAFE_ZONE.appliesAt(location);
    }



    @Override
    public long getCooldown() {
        return 120_000L;
    }

    @EventHandler
    public void onLaunch(ProjectileLaunchEvent event) {
        if (!(event.getEntity() instanceof Snowball) || !(event.getEntity().getShooter() instanceof Player)) {
            return;
        }

        final Player player = (Player) event.getEntity().getShooter();

        if (!this.isSimilar(player.getItemInHand())) {
            return;
        }

        event.getEntity().setMetadata("BALL_OF_RAGE", new FixedMetadataValue(Foxtrot.getInstance(), player.getUniqueId().toString()));

        this.applyCooldown(player);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onInteract(PlayerInteractEvent event) {
        if (!event.hasItem() || !this.isSimilar(event.getPlayer().getItemInHand())) {
            return;
        }

        if (Foxtrot.getInstance().getPvPTimerMap().hasTimer(event.getPlayer().getUniqueId())) {
            event.getPlayer().sendMessage(CC.translate("&cYou may not use " + this.getDisplayName() + " &cwhilst your &a&lPvP Timer &cis active!"));
            event.getPlayer().updateInventory();
            event.setCancelled(true);
            return;
        }

        if (this.hasCooldown(event.getPlayer())) {
            event.setCancelled(true);
            event.getPlayer().updateInventory();
            return;
        }

        if (this.isAllowedAtLocation(event.getPlayer().getLocation())) {
            return;
        }

        String teamName;

        final Team ownerTeam = LandBoard.getInstance().getTeam(event.getPlayer().getLocation());

        if (ownerTeam != null) {
            teamName = ownerTeam.getName(event.getPlayer());
        } else if (!Foxtrot.getInstance().getServerHandler().isWarzone(event.getPlayer().getLocation())) {
            teamName = ChatColor.GRAY + "The Wilderness";
        } else {
            teamName = ChatColor.DARK_RED + "WarZone";
        }

        event.getPlayer().sendMessage(ChatColor.RED + "You cannot use a " + this.getDisplayName() + ChatColor.RED + " in " + teamName + ChatColor.RED + ".");
        event.getPlayer().updateInventory();
        event.setCancelled(true);
    }

    @EventHandler
    private void onLand(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Snowball) || !(event.getEntity().getShooter() instanceof Player) || !event.getEntity().hasMetadata("BALL_OF_RAGE")) {
            return;
        }

        final Projectile snowBall = event.getEntity();
        final Player player = (Player) snowBall.getShooter();

        snowBall.getWorld().createExplosion(snowBall.getLocation(), 0);
        snowBall.getWorld().spigot().playEffect(
                snowBall.getLocation().clone().add(0, 1, 0),
                Effect.EXPLOSION_HUGE
        );

        final Team team = Foxtrot.getInstance().getTeamHandler().getTeam(player);

        snowBall.getNearbyEntities(6, 6, 6).stream().filter(it -> it instanceof Player && this.isAllowedAtLocation(it.getLocation())).map(it -> (Player) it).forEach(it -> {
            if (team != null && !team.isMember(it.getUniqueId())) {
                return;
            }

            if (team == null && !it.getName().equalsIgnoreCase(player.getName())) {
                return;
            }

            it.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20*6, 1), true);
            it.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20*6, 2), true);

            it.sendMessage("");
            it.sendMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Ability Items");
            it.sendMessage(ChatColor.GRAY + "You have been hit by " + this.getDisplayName() + ChatColor.GRAY + ".");
            it.sendMessage(ChatColor.RED + "You were given Strength II and Resistance III for 5 seconds!");
            it.sendMessage("");
        });
    }
}
