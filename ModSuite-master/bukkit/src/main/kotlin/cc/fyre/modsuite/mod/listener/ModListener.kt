package cc.fyre.modsuite.mod.listener

import cc.fyre.modsuite.ModSuite
import cc.fyre.modsuite.mod.ModHandler
import cc.fyre.modsuite.mod.command.ModCommand
import cc.fyre.modsuite.mod.event.PlayerModItemInteractEvent
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.block.Chest
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.*
import org.bukkit.event.hanging.HangingBreakByEntityEvent
import org.bukkit.event.player.*
import org.bukkit.event.vehicle.VehicleDestroyEvent
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent

object ModListener : Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    private fun onPlayerJoin(event: PlayerJoinEvent) {

        if (!ModSuite.instance.config.modModeOnJoin) {
            return
        }

        if (!event.player.hasPermission(ModCommand.PERMISSION)) {
            return
        }

        ModHandler.loadOrCreateByPlayer(event.player,true)
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private fun onPlayerQuit(event: PlayerQuitEvent) {

        val modMode = ModHandler.remove(event.player)

        if (modMode == null || !modMode.enabled) {
            return
        }

        modMode.setModMode(false,event.player,true)
    }

    @EventHandler(priority = EventPriority.LOW)
    private fun onPlayerInteractEntity(event: PlayerInteractEntityEvent) {

        if (!ModHandler.isInModMode(event.player.uniqueId)) {
            return
        }

        val item = ModHandler.getModModeItemByStack(event.player.itemInHand) ?: return

        event.isCancelled = true

        val modItemEvent = PlayerModItemInteractEvent(event.player,item).call()

        if (modItemEvent.isCancelled) {

            if (modItemEvent.cancelledMessage != null) {
                event.player.sendMessage(modItemEvent.cancelledMessage)
            }

            return
        }

        item.handleInteractEntity(event)
    }

    @EventHandler(priority = EventPriority.LOW)
    private fun onPlayerInteract(event: PlayerInteractEvent) {

        if (event.action == Action.LEFT_CLICK_AIR || event.action == Action.LEFT_CLICK_BLOCK) {
            return
        }

        if (!ModHandler.isInModMode(event.player.uniqueId)) {
            return
        }

        if (event.item == null) {
            return
        }

        if (event.clickedBlock != null && !event.clickedBlock.type.isBlock) {
            event.isCancelled = true
        }

        val item = ModHandler.getModModeItemByStack(event.item) ?: return

        val modItemEvent = PlayerModItemInteractEvent(event.player,item).call()

        if (modItemEvent.isCancelled) {

            if (modItemEvent.cancelledMessage != null) {
                event.player.sendMessage(modItemEvent.cancelledMessage)
            }

            event.isCancelled = true
            return
        }

        item.handleInteract(event)
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private fun onBlockBreak(event: BlockBreakEvent) {

        if (!ModHandler.isInModMode(event.player.uniqueId)) {
            return
        }

        event.player.sendMessage("${ChatColor.RED}${ChatColor.BOLD}You cannot break blocks whilst in mod mode!")
        event.isCancelled = true
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private fun onBlockPlace(event: BlockPlaceEvent) {

        if (!ModHandler.isInModMode(event.player.uniqueId)) {
            return
        }

        event.player.sendMessage("${ChatColor.RED}${ChatColor.BOLD}You cannot place blocks whilst in mod mode!")
        event.isCancelled = true
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private fun onEntityDamage(event: EntityDamageEvent) {

        if (event.entity !is Player) {
            return
        }

        if (!ModHandler.isInModMode(event.entity.uniqueId)) {
            return
        }

        event.isCancelled = true
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private fun onEntityDamageByEntity(event: EntityDamageByEntityEvent) {

        if (event.damager !is Player) {
            return
        }

        if (!ModHandler.isInModMode(event.damager.uniqueId)) {
            return
        }

        (event.damager as Player).sendMessage("${ChatColor.RED}${ChatColor.BOLD}You cannot do this whilst in mod mode!")
        event.isCancelled = true
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private fun onDropItem(event: PlayerDropItemEvent) {

        if (!ModHandler.isInModMode(event.player.uniqueId)) {
            return
        }

        if (ModHandler.getModModeItemByStack(event.itemDrop.itemStack) == null) {
            event.itemDrop.remove()
            return
        }

        event.isCancelled = true
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private fun onPickupItem(event: PlayerPickupItemEvent) {

        if (!ModHandler.isInModMode(event.player.uniqueId)) {
            return
        }

        event.isCancelled = true
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private fun onFoodLevelChange(event: FoodLevelChangeEvent) {

        if (!ModHandler.isInModMode(event.entity.uniqueId)) {
            return
        }

        event.isCancelled = true
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private fun onEntityTarget(event: EntityTargetEvent) {

        if (event.target !is Player) {
            return
        }

        if (!ModHandler.isInModMode(event.target.uniqueId)) {
            return
        }

        event.isCancelled = true
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private fun onPlayerDeath(event: PlayerDeathEvent) {

        if (!ModHandler.isInModMode(event.entity.uniqueId)) {
            return
        }

        event.drops.clear()
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private fun onVehicleDestroy(event: VehicleDestroyEvent) {

        if (event.attacker !is Player || !ModHandler.isInModMode(event.attacker.uniqueId)) {
            return
        }

        (event.attacker as Player).sendMessage("${ChatColor.RED}${ChatColor.BOLD}You cannot do this whilst in mod mode!")
        event.isCancelled = true
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private fun onVehicleEntityCollision(event: VehicleEntityCollisionEvent) {

        if (event.entity !is Player || !ModHandler.isInModMode(event.entity.uniqueId)) {
            return
        }

        event.isCancelled = true
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private fun onHangingBreak(event: HangingBreakByEntityEvent) {

        if (event.remover !is Player || !ModHandler.isInModMode(event.remover.uniqueId)) {
            return
        }

        event.isCancelled = true
        (event.remover as Player).sendMessage("${ChatColor.RED}${ChatColor.BOLD}You cannot break blocks whilst in mod mode!")
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private fun onProjectileLaunch(event: ProjectileLaunchEvent) {

        if (event.entity.shooter !is Player) {
            return
        }

        if (!ModHandler.isInModMode(event.entity.uniqueId)) {
            return
        }

        event.isCancelled = true
        (event.entity.shooter as Player).sendMessage("${ChatColor.RED}${ChatColor.BOLD}You cannot do this whilst in mod mode!")
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private fun onEntityDamageByEntity2(event: EntityDamageByEntityEvent) {

        if (event.isCancelled) {
            return
        }

        if (event.entity !is Player) {
            return
        }

        val player = if (event.damager is Player) {
            event.damager as Player
        } else if (event.damager is Projectile && ((event.damager as Projectile).shooter is Player)) {
            (event.damager as Projectile).shooter as Player
        } else {
            null
        } ?: return

        ModHandler.setLastPvPLocation(player.location)
    }

}