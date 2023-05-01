package org.cavepvp.entity.util

import cc.fyre.proton.menu.Button
import cc.fyre.proton.menu.Menu
import cc.fyre.proton.menu.menus.ConfirmMenu
import cc.fyre.proton.util.Callback
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import java.util.*
import java.util.function.Predicate
import kotlin.math.max

abstract class ListButton<T>(private val menu: Menu, private val length: Int, private val characterLimit: Int) : Button() {

    init {
        this.menu.isUpdateAfterClick = true
    }

    private var list = mutableListOf<T>()

    protected var index = 0
    protected var selectedItem: T? = null

    abstract fun getList():List<T>

    abstract fun addItem(item: T,player: Player):Runnable
    abstract fun removeItem(item: T,player: Player):Runnable

    abstract fun isModifiable():Boolean

    abstract fun startAddConversation(player: Player,callback: Callback<T>)

    abstract fun getTextFormat(item: T):Pair<String,String>

    abstract fun getItemStack(player: Player):ItemStack
    abstract fun getEmptyDescription(player: Player):List<String>

    open fun getExtraDescription(player: Player):List<String> {
        return emptyList()
    }

    override fun getName(p0: Player?): String {
        return ""
    }

    override fun getDescription(p0: Player?): MutableList<String> {
        return Collections.emptyList()
    }

    override fun getMaterial(p0: Player?): Material {
        return Material.AIR
    }

    override fun getButtonItem(player: Player): ItemStack {
        this.list = this.getList().toMutableList()

        if (this.index > this.list.lastIndex) {
            this.index = this.list.lastIndex
        }

        this.selectedItem = if (this.list.isEmpty()) null else this.list[this.index]

        val lore = mutableListOf<String>()

        lore.add(" ")
        lore.addAll(if (this.list.isEmpty()) this.getEmptyDescription(player) else this.getDescription(player))

        if (this.list.isNotEmpty()) {

            val startIndex = if (this.index < (this.length / 2)) 0 else this.index - (this.length / 2)
            val (looping,leftover) = this.list.withIndex().partition{it.index in (startIndex until (this.index + (this.length / 2) + 1))}

            val (top,bottom) = leftover.partition{it.index < looping.first().index}

            if (top.isNotEmpty()) {
                lore.add("${ChatColor.GRAY}${looping.first().index - 1}. ${ChatColor.WHITE}${top.size} more..")
            }

            looping.forEach{

                val format = this.getTextFormat(it.value)
                var text = format.first
                val exceededLimit = text.length > this.characterLimit

                if (exceededLimit) {
                    text = text.substring(0,this.characterLimit)
                }

                lore.add("${if (this.index == it.index) ChatColor.GREEN else ChatColor.GRAY}${it.index}. ${ChatColor.WHITE}$text${ChatColor.WHITE}${if (exceededLimit) ".." else ""} ${format.second}")
            }

            if (bottom.isNotEmpty()) {
                lore.add("${ChatColor.GRAY}${looping.last().index + 1}. ${ChatColor.WHITE}${bottom.size} more..")
            }

        }

        lore.add(" ")

        if (this.isModifiable()) {
            lore.add("${ChatColor.GREEN}Shift Left click to add a item")
            lore.add("${ChatColor.RED}Shift Right click to delete a item")
        }

        lore.add("${ChatColor.YELLOW}Left and right click to scroll")
        lore.addAll(this.getExtraDescription(player))

        return ItemBuilder.copyOf(this.getItemStack(player))
            .lore(lore)
            .build()
    }

    override fun clicked(player: Player, slot: Int, clickType: ClickType) {

        if (this.index > this.list.lastIndex) {
            this.setIndex(this.list.lastIndex)
        }

        if (clickType.isShiftClick) {

            if (!this.isModifiable()) {
                return
            }

            if (clickType.isLeftClick) {
                this.startAddConversation(player) { callback ->
                    this@ListButton.addItem(callback, player)
                    this@ListButton.menu.openMenu(player)
                }
                return
            }

            object : ConfirmMenu("Delete?", Callback{value ->
                if (value) {
                    this@ListButton.removeItem(this@ListButton.list[this@ListButton.index],player)
                    this@ListButton.menu.openMenu(player)
                }

                this@ListButton.menu.openMenu(player)
            }) {}.openMenu(player)
            return
        }

        if (clickType.isLeftClick) {

            if (this.index >= this.list.lastIndex) {
                this.setIndex(0)
            } else {
                this.setIndex(this.index + 1)
            }

        } else if (clickType.isRightClick) {
            this.setIndex(max(this.index - 1,0))
        }

    }

    @JvmName("setIndex1")
    private fun setIndex(index: Int) {
        this.index = index
        this.selectedItem = if (this.list.isEmpty()) null else if (index > this.list.lastIndex) this.list.last() else this.list[this.index]
    }

}