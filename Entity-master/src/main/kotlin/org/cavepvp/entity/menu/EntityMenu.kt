package org.cavepvp.entity.menu

import cc.fyre.proton.menu.Button
import cc.fyre.proton.menu.pagination.PaginatedMenu
import org.bukkit.Material
import org.cavepvp.entity.menu.button.EntityCreateButton
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.cavepvp.entity.EntityHandler
import org.cavepvp.entity.menu.hologram.HologramButton
import org.cavepvp.entity.menu.npc.NPCButton
import org.cavepvp.entity.type.hologram.Hologram
import org.cavepvp.entity.type.npc.NPC

class EntityMenu : PaginatedMenu() {

    override fun getPrePaginatedTitle(p0: Player?): String {
        return "Entities"
    }

    override fun getAllPagesButtons(p0: Player?): MutableMap<Int, Button> {
        return EntityHandler.getAllEntities()
            .sortedByDescending{it is NPC }
            .withIndex()
            .associate{it.index to when (it.value) {
                is NPC -> NPCButton(it.value as NPC)
                is Hologram -> HologramButton(it.value as Hologram)
                else -> Button.fromItem(ItemStack(Material.AIR)) // Should never really happen?
            }}
            .toMutableMap()
    }

    override fun getGlobalButtons(player: Player?): MutableMap<Int, Button> {
        return mutableMapOf(
            4 to EntityCreateButton
        )
    }

}