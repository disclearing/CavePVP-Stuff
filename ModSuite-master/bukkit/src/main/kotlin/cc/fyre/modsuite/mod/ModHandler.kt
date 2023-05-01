package cc.fyre.modsuite.mod

import cc.fyre.modsuite.ModSuite
import cc.fyre.modsuite.mod.item.ModModeItem
import cc.fyre.modsuite.mod.item.inventory.ModSuiteInventory
import cc.fyre.modsuite.mod.item.type.*
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemStack
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object ModHandler {

    private val modMode = mutableMapOf<UUID,ModMode>()
    private val modModeItems = mutableMapOf<String,ModModeItem>()

    private val inventories = ConcurrentHashMap<UUID,ModSuiteInventory>()

    private var onlineStaffMembers = mutableListOf<UUID>()

    private var lastPvPLocation: Location? = null

    init {
        this.register(BookItem)
        this.register(WandItem)
        this.register(CarpetItem)
        this.register(CompassItem)
        this.register(VisibilityItem)
        this.register(OnlineStaffItem)
        this.register(RandomTeleportItem)
        this.register(LastPvPItem)
        this.register(FreezeItem)

    }

    fun loadOrCreateByPlayer(player: Player, enable: Boolean = true):ModMode {

        var toReturn = this.modMode[player.uniqueId]

        if (toReturn == null) {
            toReturn = ModMode(player).also{this.modMode[player.uniqueId] = it}
            toReturn.init(player,enable)
        }

        return toReturn
    }

    fun remove(player: Player):ModMode? {
        return this.modMode.remove(player.uniqueId)
    }

    fun isInVanish(id: UUID):Boolean {

        if (!this.modMode.containsKey(id)) {
            return false
        }

        return this.modMode[id]?.visibility != ModVisibility.VISIBLE
    }

    fun isInModMode(id: UUID):Boolean {
        return this.modMode[id]?.enabled == true
    }

    fun getModModeById(id: UUID):ModMode? {
        return this.modMode[id]
    }

    fun getAllModModes():List<ModMode> {
        return this.modMode.values.toList()
    }

    fun getInventoryByPlayer(player: Player):ModSuiteInventory? {
        return this.inventories[player.uniqueId]
    }

    fun setInventoryByPlayer(player: Player,inventory: ModSuiteInventory) {
        this.inventories[player.uniqueId] = inventory
    }

    fun destroyInventoryByPlayer(player: Player):ModSuiteInventory? {
        return this.inventories.remove(player.uniqueId)
    }

    fun register(item: ModModeItem) {
        this.modModeItems[item.getKey()] = item

        if (item is Listener) {
            Bukkit.getServer().pluginManager.registerEvents(item,ModSuite.instance)
        }

    }

    fun unregister(item: ModModeItem) {

        if (item is Listener) {
            HandlerList.unregisterAll(item)
        }

        this.modModeItems.remove(item.getKey())
    }

    fun getAllModModeItems():List<ModModeItem> {
        return this.modModeItems.values.toList()
    }

    fun getModModeItemByKey(key: String):ModModeItem? {
        return this.modModeItems[key]
    }

    fun getModModeItemByStack(item: ItemStack):ModModeItem? {
        /*TODO
        if (!ItemUtil.hasMetadata(item,ModModeItem.META_KEY)) {
            return null
        }

        return this.modModeItems[ItemUtil.getMetadata<String>(item,ModModeItem.META_KEY)]*/

        if (!item.hasItemMeta() || item.itemMeta.lore == null || item.itemMeta.lore.isEmpty()) {
            return null
        }

        return this.modModeItems[ChatColor.stripColor(item.itemMeta.lore[0])]
    }

    fun getOnlineStaff():List<UUID> {
        return this.onlineStaffMembers
    }

    internal fun setOnlineStaff(list: MutableList<UUID>) {
        this.onlineStaffMembers = list
    }

    fun getAllOnlineStaffMembers():List<Player> {
        return Bukkit.getServer().onlinePlayers.filter{it.hasPermission("neutron.staff")}.toList()
    }

    fun getLastPvPLocation():Location? {
        return this.lastPvPLocation
    }

    fun setLastPvPLocation(location: Location) {
        this.lastPvPLocation = location
    }

    const val VANISH_METADATA = "MODSUITE_VANISHED"
    const val BUNGEE_TP_PACKET = "MODSUITE_BUNGEE_TP_PACKET"

}