package org.cavepvp.entity.menu.npc

import cc.fyre.proton.menu.Button
import cc.fyre.proton.menu.menus.ConfirmMenu
import cc.fyre.proton.util.Callback
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.SkullType
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack
import org.cavepvp.entity.EntityHandler
import org.cavepvp.entity.menu.EntityMenu
import org.cavepvp.entity.type.npc.NPC
import org.cavepvp.entity.util.ItemBuilder
import java.util.*
import java.util.function.Predicate

class NPCButton(private val npc: NPC) : Button() {
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

        lore.add("${ChatColor.DARK_GRAY}#${this.npc.id}")
        lore.add(" ")
        lore.add("${ChatColor.GRAY}Skin: ${ChatColor.YELLOW}${this.npc.skinUsername ?: "N/A"}")
        lore.add("${ChatColor.GRAY}Visibility: ${ChatColor.YELLOW}${this.npc.visibility.getDisplayName()}")
        lore.add(" ")
        lore.add("${ChatColor.GREEN}Click to edit NPC")

        return ItemBuilder.of(Material.SKULL_ITEM)
            .name("${ChatColor.GREEN}${ChatColor.BOLD}${this.npc.name}")
            .lore(lore)
            .data(SkullType.SKELETON.ordinal)
            .build()
    }

    override fun clicked(player: Player, slot: Int, clickType: ClickType) {

        if (clickType.isRightClick) {

            if (clickType.isShiftClick) {
                object : ConfirmMenu("Delete NPC?", Callback{
                    this@NPCButton.npc.sendToAll{
                        this@NPCButton.npc.sendDestroyPacket(it)
                    }

                    EntityHandler.destroy(this@NPCButton.npc)
                    EntityMenu().openMenu(player)
                }){}.openMenu(player)
                return
            }

            player.teleport(this.npc.getLocation())
        } else if (clickType.isLeftClick) {
            NPCMenu(this.npc).openMenu(player)
        }

    }

}