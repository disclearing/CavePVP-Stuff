package net.frozenorb.foxtrot.gameplay.ability.type;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.ability.Ability;
import net.frozenorb.foxtrot.gameplay.ability.Category;
import net.frozenorb.foxtrot.gameplay.ability.listener.events.AbilityUseEvent;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.ArrayList;
import java.util.List;

public class ShockWave extends Ability {
    @Override
    public Category getCategory() {
        return Category.SEASONAL_CRATE;
    }

    @Override
    public String getDescription() {
        return "Once this fireball lands players effected will be launched!";
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public Material getMaterial() {
        return Material.FIREBALL;
    }

    @Override
    public String getDisplayName() {
        return ChatColor.DARK_RED.toString() + ChatColor.BOLD + "Shockwave";
    }

    @Override
    public List<String> getLore() {

        final List<String> toReturn = new ArrayList<>();

        toReturn.add("");
        toReturn.add(ChatColor.translate("&6❙ &fThrow this and everybody within"));
        toReturn.add(ChatColor.translate("&6❙ &c&l10 blocks &fof where it lands will be launched."));
        toReturn.add("");
        if (Foxtrot.getInstance().getMapHandler().isKitMap()) {
            toReturn.add(ChatColor.translate("&fCan be found in an &b&lAirdrop&f!"));
        } else {
            toReturn.add(ChatColor.translate("&fCan be found in the &4&lCave Crate&f!"));
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
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        final Player player = event.getPlayer();

        if (!this.isSimilar(event.getItem())) {
            return;
        }

        event.setCancelled(true);

        final AbilityUseEvent abilityUseEvent = new AbilityUseEvent(player, null, player.getLocation(), this, false);
        Foxtrot.getInstance().getServer().getPluginManager().callEvent(abilityUseEvent);

        if (abilityUseEvent.isCancelled()) {
            return;
        }

        if (player.getItemInHand().getAmount() == 1) {
            player.setItemInHand(null);
        } else {
            player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
        }

        final Fireball fireball = player.launchProjectile(Fireball.class);

        fireball.setMetadata("SHOCK_WAVE", new FixedMetadataValue(Foxtrot.getInstance(), player.getUniqueId().toString()));
        fireball.setIsIncendiary(false);
        fireball.setYield(0.0f);
        fireball.setShooter(player);

        this.applyCooldown(player);
    }

    @EventHandler
    private void onLand(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Fireball) || !(event.getEntity().getShooter() instanceof Player) || !event.getEntity().hasMetadata("SHOCK_WAVE")) {
            return;
        }

        final Projectile fireBall = event.getEntity();
        final Player player = (Player) fireBall.getShooter();

        fireBall.getNearbyEntities(10, 10, 10).stream().filter(it -> it instanceof Player && this.isAllowedAtLocation(it.getLocation())).forEach(it -> {
            it.setVelocity(it.getLocation().getDirection().multiply(-2.5));

            ((Player) it).setHealth(((Player) it).getHealth() - 1);

            ((Player) it).sendMessage("");
            ((Player) it).sendMessage(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Ability Items");
            ((Player) it).sendMessage(ChatColor.GRAY + "You have been hit with the " + this.getDisplayName() + ChatColor.GRAY + "!");
            ((Player) it).sendMessage(ChatColor.RED + "You have been sent flying in the air!");
            ((Player) it).sendMessage("");
        });
    }
}
