package cc.fyre.modsuite.mod.editor

import cc.fyre.modsuite.mod.ModHandler
import cc.fyre.modsuite.mod.item.ModModeItem
import cc.fyre.modsuite.mod.item.type.CarpetItem
import cc.fyre.proton.util.ItemBuilder
import org.bukkit.Bukkit
import org.bukkit.DyeColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.cavepvp.profiles.Profiles
import org.cavepvp.profiles.playerProfiles.impl.ModLayout

class ModLayoutEditor(player: Player) {

    private var layout = Profiles.getInstance().playerProfileHandler.fromUuid(player.uniqueId)?.get()!!.modLayout
    private var modified = false

    private val inventory = Bukkit.createInventory(player,6*9,TITLE)

    init {
        this.loadPresets(player)
    }

    fun isModified():Boolean {
        return this.modified
    }

    fun getInventory():Inventory {
        return this.inventory
    }

    fun getNextDisabledSlot(layout: ModLayout):Int {
        return DISABLED_SLOTS.first{!layout.disabledItemSlots.values.contains(it)}
    }

    fun getLayout():ModLayout {
        return this.layout
    }

    private fun loadPresets(player: Player) {

        GLASS_SLOTS.forEach{this.inventory.setItem(it, FILLER)}
        CARPET_SLOTS.forEach{this.inventory.setItem(it.key,it.value)}

        val (enabled,disabled) = ModHandler.getAllModModeItems().partition{this.layout.isItemEnabled(it.getKey())}

        for (item in enabled) {
            this.inventory.setItem(layout.getSlotByItem(item.getKey(),item.slot),item.getItemStack(player))
        }

        for (item in disabled.withIndex()) {

            val slot = DISABLED_SLOTS[item.index]

            this.layout.setDisabledItemSlot(item.value.getKey(),slot)

            this.inventory.setItem(slot,item.value.getItemStack(player))
        }
    }

    fun setCarpetColor(player: Player,color: DyeColor) {

        this.layout.carpetColor = color

        val item = CarpetItem.getItemStack(player)

        if (this.layout.isItemEnabled(CarpetItem.getKey())) {
            CarpetItem.getSlot(player).also{slot ->
                this.inventory.setItem(slot,item)
            }

        } else {
            this.inventory.setItem(this.layout.getDisabledItemSlot(CarpetItem.getKey()),item)
        }

        this.modified = true
    }

    fun setItemDisabled(slot: Int,modModeItem: ModModeItem,customSlot: Int? = null) {

        val item = this.inventory.getItem(slot)

        this.layout.setItemEnabled(modModeItem.getKey(),false)

        val newSlot = customSlot ?: this.getNextDisabledSlot(this.layout)

        this.inventory.setItem(slot,null)
        this.inventory.setItem(newSlot,item)

        this.layout.setDisabledItemSlot(modModeItem.getKey(),newSlot)

        this.modified = true
    }

    fun setItemEnabled(modModeItem: ModModeItem, slot: Int) {

        val oldSlot = this.layout.getDisabledItemSlot(modModeItem.getKey()) ?: return
        val item = this.inventory.getItem(oldSlot)

        this.layout.setItemSlot(modModeItem.getKey(),slot)
        this.layout.setItemEnabled(modModeItem.getKey(),true)

        this.inventory.setItem(slot,item)
        this.inventory.setItem(oldSlot,null)

        this.modified = true
    }

    companion object {

        const val TITLE = "Mod Mode Editor"

        val GLASS_SLOTS = IntRange(9,17)
        val CARPET_SLOTS = intArrayOf(
            18,19,20,21,22,23,24,25,26,
            28,29,30,31,32,33,34
        ).withIndex().associate{it.value to ItemBuilder.of(Material.CARPET).data(DyeColor.values()[it.index].woolData.toShort()).build()}

        val DISABLED_SLOTS = IntRange(36,53).plus(arrayOf(27,35)).sortedBy{it}
            .toIntArray()

        private val FILLER = ItemBuilder.of(Material.STAINED_GLASS_PANE)
            .name(" ")
            .data(14)
            .build()
    }

}