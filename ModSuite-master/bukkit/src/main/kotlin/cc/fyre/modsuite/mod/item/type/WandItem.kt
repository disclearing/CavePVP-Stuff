package cc.fyre.modsuite.mod.item.type

import cc.fyre.modsuite.mod.item.ModModeItem
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent

object WandItem : ModModeItem {

    private val PERMISSIONS = arrayOf(
        "worldedit.wand",
    ).map{it.toLowerCase()}

    override val slot = 2
    override val data = 0.toByte()
    override val type = Material.WOOD_AXE
    override val name = "${ChatColor.AQUA}World Edit Wand"

    override fun getKey(): String {
        return "WAND"
    }

    override fun hasPermission(player: Player): Boolean {
        return PERMISSIONS.any{player.hasPermission(it)}
    }

    override fun isPersonalized(): Boolean {
        return false
    }

    override fun handleInteract(event: PlayerInteractEvent) {}
    override fun handleInteractEntity(event: PlayerInteractEntityEvent) {}

}