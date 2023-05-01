package cc.fyre.modsuite.mod.item.type

import cc.fyre.modsuite.mod.ModHandler
import cc.fyre.modsuite.mod.ModVisibility
import cc.fyre.modsuite.mod.command.VanishCommand
import cc.fyre.modsuite.mod.item.ModModeItem
import cc.fyre.proton.util.ItemBuilder
import org.bukkit.ChatColor
import org.bukkit.DyeColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent

object VisibilityItem : ModModeItem {

    override val slot = 8
    override val data = DyeColor.GRAY.dyeData
    override val type = Material.INK_SACK
    override val name = "${ChatColor.AQUA}Become Visible"

    override fun getKey(): String {
        return "VANISH"
    }

    override fun hasPermission(player: Player): Boolean {
        return player.hasPermission(VanishCommand.PERMISSION)
    }

    override fun isPersonalized(): Boolean {
        return true
    }

    override fun handleInteract(event: PlayerInteractEvent) {

        val modMode = ModHandler.loadOrCreateByPlayer(event.player,true)

        modMode.setVisibility(modMode.getNextVisibility(),event.player)

        event.player.itemInHand = this.getItemStack(event.player)
    }
    override fun handleInteractEntity(event: PlayerInteractEntityEvent) {}

    override fun getItemBuilder(player: Player): ItemBuilder {
        return when (ModHandler.getModModeById(player.uniqueId)!!.visibility) {
            ModVisibility.VISIBLE -> super.getItemBuilder(player)
                .data(DyeColor.LIME.dyeData.toShort())
                .name("${ChatColor.AQUA}Become Invisible")
            ModVisibility.INVISIBLE -> {
                super.getItemBuilder(player)
            }
            ModVisibility.LOWER_STAFF -> {
                super.getItemBuilder(player)
                    .name("${ChatColor.AQUA}Become Visible to all staff")
                    .data(DyeColor.CYAN.dyeData.toShort())
            }
        }
    }

}