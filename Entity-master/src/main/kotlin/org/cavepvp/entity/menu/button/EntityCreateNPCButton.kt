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
import org.cavepvp.entity.EntityVisibility
import org.cavepvp.entity.menu.EntityCreateMenu
import org.cavepvp.entity.menu.npc.NPCMenu
import org.cavepvp.entity.type.npc.NPC
import org.cavepvp.entity.util.InputPrompt
import org.cavepvp.entity.util.ItemBuilder
import java.util.*
import java.util.function.Predicate

object EntityCreateNPCButton : Button() {
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
            .name("${ChatColor.GREEN}Create NPC")
            .data(SkullType.PLAYER.ordinal)
            .build()
    }

    override fun clicked(player: Player, slot: Int, clickType: ClickType?) {

        InputPrompt{

            if (EntityHandler.getEntityByName(it) != null) {
                player.sendMessage("${ChatColor.RED}Entity with name \"$it\" already exists.")
                EntityCreateMenu.openMenu(player)
                return@InputPrompt
            }

            val npc = NPC(it,player.location.clone())

            npc.tabVisibility = EntityVisibility.HIDDEN
            npc.tagVisibility = EntityVisibility.HIDDEN

            EntityHandler.register(npc)

            NPCMenu(npc).openMenu(player)
        }
            .withText("Please type a name: ")
            .start(player)
    }
}