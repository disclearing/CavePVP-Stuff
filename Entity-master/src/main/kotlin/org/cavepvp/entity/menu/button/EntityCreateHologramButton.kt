package org.cavepvp.entity.menu.button

import cc.fyre.proton.menu.Button
import org.apache.commons.lang.StringUtils
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.SkullType
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack
import org.cavepvp.entity.EntityHandler
import org.cavepvp.entity.menu.EntityCreateMenu
import org.cavepvp.entity.menu.hologram.HologramMenu
import org.cavepvp.entity.type.hologram.Hologram
import org.cavepvp.entity.util.InputPrompt
import org.cavepvp.entity.util.ItemBuilder
import java.util.*
import java.util.function.Predicate

object EntityCreateHologramButton: Button() {
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
        return ItemBuilder.of(Material.SKULL_ITEM)
            .name("${ChatColor.GREEN}Create Hologram")
            .data(SkullType.WITHER.ordinal)
            .build()
    }

    override fun clicked(player: Player, slot: Int, clickType: ClickType) {
        InputPrompt{

                if (EntityHandler.getEntityByName(it) != null) {
                    player.sendMessage("${ChatColor.RED}Entity with name \"$it\" already exists.")
                    EntityCreateMenu.openMenu(player)
                    return@InputPrompt
                }

                val hologram = Hologram(it,player.location.clone().add(0.0,1.3,0.0))

                hologram.addText("Use /entity to start editing this hologram.")

                EntityHandler.register(hologram)

                HologramMenu(hologram).openMenu(player)
                return@InputPrompt
        }.start(player)
    }
}