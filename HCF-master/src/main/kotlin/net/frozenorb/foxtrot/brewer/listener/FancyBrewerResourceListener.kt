package net.frozenorb.foxtrot.brewer.listener

import net.frozenorb.foxtrot.Foxtrot
import net.frozenorb.foxtrot.brewer.FancyBrewer
import net.frozenorb.foxtrot.brewer.FancyBrewerHandler
import net.frozenorb.foxtrot.brewer.FancyBrewerResource
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.BrewingStand
import org.bukkit.block.Hopper
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.inventory.InventoryMoveItemEvent
import org.bukkit.inventory.ItemStack

object FancyBrewerResourceListener : Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    private fun onInventoryClick(event: InventoryClickEvent) {

        val player = event.whoClicked as? Player ?: return

        if (event.clickedInventory == null) {
            return
        }

        val brewer = FancyBrewerHandler.getBrewerByViewer(player) ?: return
        val inventory = brewer.inventory

        val cursor = if (event.cursor != null) {
            if (event.cursor.type == Material.AIR) null else event.cursor
        } else {
            null
        }

        val current = if (event.currentItem != null) {
            if (event.currentItem.type == Material.AIR) null else event.currentItem
        } else {
            null
        }

        if (event.clickedInventory == inventory) {
            when (event.action) {
                InventoryAction.HOTBAR_SWAP -> {

                    val item = player.inventory.getItem(event.hotbarButton) ?: return

                    if (brewer.isAllowedToInsert(event.slot,item)) {
                        return
                    }

                    event.isCancelled = true
                }
                InventoryAction.SWAP_WITH_CURSOR -> {

                    if (brewer.isAllowedToInsert(event.slot,cursor!!)) {
                        return
                    }

                    event.isCancelled = true
                }
                InventoryAction.PLACE_ONE,InventoryAction.PLACE_SOME,InventoryAction.PLACE_ALL -> {

                    if (cursor == null) {
                        return
                    }

                    if (brewer.isAllowedToInsert(event.slot,cursor)) {
                        return
                    }

                    event.isCancelled = true
                }
                else -> {}
            }

            return
        }

        if (event.action != InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            return
        }

        if (current == null) {
            return
        }

        if (current.type == Material.GLASS_BOTTLE) {

            val available = FancyBrewer.MAX_BOTTLES - brewer.getAllBottles()

            if (available > 0) {

                var amount = current.amount

                if (amount > available) {
                    amount = available
                }

                brewer.addBottles(amount)

                Bukkit.getServer().scheduler.runTask(Foxtrot.instance) {
                    player.openInventory.bottomInventory.setItem(event.slot,if (current.amount - amount == 0) {
                        null
                    } else {
                        current.apply {this.amount = (this.amount - amount)}
                    })
                }

            }

            event.isCancelled = true
            return
        }

        val resource = FancyBrewerResource.getResourceByType(current.type)

        if (resource == null) {
            event.isCancelled = true
            return
        }

        val value = brewer.getResourceByType(resource)

        if (value != null) {

            if (value.amount == value.maxStackSize) {
                event.isCancelled = true
                return
            }

            val leftover = (current.amount + value.amount) - value.maxStackSize

            if (leftover <= 0) {
                return
            }

            event.isCancelled = true
            brewer.setResource(resource,value.apply{this.amount = this.maxStackSize})

            Bukkit.getServer().scheduler.runTask(Foxtrot.instance) {
                player.openInventory.bottomInventory.setItem(event.slot, current.apply {
                    this.amount = leftover
                })
            }
            return
        }

        Bukkit.getServer().scheduler.runTask(Foxtrot.instance) {
            player.openInventory.topInventory.setItem(resource.getSlot(), current)
            player.openInventory.bottomInventory.setItem(event.slot, null)
        }

        event.isCancelled = true
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private fun onInventoryMove(event: InventoryMoveItemEvent) {

        val hopper = event.source.holder

        if (hopper !is Hopper) {
            return
        }

        val stand = event.destination.holder

        if (stand !is BrewingStand) {
            return
        }

        val brewer = FancyBrewerHandler.getBrewerByLocation(stand.location)

        if (brewer == null || brewer.hopper == hopper) {
            return
        }

        event.isCancelled = true
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private fun onInventoryDrag(event: InventoryDragEvent) {

        val player = event.whoClicked as? Player ?: return

        if (event.inventory == null) {
            return
        }

        val brewer = FancyBrewerHandler.getBrewerByViewer(player) ?: return

        event.isCancelled = true
    }
}