package org.cavepvp.entity.menu.npc.button

import cc.fyre.proton.Proton
import cc.fyre.proton.menu.Button
import cc.fyre.proton.util.MojangUtil
import cc.fyre.proton.util.UUIDUtils
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.SkullType
import org.bukkit.conversations.ConversationContext
import org.bukkit.conversations.Prompt
import org.bukkit.conversations.StringPrompt
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack
import org.cavepvp.entity.EntityPlugin
import org.cavepvp.entity.menu.npc.NPCMenu
import org.cavepvp.entity.type.npc.NPC
import org.cavepvp.entity.util.ItemBuilder
import org.cavepvp.entity.util.PlayerUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class NPCSkinButton(private val npc: NPC) : Button() {
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
            .name("${ChatColor.GOLD}${ChatColor.BOLD}Skin")
            .data(SkullType.PLAYER.ordinal)
            .build()
    }

    override fun clicked(player: Player, slot: Int, clickType: ClickType) {
        player.closeInventory()
        PlayerUtil.startPrompt(player,object : StringPrompt() {

            override fun getPromptText(context: ConversationContext): String {
                return "${ChatColor.YELLOW}Type a player's skin for this NPC."
            }

            override fun acceptInput(context: ConversationContext,input: String): Prompt? {

                Bukkit.getServer().scheduler.runTaskAsynchronously(EntityPlugin.instance) {

                    var uuid = UUIDUtils.uuid(input)

                    if (uuid == null) {
                        uuid = MojangUtil.getFromMojang(input)
                    }

                    if (uuid == null) {

                        if (player.isOnline) {
                            player.sendMessage("${ChatColor.RED}Unable to find \"$input\" in mojang database.")
                        }

                        return@runTaskAsynchronously
                    }

                    val skin = MojangUtil.getSkin(uuid)

                    if (skin == null) {
                        player.sendMessage("${ChatColor.RED}Unable to find \"$input\" in mojang database. [1]")
                        return@runTaskAsynchronously
                    }

                    if (player.isOnline) {
                        NPCMenu(npc).openMenu(player)
                    }

                    npc.setSkin(input,skin.key,skin.value)

                }

                return END_OF_CONVERSATION
            }

        },600,EntityPlugin.instance)
    }
}
