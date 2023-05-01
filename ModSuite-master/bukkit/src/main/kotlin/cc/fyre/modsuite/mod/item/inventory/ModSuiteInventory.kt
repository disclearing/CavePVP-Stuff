package cc.fyre.modsuite.mod.item.inventory

import cc.fyre.modsuite.ModSuite
import cc.fyre.modsuite.mod.ModHandler
import cc.fyre.neutron.Neutron
import net.minecraft.server.v1_7_R4.*
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftHumanEntity
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftInventory
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import java.util.*

class ModSuiteInventory(player: Player) : PlayerInventory((player as CraftPlayer).handle) {

    val displayName = Neutron.getInstance().profileHandler.findDisplayName(this.player.uniqueID)
    private val scoreboardDisplayName = ChatComponentText("Inventory: ${this.displayName}")

    private var online = player.isOnline

    private val extra = arrayOfNulls<ItemStack>(5)
    private val inventory = CraftInventory(this)

    private val viewers = mutableSetOf<UUID>()

    init {
        this.items = this.player.inventory.items
        this.armor = this.player.inventory.armor

        ModHandler.setInventoryByPlayer(player,this)
    }

    fun getInventory():Inventory {
        return this.inventory
    }

    override fun getInventoryName(): String {
        return this.scoreboardDisplayName.g()
    }

    private fun remove() {
        Bukkit.getServer().scheduler.runTaskAsynchronously(ModSuite.instance) {
            (this.owner as CraftPlayer).saveData()
        }

        if (this.transaction.isNotEmpty() || this.online) {
            return
        }

        ModHandler.destroyInventoryByPlayer(((this.owner) as CraftPlayer).player)
    }

    fun onJoin(player: Player) {

        if (this.online) {
            return
        }

        (player as CraftPlayer).also{
            it.handle.inventory.items = this.items
            it.handle.inventory.armor = this.armor
        }

        this.online = true

        Bukkit.getServer().scheduler.runTaskAsynchronously(ModSuite.instance) {
            (this.owner as CraftPlayer).saveData()
        }
    }

    fun onQuit() {
        this.online = false
        this.remove()
    }

    fun isViewer(uuid: UUID): Boolean {
        return this.viewers.contains(uuid)
    }

    fun addViewer(viewer: Player) {

        if (this.viewers.contains(viewer.uniqueId)) {
            return
        }

        this.viewers.add(viewer.uniqueId)
        viewer.openInventory(this.inventory)
    }

    override fun onClose(entity: CraftHumanEntity) {
        super.onClose(entity)

        this.viewers.remove(entity.uniqueId)
        this.remove()
    }


    override fun getContents(): Array<ItemStack?> {

        val contents = arrayOfNulls<ItemStack>(this.size)

        System.arraycopy(this.items,0,contents,0,this.items.size)
        System.arraycopy(this.items,0,contents,this.items.size,this.armor.size)

        /*
        val potions = this.player.bukkitEntity.inventory.contents.sumOf {

            if (it == null || it.type != Material.POTION || it.durability != 16421.toShort()) {
                return@sumOf 0
            }

            return@sumOf it.amount
        }.coerceAtLeast(1)

        contents[44] = CraftItemStack.asNMSCopy(ItemBuilder.of(Material.POTION)
            .amount(potions)
            .data(16421)
            .build()
        )*/

        return contents
    }

    override fun getSize(): Int {
        return super.getSize() + 5
    }

    override fun getItem(i: Int): ItemStack? {
        var index = i
        var toReturn = this.items

        if (index >= toReturn.size) {
            index -= toReturn.size
            toReturn = this.armor
        } else {
            index = this.getReversedItemSlotNum(index)
        }
        if (index >= toReturn.size) {
            index -= toReturn.size
            toReturn = this.extra
        } else if (toReturn.contentEquals(this.armor)) {
            index = this.getReversedArmorSlotNum(index)
        }

        return toReturn[index]
    }

    override fun splitStack(i: Int, j: Int): ItemStack? {

        var index = i
        var toReturn = this.items

        if (index >= toReturn.size) {
            index -= toReturn.size
            toReturn = this.armor
        } else {
            index = this.getReversedItemSlotNum(index)
        }
        if (index >= toReturn.size) {
            index -= toReturn.size
            toReturn = this.extra
        } else if (toReturn.contentEquals(this.armor)) {
            index = this.getReversedArmorSlotNum(index)
        }

        return if (toReturn[index] != null) {
            val itemstack: ItemStack?
            if (toReturn[index]!!.count <= j) {
                itemstack = toReturn[index]
                toReturn[index] = null
                itemstack
            } else {
                itemstack = toReturn[index]!!.cloneItemStack()
                itemstack.count = itemstack.count - 1
                if (toReturn[index]!!.count == 0) {
                    toReturn[index] = null
                }
                itemstack
            }
        } else {
            null
        }
    }

    override fun splitWithoutUpdate(i: Int): ItemStack? {
        var index = i
        var toReturn = this.items
        if (index >= toReturn.size) {
            index -= toReturn.size
            toReturn = this.armor
        } else {
            index = this.getReversedItemSlotNum(index)
        }
        if (index >= toReturn.size) {
            index -= toReturn.size
            toReturn = this.extra
        } else if (toReturn.contentEquals(this.armor)) {
            index = this.getReversedArmorSlotNum(index)
        }
        return if (toReturn[index] != null) {
            val itemstack = toReturn[index]
            toReturn[index] = null
            itemstack
        } else {
            null
        }
    }

    override fun setItem(i: Int, stack: ItemStack?) {
        var index = i
        var itemstack = stack
        var toReturn = this.items
        if (index >= toReturn.size) {
            index -= toReturn.size
            toReturn = this.armor
        } else {
            index = this.getReversedItemSlotNum(index)
        }
        if (index >= toReturn.size) {
            index -= toReturn.size
            toReturn = this.extra
        } else if (toReturn.contentEquals(this.armor)) {
            index = this.getReversedArmorSlotNum(index)
        }
        if (toReturn.contentEquals(this.extra)) {
            (this.owner as CraftPlayer).handle.drop(itemstack, true)
            itemstack = null
        }
        toReturn[index] = itemstack

        (this.owner as CraftPlayer).handle.defaultContainer.b()
    }

    private fun getReversedItemSlotNum(i: Int): Int {
        return if (i >= 27) i - 27 else i + 9
    }

    private fun getReversedArmorSlotNum(i: Int): Int {
        return when (i) {
            0 -> 3
            1 -> 2
            2 -> 1
            3 -> 0
            else -> i
        }
    }

    override fun a(entityhuman: EntityHuman): Boolean {
        return true
    }

}