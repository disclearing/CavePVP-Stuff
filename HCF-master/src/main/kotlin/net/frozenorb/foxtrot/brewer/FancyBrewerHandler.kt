package net.frozenorb.foxtrot.brewer

import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object FancyBrewerHandler {

    private val viewers = hashMapOf<UUID,FancyBrewer>()

    private val brewers = hashMapOf<UUID,FancyBrewer>()
    private val brewersByLocation = hashMapOf<Location,FancyBrewer>()

    private val updateQueue = ConcurrentHashMap.newKeySet<UUID>()
    private val deleteQueue = ConcurrentHashMap.newKeySet<UUID>()

    fun getAllBrewers():List<FancyBrewer> {
        return this.brewersByLocation.values.toList()
    }

    fun addBrewer(brewer: FancyBrewer) {
        brewer.init()

        if (!brewer.initialized) {
            return
        }

        this.brewers[brewer.id] = brewer
        this.brewersByLocation[brewer.location] = brewer
    }

    fun removeBrewer(location: Location):FancyBrewer? {

        val brewer = this.brewersByLocation.remove(location) ?: return null

        this.brewers.remove(brewer.id)
        this.updateQueue.remove(brewer.id)
        this.deleteQueue.add(brewer.id)

        return brewer
    }

    fun getBrewerById(id: UUID):FancyBrewer? {
        return this.brewers[id]
    }

    fun getBrewerByLocation(location: Location):FancyBrewer? {
        return this.brewersByLocation[location.apply{
            this.yaw = 0.0F
            this.pitch = 0.0F
        }]
    }

    fun getBrewerByViewer(player: Player):FancyBrewer? {
        return this.viewers[player.uniqueId]
    }

    fun setBrewerByViewer(player: Player,brewer: FancyBrewer?) {

        if (brewer == null) {
            this.viewers.remove(player.uniqueId)
        } else {
            this.viewers[player.uniqueId] = brewer
        }

    }

    fun getAllUpdates():ConcurrentHashMap.KeySetView<UUID,Boolean> {
        return this.updateQueue
    }

    fun getAllDeletes():ConcurrentHashMap.KeySetView<UUID,Boolean> {
        return this.deleteQueue
    }

    fun createItemStack(amount: Int):ItemStack {
        return ITEM.clone().apply{this.amount = amount}
    }

    fun isBrewer(item: ItemStack):Boolean {
        return item.type == ITEM.type
                && item.hasItemMeta()
                && item.itemMeta.displayName == ITEM.itemMeta.displayName
                && item.itemMeta.lore.size == ITEM.itemMeta.lore.size
    }

    fun addUpdate(brewer: FancyBrewer) {
        this.updateQueue.add(brewer.id)
    }

    private val ITEM = ItemStack(Material.BREWING_STAND_ITEM)
        .apply{

            val itemMeta = this.itemMeta

            itemMeta.displayName = "${ChatColor.AQUA}${ChatColor.BOLD}Fancy Brewer"
            itemMeta.lore = listOf(
                "${ChatColor.GRAY}A brewing stand... but fancy.",
                "${ChatColor.GRAY}Place the brewer and open to get started.",
                "",
                "${ChatColor.GREEN}Learn more at cavepvp.org/fancybrewers"
            )

            this.itemMeta = itemMeta
        }

    const val BREWER_DROP_METADATA  = "FANCY_BREWER_DROP"
}