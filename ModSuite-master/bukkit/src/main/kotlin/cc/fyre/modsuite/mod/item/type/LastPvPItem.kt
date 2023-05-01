package cc.fyre.modsuite.mod.item.type

import cc.fyre.modsuite.mod.ModHandler
import cc.fyre.modsuite.mod.item.ModModeItem
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent

object LastPvPItem : ModModeItem {
    override val slot = 6
    override val data = 0.toByte()
    override val type = Material.EMERALD
    override val name = "${ChatColor.GREEN}Teleport to PvP"

    override fun getKey(): String {
        return "LAST_PVP"
    }

    override fun isDefault(): Boolean {
        return false
    }

    override fun hasPermission(player: Player): Boolean {
        return true
    }

    override fun isPersonalized(): Boolean {
        return false
    }

    override fun handleInteract(event: PlayerInteractEvent) {

        if (ModHandler.getLastPvPLocation() == null) {
            return
        }

        event.player.teleport(ModHandler.getLastPvPLocation())
    }

    override fun handleInteractEntity(event: PlayerInteractEntityEvent) {}

}