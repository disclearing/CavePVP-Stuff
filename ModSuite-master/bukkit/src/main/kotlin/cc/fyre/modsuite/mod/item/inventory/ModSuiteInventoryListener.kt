package cc.fyre.modsuite.mod.item.inventory

import cc.fyre.modsuite.mod.ModHandler
import cc.fyre.modsuite.mod.command.InvseeCommand
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.PlayerInventory

object ModSuiteInventoryListener : Listener {

    @EventHandler(priority = EventPriority.NORMAL)
    fun onPlayerJoin(event: PlayerJoinEvent) {
        ModHandler.destroyInventoryByPlayer(event.player)?.onJoin(event.player)
    }

    @EventHandler(priority = EventPriority.NORMAL)
    private fun onPlayerQuit(event: PlayerQuitEvent) {
        ModHandler.destroyInventoryByPlayer(event.player)?.onQuit()
    }

    @EventHandler(priority = EventPriority.NORMAL)
    private fun onInventoryClick(event: InventoryClickEvent) {

        if (event.inventory !is PlayerInventory || event.inventory.holder !is Player) {
            return
        }

        val inventory = ModHandler.getInventoryByPlayer(event.inventory.holder as Player)

        if (inventory == null || !inventory.isViewer(event.whoClicked.uniqueId)) {
            return
        }

        if (event.whoClicked.hasPermission("${InvseeCommand.PERMISSION}.advanced")) {
            return
        }

        event.isCancelled = true
    }

}