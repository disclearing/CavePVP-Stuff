package net.frozenorb.foxtrot.listener;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.server.SpawnTagHandler;
import net.frozenorb.foxtrot.listener.event.DisallowedPotionDrinkEvent;
import net.frozenorb.foxtrot.team.dtr.DTRBitmask;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionType;

import java.util.Iterator;

public class PotionLimiterListener implements Listener {
    @EventHandler(priority=EventPriority.HIGH)
    public void onPotionSplash(PotionSplashEvent event) {
        for (LivingEntity livingEntity : event.getAffectedEntities()) {
            if (DTRBitmask.SAFE_ZONE.appliesAt(livingEntity.getLocation())) {
                event.setIntensity(livingEntity, 0D);
            }
        }

        Potion potion = Potion.fromItemStack(event.getPotion().getItem());
        if (!Foxtrot.getInstance().getMapHandler().isKitMap()) {

            if (Foxtrot.getInstance().getServerHandler().isDrinkablePotionAllowed(potion.getType()) || Foxtrot.getInstance().getServerHandler().isPotionLevelAllowed(potion.getType(), potion.getLevel())) {
                event.setCancelled(true);
            } else if (potion.hasExtendedDuration() && (potion.getType() == PotionType.SLOWNESS || potion.getType() == PotionType.POISON)) {
                event.setCancelled(true);
            } else if (potion.getType() == PotionType.POISON && 1 < potion.getLevel()) {
                event.setCancelled(true);
            }

        }

        if (potion.getType() == PotionType.INSTANT_DAMAGE) {
            event.setCancelled(true);
            return;
        } else if (potion.getType() == PotionType.STRENGTH) {
            event.setCancelled(true);
            return;
        }

        if (event.getPotion().getShooter() != null && event.getPotion().getShooter() instanceof Player && !event.isCancelled()) {
            Iterator<PotionEffect> iterator = event.getPotion().getEffects().iterator();

            if (iterator.hasNext()) {
                if (FoxListener.DEBUFFS.contains(iterator.next().getType())) {
                    if (event.getAffectedEntities().size() > 1 || (event.getAffectedEntities().size() == 1 && !event.getAffectedEntities().contains(event.getPotion().getShooter()))) {
                        SpawnTagHandler.addOffensiveSeconds((Player) event.getPotion().getShooter(), SpawnTagHandler.getMaxTagTime((Player) event.getPotion().getShooter()));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        if (event.getItem().getType() != Material.POTION || event.getItem().getDurability() == 0) {
            return;
        }

        Potion potion = Potion.fromItemStack(event.getItem());

        if (Foxtrot.getInstance().getServerHandler().isDrinkablePotionAllowed(potion.getType()) || Foxtrot.getInstance().getServerHandler().isPotionLevelAllowed(potion.getType(), potion.getLevel())) {
            DisallowedPotionDrinkEvent potionDrinkEvent = new DisallowedPotionDrinkEvent(event.getPlayer(), potion);

            if (!potionDrinkEvent.isAllowed()) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.RED + "This potion is not usable!");
            }
        }
    }

}
