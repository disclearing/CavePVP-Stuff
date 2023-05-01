package org.cavepvp.entity.menu.npc.menu

import cc.fyre.proton.menu.Button
import cc.fyre.proton.menu.buttons.BackButton
import cc.fyre.proton.menu.pagination.PaginatedMenu
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import org.cavepvp.entity.animation.EntityAnimationRegistry
import org.cavepvp.entity.menu.npc.NPCMenu
import org.cavepvp.entity.type.npc.NPC
import org.cavepvp.entity.util.ItemBuilder
import java.util.*

class NPCAnimationMenu(private val npc: NPC) : PaginatedMenu() {

    override fun getMaxItemsPerPage(player: Player): Int {
        return 2*9
    }
    
    override fun getGlobalButtons(player: Player): MutableMap<Int, Button> {
        return mutableMapOf(
            4 to BackButton(NPCMenu(this.npc))
        )
    }

    override fun getPrePaginatedTitle(p0: Player?): String {
        return "Animations"
    }

    override fun getAllPagesButtons(player: Player): MutableMap<Int, Button> {
        return EntityAnimationRegistry.getAllAnimations()
            .withIndex()
            .associate{it.index to object : Button() {
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

                    var item = ItemBuilder.copyOf(it.value.getDisplayItem().clone())
                        .name(it.value.getDisplayName())

                    if (this@NPCAnimationMenu.npc.animations.contains(it.value)) {
                        item = item.enchant(Enchantment.DURABILITY, 10)
                    }

                    return item.build()
                }
                
                override fun clicked(player: Player, slot: Int, clickType: ClickType) {

                    if (this@NPCAnimationMenu.npc.animations.contains(it.value)) {
                        this@NPCAnimationMenu.npc.animations.remove(it.value)
                    } else {
                        this@NPCAnimationMenu.npc.animations.add(it.value)
                    }

                }

            }}
            .toMutableMap()
    }

}