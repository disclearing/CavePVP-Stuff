package cc.fyre.modsuite.freeze.listener

import cc.fyre.modsuite.ModSuite
import cc.fyre.modsuite.freeze.FreezeHandler
import cc.fyre.modsuite.mod.ModHandler
import cc.fyre.universe.UniverseAPI
import mkremins.fanciful.FancyMessage
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.*
import org.bukkit.potion.PotionEffectType

object FreezeListener : Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    private fun onPlayerMove(event: PlayerMoveEvent) {

        if (event.from.x == event.to.x && event.from.z == event.to.z) {
            return
        }

        if (!FreezeHandler.isFrozen(event.player)) {
            return
        }

        event.isCancelled = true
        event.player.teleport(event.from)
        event.player.velocity = FreezeHandler.VELOCITY
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private fun onPlayerCommand(event: PlayerCommandPreprocessEvent) {

        if (!FreezeHandler.isFrozen(event.player)) {
            return
        }

        event.isCancelled = true
        event.player.sendMessage("${ChatColor.RED}${ChatColor.BOLD}You cannot execute commands whilst frozen!")
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private fun onEntityDamage(event: EntityDamageByEntityEvent) {

        if (event.entity !is Player || event.damager !is Player) {
            return
        }

        val player = event.entity as Player
        val damager = event.damager as Player

        if (FreezeHandler.isFrozen(player)) {
            event.isCancelled = true
            damager.sendMessage("${player.displayName}${ChatColor.RED} is currently frozen.")
            return
        }

        if (!FreezeHandler.isFrozen(damager)) {
            return
        }

        event.isCancelled = true
        damager.sendMessage(FreezeHandler.FROZEN_MESSAGE)
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private fun onInventoryClick(event: InventoryClickEvent) {

        if (event.whoClicked !is Player || !FreezeHandler.isFrozen(event.whoClicked as Player)) {
            return
        }

        event.isCancelled = true
        (event.whoClicked as Player).sendMessage(FreezeHandler.FROZEN_MESSAGE)
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private fun onPlayerDropItem(event: PlayerDropItemEvent) {

        if (!FreezeHandler.isFrozen(event.player)) {
            return
        }

        event.isCancelled = true
        event.player.sendMessage(FreezeHandler.FROZEN_MESSAGE)
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private fun onPlayerInteract(event: PlayerInteractEvent) {

        if (!FreezeHandler.isFrozen(event.player)) {
            return
        }

        event.isCancelled = true
        event.player.sendMessage(FreezeHandler.FROZEN_MESSAGE)
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private fun onBlockBreak(event: BlockBreakEvent) {

        if (!FreezeHandler.isFrozen(event.player)) {
            return
        }

        event.isCancelled = true
        event.player.sendMessage(FreezeHandler.FROZEN_MESSAGE)
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private fun onBlockPlace(event: BlockPlaceEvent) {

        if (!FreezeHandler.isFrozen(event.player)) {
            return
        }

        event.isCancelled = true
        event.player.sendMessage(FreezeHandler.FROZEN_MESSAGE)
    }

    @EventHandler(priority = EventPriority.NORMAL)
    private fun onPlayerJoin(event: PlayerJoinEvent) {

        if (!FreezeHandler.isFrozen(event.player)) {
            return
        }

        FreezeHandler.setFrozen(event.player,true)
    }

    @EventHandler(priority = EventPriority.NORMAL)
    private fun onPlayerQuit(event: PlayerQuitEvent) {

        if (!FreezeHandler.isFrozen(event.player)) {
            return
        }

        event.player.walkSpeed = 0.2F

        val effects = FreezeHandler.get(event.player) ?: listOf()

        if (effects.isNotEmpty()) {
            event.player.removePotionEffect(PotionEffectType.JUMP)
            event.player.addPotionEffects(effects)
        }

        val message = FancyMessage()
        val displayName = event.player.displayName

        message.then("\n")
        message.then("${displayName}${ChatColor.DARK_RED}${ChatColor.BOLD} has logged out whilst frozen ")
        message.then("on ${UniverseAPI.getServerName()}${ChatColor.DARK_RED}.")
        message.then("\n")
        message.command("ban ${event.player.uniqueId} perm Logged out whilst frozen.")
        message.tooltip("${ChatColor.YELLOW}Click to ban ${displayName}${ChatColor.YELLOW}.")

        ModHandler.getAllOnlineStaffMembers().forEach{message.send(it)}
        /*
        Bukkit.getServer().scheduler.runTaskAsynchronously(ModSuite.instance) {
            ServerModule.sendMessageToNetwork(message,FreezeCommand.PERMISSION,false)
        }*/

        ModSuite.instance.logger.info("${event.player.name} has logged out whilst frozen.")
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private fun onAsyncPlayerChat(event: AsyncPlayerChatEvent) {

        if (!FreezeHandler.isFrozen(event.player)) {
            return
        }

        event.isCancelled = true

        val message = "${ChatColor.RED}${ChatColor.BOLD}[Frozen] ${event.player.displayName}${ChatColor.YELLOW}: ${event.message}"

        ModHandler.getAllOnlineStaffMembers().also{event.player}.forEach{
            it.sendMessage(message)
        }

    }

}