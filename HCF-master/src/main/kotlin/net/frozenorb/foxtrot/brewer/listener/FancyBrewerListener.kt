package net.frozenorb.foxtrot.brewer.listener

import net.frozenorb.foxtrot.Foxtrot
import net.frozenorb.foxtrot.brewer.FancyBrewer
import net.frozenorb.foxtrot.brewer.FancyBrewerHandler
import net.frozenorb.foxtrot.brewer.FancyBrewerResource
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.block.BrewingStand
import org.bukkit.block.Hopper
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockDropItemsEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.inventory.InventoryMoveItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.metadata.FixedMetadataValue
import java.util.*

object FancyBrewerListener : Listener {

    @EventHandler(priority = EventPriority.MONITOR,ignoreCancelled = true)
    private fun onBlockPlace(event: BlockPlaceEvent) {

        if (event.itemInHand == null || event.itemInHand.type == Material.AIR) {
            return
        }

        if (event.itemInHand.type == Material.HOPPER) {

            val block = event.block.getRelative(BlockFace.UP)

            if (block == null || block.state !is BrewingStand) {
                return
            }

            Bukkit.getServer().scheduler.runTask(Foxtrot.instance) {
                FancyBrewerHandler.getBrewerByLocation(block.location)?.setHopper(event.block.state as Hopper)
            }
            return
        }

        if (!FancyBrewerHandler.isBrewer(event.itemInHand)) {
            return
        }

        val block = event.blockPlaced.location.clone().apply{
            this.yaw = 0.0F
            this.pitch = 0.0F
        }

        val brewer = FancyBrewer(UUID.randomUUID(),event.player.uniqueId,block)

        event.player.sendMessage(ChatColor.translate("&aYou have placed a Fancy Brewer."))
        event.player.sendMessage(ChatColor.translate("&7Watch this tutorial on how to use a Fancy Brewer &fhttps://cavepvp.org/fancybrewers"))

        FancyBrewerHandler.addBrewer(brewer.also{it.flagForSave()})
    }

    @EventHandler(priority = EventPriority.MONITOR,ignoreCancelled = true)
    private fun onBlockBreak(event: BlockBreakEvent) {

        if (event.block.type == Material.HOPPER) {

            val block = event.block.getRelative(BlockFace.UP)

            if (block == null || block.state !is BrewingStand) {
                return
            }

            FancyBrewerHandler.getBrewerByLocation(block.location)?.setHopper(null)
            return
        }

        if (event.block.state !is BrewingStand) {
            return
        }

        val block = event.block.location.clone().apply{
            this.yaw = 0.0F
            this.pitch = 0.0F
        }

        val brewer = FancyBrewerHandler.removeBrewer(block) ?: return

        event.block.setMetadata(FancyBrewerHandler.BREWER_DROP_METADATA,FixedMetadataValue(Foxtrot.instance,true))

        if (event.player.gameMode != GameMode.CREATIVE) {
            event.block.world.dropItemNaturally(event.block.location,FancyBrewerHandler.createItemStack(1))
        }

        brewer.stand.inventory.clear()
        brewer.inventory.viewers.forEach{FancyBrewerHandler.setBrewerByViewer(it as Player,null)}
        brewer.getAllDrops().filterNotNull().forEach{event.block.world.dropItemNaturally(event.block.location,it)}
    }

    @EventHandler(priority = EventPriority.MONITOR,ignoreCancelled = true)
    private fun onPlayerInteract(event: PlayerInteractEvent) {

        if (event.clickedBlock == null) {
            return
        }

        if (event.action != Action.RIGHT_CLICK_BLOCK) {
            return
        }

        if (event.player.isSneaking
            && event.player.itemInHand != null
            && event.player.itemInHand.type != Material.AIR
            && event.player.itemInHand.type.isBlock
        ) {
            return
        }

        val brewer = FancyBrewerHandler.getBrewerByLocation(event.clickedBlock.location) ?: return

        brewer.openInventory(event.player)
        FancyBrewerHandler.setBrewerByViewer(event.player,brewer)

        event.isCancelled = true
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private fun onInventoryClose(event: InventoryCloseEvent) {
        FancyBrewerHandler.setBrewerByViewer(event.player as Player,null)
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private fun onInventoryClick(event: InventoryClickEvent) {

        val player = event.whoClicked as? Player ?: return

        if (event.clickedInventory == null) {
            return
        }

        val brewer = FancyBrewerHandler.getBrewerByViewer(player) ?: return
        val inventory = brewer.inventory

        if (event.clickedInventory != inventory) {
            return
        }

        if (FancyBrewerResource.getResourceBySlot(event.slot) != null) {
            return
        }

        if (FancyBrewer.GLASS_SLOTS.contains(event.slot)) {
            return
        }

        FancyBrewer.BUTTONS[event.slot]?.handleClick(player, brewer,event.click)

        event.isCancelled = true
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private fun onInventoryMove(event: InventoryMoveItemEvent) {

        val holder = event.source.holder

        if (holder !is BrewingStand) {
            return
        }

        if (FancyBrewerHandler.getBrewerByLocation(holder.location) == null) {
            return
        }

        event.isCancelled = true
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private fun onBlockDropItems(event: BlockDropItemsEvent) {

        if (!event.block.hasMetadata(FancyBrewerHandler.BREWER_DROP_METADATA)) {
            return
        }

        event.toDrop.clear()
    }

}