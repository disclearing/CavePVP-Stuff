package org.cavepvp.entity.menu.button

import cc.fyre.proton.menu.Button
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack
import org.cavepvp.entity.menu.EntityCreateMenu
import org.cavepvp.entity.util.ItemBuilder
import java.util.*

object EntityCreateButton : Button() {
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
        return ItemBuilder.of(Material.NETHER_STAR)
            .name("${ChatColor.GREEN}Create Entity")
            .build()
    }

    override fun clicked(player: Player?, slot: Int, clickType: ClickType?) {
        EntityCreateMenu.openMenu(player)
    }
}