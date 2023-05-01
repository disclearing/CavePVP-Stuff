package org.cavepvp.entity.menu.npc.button

import cc.fyre.proton.menu.Menu
import cc.fyre.proton.util.Callback
import org.cavepvp.entity.util.ListButton
import org.cavepvp.entity.util.InputPrompt
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.cavepvp.entity.type.npc.NPC
import org.cavepvp.entity.util.ItemBuilder
import java.util.*

class NPCCommandsButton(menu: Menu,private val npc: NPC) : ListButton<String>(menu,10,50) {

    override fun getEmptyDescription(player: Player): List<String> {
        return listOf()
    }

    override fun getList(): List<String> {
        return this.npc.commands
    }

    override fun getTextFormat(item: String): Pair<String, String> {
        return Pair(item,"")
    }

    override fun isModifiable(): Boolean {
        return true
    }

    override fun addItem(item: String, player: Player): Runnable {
        this.npc.commands.add(item)

        return Runnable{this.npc.commands.add(item)}
    }

    override fun removeItem(item: String, player: Player): Runnable {
        this.npc.commands.remove(item)

        return Runnable{this.npc.commands.remove(item)}
    }

    override fun startAddConversation(player: Player, callback: Callback<String>) {
        player.closeInventory()
        InputPrompt{this.addItem(it,player)}
            .withText("${ChatColor.GREEN}Please provide a command for this npc: ")
            .start(player)
    }

    override fun getItemStack(player: Player): ItemStack {
        return ItemBuilder.of(Material.COMMAND)
            .name("${ChatColor.GOLD}${ChatColor.BOLD}Commands")
            .build()
    }


}