package org.cavepvp.entity.menu.hologram

import cc.fyre.proton.menu.Button
import cc.fyre.proton.menu.menus.ConfirmMenu
import cc.fyre.proton.util.Callback
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.SkullType
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import org.cavepvp.entity.EntityHandler
import org.cavepvp.entity.menu.EntityMenu
import org.cavepvp.entity.type.hologram.Hologram
import org.cavepvp.entity.type.hologram.line.type.HologramItemLine
import org.cavepvp.entity.type.hologram.line.type.HologramTextLine
import org.cavepvp.entity.util.ItemBuilder
import java.util.*
import java.util.function.Predicate

class HologramButton(private val hologram: Hologram) : Button() {
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

        val lore = arrayListOf<String>()

        lore.add("${ChatColor.DARK_GRAY}#${this.hologram.id}")
        lore.add(" ")

        if (this.hologram.lines.isNotEmpty()) {
            var index = 1

            for (line in this.hologram.lines) {

                val text = StringBuilder("${ChatColor.GRAY}${index}. ")

                when (line) {
                    is HologramTextLine -> text.append(buildString{

                        if (line.text.length > 25) {
                            text.append(line.text.substring(0,25))
                        } else {
                            text.append(line.text)
                        }

                    })
                    is HologramItemLine -> text.append(line.item.type.name)
                }

                lore.add(text.toString())

                index++
            }

        } else {
            lore.add("${ChatColor.YELLOW}No text")
        }


        lore.add(" ")

        if (this.hologram.parent != null) {
            EntityHandler.getEntityById(this.hologram.parent!!)?.also{
                lore.add("${ChatColor.GRAY}Parent: ${ChatColor.DARK_GRAY}#${it.id}")
            }
        }

        lore.add(" ")
        lore.add("${ChatColor.GREEN}Click to edit Hologram")

        return ItemBuilder.of(Material.SKULL_ITEM)
            .name("${ChatColor.GREEN}${ChatColor.BOLD}${this.hologram.name}")
            .lore(lore)
            .data(SkullType.SKELETON.ordinal)
            .build()
    }
    override fun clicked(player: Player, slot: Int, clickType: ClickType) {

        if (clickType.isRightClick) {

            if (clickType.isShiftClick) {
                object : ConfirmMenu("Delete Hologram?", Callback{
                    this@HologramButton.hologram.sendToAll{
                        this@HologramButton.hologram.sendDestroyPacket(it)
                    }

                    EntityHandler.destroy(this@HologramButton.hologram)
                    EntityMenu().openMenu(player)
                }){}.openMenu(player)
                return
            }

            player.teleport(this.hologram.getLocation())
        } else if (clickType.isLeftClick) {
            HologramMenu(this.hologram).openMenu(player)
        }

    }

}