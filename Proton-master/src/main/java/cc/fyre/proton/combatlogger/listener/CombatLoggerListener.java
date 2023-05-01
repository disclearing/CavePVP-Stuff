package cc.fyre.proton.combatlogger.listener;

import cc.fyre.proton.Proton;
import cc.fyre.proton.combatlogger.CombatLogger;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.MinecraftServer;
import net.minecraft.server.v1_7_R4.PlayerInteractManager;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R4.CraftServer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class CombatLoggerListener implements Listener {
    public CombatLoggerListener() {
    }

    @EventHandler(
            priority = EventPriority.HIGHEST
    )
    public void onEntityDeath(EntityDeathEvent event) {

        if (!event.getEntity().hasMetadata(CombatLogger.COMBAT_LOGGER_METADATA)) {
            return;
        }

        final CombatLogger logger = Proton.getInstance().getCombatLoggerHandler().getCombatLoggerMap().get(event.getEntity().getUniqueId());

        if (logger == null) {
            return;
        }

        for (int i = 0; i < logger.getArmor().length; i++) {

            final ItemStack item = logger.getArmor()[i];

            event.getDrops().add(item);
        }

        for (int i = 0; i < logger.getInventory().length; i++) {

            final ItemStack item = logger.getInventory()[i];

            event.getDrops().add(item);
        }

        logger.getEventAdapter().onEntityDeath(logger, event);

        final Player killer = event.getEntity().getKiller();

        Player target = Proton.getInstance().getServer().getPlayer(logger.getPlayerUuid());

        if (target == null) {

            final MinecraftServer server = ((CraftServer)Proton.getInstance().getServer()).getServer();
            final EntityPlayer entity = new EntityPlayer(server, server.getWorldServer(0), new GameProfile(logger.getPlayerUuid(), logger.getPlayerName()), new PlayerInteractManager(server.getWorldServer(0)));

            target = entity.getBukkitEntity();

            if (target != null) {
                target.loadData();
            }
        }

        if (target != null) {
            target.getInventory().clear();
            target.getInventory().setArmorContents(null);
            target.saveData();
        }

        Proton.getInstance().getCombatLoggerHandler().getCombatLoggerMap().remove(event.getEntity().getUniqueId());
        Proton.getInstance().getCombatLoggerHandler().getCombatLoggerMap().remove(logger.getPlayerUuid());
    }

    @EventHandler(
            priority = EventPriority.HIGHEST
    )
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        if (event.getRightClicked().hasMetadata(CombatLogger.COMBAT_LOGGER_METADATA)) {
            event.setCancelled(true);
        }

    }

    @EventHandler(
            priority = EventPriority.HIGHEST
    )
    public void onChunkUnload(ChunkUnloadEvent event) {

        for (Entity entity : event.getChunk().getEntities()) {

            if (entity.hasMetadata(CombatLogger.COMBAT_LOGGER_METADATA) && !entity.isDead()) {
                event.setCancelled(true);
            }

        }
    }

    @EventHandler(
            priority = EventPriority.HIGHEST
    )
    public void onEntityPortal(EntityPortalEvent event) {
        if (event.getEntity().hasMetadata(CombatLogger.COMBAT_LOGGER_METADATA)) {
            event.setCancelled(true);
        }

    }

    @EventHandler(
            priority = EventPriority.HIGHEST
    )
    public void onPlayerJoin(PlayerJoinEvent event) {

        final CombatLogger logger = Proton.getInstance().getCombatLoggerHandler().getCombatLoggerMap().get(event.getPlayer().getUniqueId());

        if (logger != null && logger.getSpawnedEntity() != null && logger.getSpawnedEntity().isValid() && !logger.getSpawnedEntity().isDead()) {

            final UUID entityId = logger.getSpawnedEntity().getUniqueId();

            logger.getSpawnedEntity().remove();
            Proton.getInstance().getCombatLoggerHandler().getCombatLoggerMap().remove(entityId);
            Proton.getInstance().getCombatLoggerHandler().getCombatLoggerMap().remove(event.getPlayer().getUniqueId());
        }

    }

    @EventHandler(
            priority = EventPriority.HIGHEST
    )
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity().hasMetadata(CombatLogger.COMBAT_LOGGER_METADATA)) {

            final CombatLogger logger = Proton.getInstance().getCombatLoggerHandler().getCombatLoggerMap().get(event.getEntity().getUniqueId());

            if (logger != null) {
                logger.getEventAdapter().onEntityDamageByEntity(logger, event);
            }
        }
    }

    @EventHandler(
            priority = EventPriority.HIGHEST
    )
    public void onEntityPressurePlate(EntityInteractEvent event) {
        boolean pressurePlate = event.getBlock().getType() == Material.STONE_PLATE || event.getBlock().getType() == Material.GOLD_PLATE || event.getBlock().getType() == Material.IRON_PLATE || event.getBlock().getType() == Material.WOOD_PLATE;
        if (pressurePlate && event.getEntity().hasMetadata(CombatLogger.COMBAT_LOGGER_METADATA)) {
            event.setCancelled(true);
        }

    }

}
