package cc.fyre.modsuite.mod.listener

import cc.fyre.modsuite.mod.ModHandler
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Sound
import org.bukkit.block.Chest
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent

object VisibilityListener : Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    private fun onPlayerInteract(event: PlayerInteractEvent) {

        if (event.action != Action.RIGHT_CLICK_BLOCK) {
            return
        }

        if (!ModHandler.isInVanish(event.player.uniqueId)) {
            return
        }

        if (event.player.isSneaking) {
            return
        }

        if (event.clickedBlock == null || event.clickedBlock.state !is Chest) {
            return
        }

        val chest = event.clickedBlock.state as Chest

        val inventory = Bukkit.getServer().createInventory(chest.inventory.holder,chest.inventory.size,chest.inventory.title)

        inventory.contents = chest.inventory.contents

        event.player.openInventory(inventory)
        event.player.sendMessage("${ChatColor.RED}Opening chest silently..")
        event.player.playSound(chest.location,Sound.CHEST_OPEN,1.0F,1.0F)
        event.isCancelled = true
    }

}