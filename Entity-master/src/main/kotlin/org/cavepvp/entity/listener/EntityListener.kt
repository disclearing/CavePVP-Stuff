package org.cavepvp.entity.listener

import org.cavepvp.entity.util.VectorUtil
import org.cavepvp.entity.Entity
import org.cavepvp.entity.EntityHandler
import org.cavepvp.entity.EntityVisibility
import org.cavepvp.entity.type.npc.NPC
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.ItemStack
import org.cavepvp.entity.EntityPlugin

object EntityListener : Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    private fun onPlayerQuit(event: PlayerQuitEvent) {
        EntityHandler.getAllEntities().filter{it.viewers.contains(event.player.uniqueId)}.forEach{
            it.viewers.remove(event.player.uniqueId)
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    private fun onPlayerInteractEvent(event: PlayerInteractEvent) {

        if (event.player.itemInHand == null || !(event.action == Action.RIGHT_CLICK_BLOCK || event.action == Action.RIGHT_CLICK_AIR)) {
            return
        }

        if (!this.isMoveTool(event.player.itemInHand)) {
            return
        }

        val entity = EntityHandler.getEntityById(event.player.itemInHand.itemMeta.lore[0]!!.replace(Entity.MOVE_ITEM_LORE_KEY,"").toInt()) ?: return

        val location = if (event.action == Action.RIGHT_CLICK_BLOCK) {
            event.clickedBlock.location.clone().add(0.5,Entity.BLOCK_HEIGHTS.getOrDefault(event.clickedBlock.type,1.0),0.5).also{

                if (entity is NPC) {
                    it.yaw = VectorUtil.convertYawFromVectors(event.player.location.toVector(),it.toVector())
                    it.pitch = it.clone().setDirection(event.player.location.clone().subtract(it.clone()).toVector()).pitch
                }

            }
        } else {
            event.player.location.clone()
        }

        entity.updateLocation(location)
        entity.updateVisibility(EntityVisibility.VISIBLE)

        event.player.inventory.itemInHand = null
        event.player.updateInventory()
    }

    @EventHandler(priority = EventPriority.NORMAL)
    private fun onInventoryClickEvent(event: InventoryClickEvent) {

        if (event.action.name.contains("DROP") && isMoveTool(event.currentItem)) {
            event.isCancelled = true
        }

    }

    @EventHandler(priority = EventPriority.NORMAL)
    private fun onInventoryDragEvent(event: InventoryDragEvent) {

        if (event.cursor == null || !this.isMoveTool(event.cursor)) {
            return
        }

        event.isCancelled = true
    }

    @EventHandler(priority = EventPriority.NORMAL)
    private fun onPlayerDeathEvent(event: PlayerDeathEvent) {
        event.drops.removeIf{this.isMoveTool(it)}
    }

    @EventHandler(priority = EventPriority.NORMAL)
    private fun onPlayerDropItem(event: PlayerDropItemEvent) {

        if (!this.isMoveTool(event.itemDrop.itemStack)) {
            return
        }

        event.isCancelled = true

        Bukkit.getServer().scheduler.runTaskLater(EntityPlugin.instance,{

            if (!event.player.isOnline) {
                return@runTaskLater
            }

            this.clearInventoryOfTool(event.player)
        },1L)

    }

    @EventHandler(priority = EventPriority.NORMAL)
    private fun onPlayerQuitEvent(event: PlayerQuitEvent) {
        this.clearInventoryOfTool(event.player)
    }

    private fun isMoveTool(item: ItemStack?):Boolean {
        return item != null
                && item.type == Entity.MOVE_ITEM_TYPE
                && item.hasItemMeta()
                && (item.itemMeta.hasDisplayName() && item.itemMeta.hasLore())
                && item.itemMeta.displayName == Entity.MOVE_ITEM_NAME
                && item.itemMeta.lore[0].startsWith(Entity.MOVE_ITEM_LORE_KEY)
    }

    private fun clearInventoryOfTool(player: Player) {
        player.inventory.contents.withIndex().filter{this.isMoveTool(it.value)}.forEach{

            val item = it.value
            val entity = EntityHandler.getEntityById(item.itemMeta.lore[0]!!.replace(Entity.MOVE_ITEM_LORE_KEY,"").toInt()) ?: return@forEach

            player.inventory.setItem(it.index,null)
            player.inventory.contents[it.index] = null
            player.updateInventory()

            entity.updateVisibility(EntityVisibility.VISIBLE)
        }
    }

}