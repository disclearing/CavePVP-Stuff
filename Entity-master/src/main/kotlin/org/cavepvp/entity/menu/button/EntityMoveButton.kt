package org.cavepvp.entity.menu.button

import cc.fyre.proton.menu.Button
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack
import org.cavepvp.entity.Entity
import org.cavepvp.entity.EntityVisibility
import org.cavepvp.entity.util.ItemBuilder
import java.util.*

class EntityMoveButton(private val entity: Entity) : Button() {
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
        return ItemBuilder.copyOf(this.entity.getMoveItem().clone())
            .build()
    }

    override fun clicked(player: Player, slot: Int, clickType: ClickType?) {

        if (player.inventory.firstEmpty() == -1) {
            player.sendMessage("${ChatColor.RED}You have no space in your inventory!")
            return
        }

        player.inventory.addItem(this.entity.getMoveItem())
        player.closeInventory()

        this.entity.updateVisibility(EntityVisibility.HIDDEN)
    }

}