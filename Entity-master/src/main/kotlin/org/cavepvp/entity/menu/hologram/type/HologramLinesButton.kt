package org.cavepvp.entity.menu.hologram.type

import cc.fyre.proton.menu.Menu
import cc.fyre.proton.util.Callback
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.conversations.ConversationContext
import org.bukkit.conversations.Prompt
import org.bukkit.conversations.StringPrompt
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import org.cavepvp.entity.menu.hologram.HologramMenu
import org.cavepvp.entity.type.hologram.Hologram
import org.cavepvp.entity.type.hologram.line.HologramLine
import org.cavepvp.entity.type.hologram.line.type.HologramItemLine
import org.cavepvp.entity.type.hologram.line.type.HologramTextLine
import org.cavepvp.entity.util.InputPrompt
import org.cavepvp.entity.util.ItemBuilder
import org.cavepvp.entity.util.ListButton
import org.cavepvp.entity.util.PlayerUtil

class HologramLinesButton(parent: Menu, private val hologram: Hologram) : ListButton<HologramLine>(parent,15,30) {

    override fun addItem(item: HologramLine, player: Player): Runnable {
        return Runnable{}
    }

    override fun getEmptyDescription(player: Player): List<String> {
        return arrayListOf("${ChatColor.GRAY}No text")
    }

    override fun getItemStack(player: Player): ItemStack {
        return ItemBuilder.of(Material.SIGN)
            .name("${ChatColor.GOLD}${ChatColor.BOLD}Lines")
            .build()
    }

    override fun getList(): List<HologramLine> {
        return this.hologram.lines
    }

    override fun getTextFormat(item: HologramLine): Pair<String, String> {
        return when (item) {
            is HologramTextLine -> Pair(ChatColor.translateAlternateColorCodes('&',item.text),"")
            is HologramItemLine -> Pair("","")
            else -> Pair("N/A","")
        }
    }

    override fun getExtraDescription(player: Player): List<String> {
        return super.getExtraDescription(player).plus("${ChatColor.AQUA}Middle click to edit text")
    }

    override fun isModifiable(): Boolean {
        return true
    }

    override fun removeItem(item: HologramLine, player: Player): Runnable {
        return Runnable{
            this.hologram.remove(this.hologram.lines.withIndex().first{it.value.skullId == item.skullId}.index)
        }
    }

    override fun startAddConversation(player: Player, callback: Callback<HologramLine>) {
        player.closeInventory()

        PlayerUtil.startPrompt(player,object : StringPrompt() {

            override fun getPromptText(context: ConversationContext): String {
                return "${ChatColor.GREEN}Please type out text for this hologram"
            }

            override fun acceptInput(context: ConversationContext,input: String): Prompt? {
                this@HologramLinesButton.hologram.addText(input)
                HologramMenu(this@HologramLinesButton.hologram).openMenu(player)
                return END_OF_CONVERSATION
            }

        },600)
    }

    override fun clicked(player: Player, slot: Int, clickType: ClickType) {
        if (clickType == ClickType.MIDDLE) {
            InputPrompt{
                this.hologram.setText(this.index,it)
            }
                .withText("${ChatColor.GREEN}Please type out new text for this line.")
                .start(player)
            return
        }

        super.clicked(player, slot, clickType)
    }
}