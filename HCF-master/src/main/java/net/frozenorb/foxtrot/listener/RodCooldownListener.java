package net.frozenorb.foxtrot.listener;

import cc.fyre.proton.scoreboard.construct.ScoreFunction;
import cc.fyre.proton.util.TimeUtils;
import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.gameplay.pvpclasses.PvPClassHandler;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class RodCooldownListener implements Listener {
    public static Map<UUID, Long> cache = new HashMap<>();

//    @EventHandler
//    public void onLaunch(ProjectileLaunchEvent event){
//        final Projectile hook = event.getEntity();
//
//        if (event.getEntityType().equals(EntityType.FISHING_HOOK)){
//            hook.setVelocity(hook.getVelocity().multiply(1.2));
//        }
//    }

//    @EventHandler
//    private void onAttack(EntityDamageByEntityEvent event) {
//        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof FishHook)) {
//            return;
//        }
//
//        final Player player = (Player) event.getEntity();
//
//        Foxtrot.getInstance().getServer().getScheduler().runTaskLater(Foxtrot.getInstance(), () -> {
//            System.out.println("Current " + player.getVelocity().getX() + " + " + player.getVelocity().getY() + " " + player.getVelocity().getZ());
//
//            player.setVelocity(player.getVelocity().multiply(0.5));
//            System.out.println("Current2 " + player.getVelocity().getX() + " + " + player.getVelocity().getY() + " " + player.getVelocity().getZ());
//        }, 1);
//    }

    @EventHandler(priority = EventPriority.LOW)
    private void onFish(PlayerFishEvent event) {
        final Player player = event.getPlayer();

        if (player.getWorld().getEnvironment() == World.Environment.NORMAL && !Foxtrot.getInstance().getServerHandler().isAu() || event.getState() != PlayerFishEvent.State.FISHING) {
            return;
        }

        if (PvPClassHandler.getPvPClass(player) != null) {
            return;
        }

        if (cache.containsKey(player.getUniqueId()) && System.currentTimeMillis() >= cache.get(player.getUniqueId()) || !cache.containsKey(player.getUniqueId())) {
            cache.remove(player.getUniqueId());
            cache.put(player.getUniqueId(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(1) + TimeUnit.MILLISECONDS.toMillis(50));
            return;
        }

        long millisRemaining = cache.get(player.getUniqueId()) - System.currentTimeMillis();
        double value = (millisRemaining / 1000D);
        double sec = value > 0.1 ? Math.round(10.0 * value) / 10.0 : 0.1;

        event.setCancelled(true);
        player.sendMessage(ChatColor.RED + "You may not use a fishing rod for another " + ChatColor.BOLD + sec + ChatColor.RED + " seconds!");
    }
}
