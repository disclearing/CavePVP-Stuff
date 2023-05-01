package net.frozenorb.foxtrot.gameplay.grounds.listener;

import net.frozenorb.foxtrot.Foxtrot;
import net.frozenorb.foxtrot.util.CC;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

public class GroundsListener implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    private void onInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final Block block = event.getClickedBlock();

        if (block == null || block.getType() != Material.MOB_SPAWNER || !block.hasMetadata("GROUNDS")) {
            return;
        }

        int value = block.hasMetadata("GROUNDS_COUNT") ? (block.getMetadata("GROUNDS_COUNT").get(0).asInt() + 1) : 1;

        block.setMetadata("GROUNDS_COUNT", new FixedMetadataValue(Foxtrot.getInstance(), value));

        if (value != 100) {
            player.sendMessage(CC.translate("&cYou have to hit the spawner " + (100 - value) + " more time" + (100 - value == 1 ? "" : "s") + "!"));
            return;
        }

        CreatureSpawner spawner = (CreatureSpawner) event.getClickedBlock().getState();

        block.getWorld().playEffect(block.getLocation(), Effect.STEP_SOUND, block.getTypeId());
        block.setType(Material.AIR);
        block.removeMetadata("GROUNDS_COUNT", Foxtrot.getInstance());
        block.removeMetadata("GROUNDS", Foxtrot.getInstance());
        block.getState().update();

        ItemStack drop = new ItemStack(Material.MOB_SPAWNER);
        ItemMeta meta = drop.getItemMeta();

        meta.setDisplayName(ChatColor.RESET + StringUtils.capitaliseAllWords(spawner.getSpawnedType().toString().toLowerCase().replaceAll("_", " ")) + " Spawner");
        drop.setItemMeta(meta);

        block.getWorld().dropItemNaturally(block.getLocation().add(0, 0.5, 0), drop);
        block.getWorld().playSound(block.getLocation(), Sound.ANVIL_USE, 1.0F, 1.0F);
        block.getWorld().playEffect(block.getLocation(), Effect.EXPLOSION_HUGE, 5);
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onDamage(EntityDamageByEntityEvent event) {
        Monster actualFuckingDamager = null;

        if (event.getDamager() instanceof Player) {
            return;
        }

        if (event.getDamager() instanceof Monster) {
            actualFuckingDamager = (Monster) event.getDamager();
        }

        if (event.getDamager() instanceof Projectile) {
            if (((Projectile) event.getDamager()).getShooter() instanceof Monster) {
                actualFuckingDamager = (Monster) ((Projectile) event.getDamager()).getShooter();
            }
        }

        if (actualFuckingDamager == null) {
            return;
        }

        if (actualFuckingDamager.hasMetadata("GUARD")) {
            event.setDamage(event.getDamage()*2.5);
        }
    }

}
