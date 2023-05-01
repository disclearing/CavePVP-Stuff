package cc.fyre.modsuite.mod.item.type

import cc.fyre.modsuite.mod.ModHandler
import cc.fyre.modsuite.mod.item.ModModeItem
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import java.util.concurrent.ThreadLocalRandom

object RandomTeleportItem : ModModeItem {

    override val slot = 5
    override val data = 0.toByte()
    override val type = Material.DIAMOND
    override val name = "${ChatColor.AQUA}Random Teleport"

    override fun getKey(): String {
        return "RANDOM_TELEPORT"
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

        val player = Bukkit.getServer().onlinePlayers
            .filter{!ModHandler.isInModMode(it.uniqueId) && !ModHandler.isInVanish(it.uniqueId)}
            .randomOrNull() ?: return

        event.player.teleport(player)
    }

    override fun handleInteractEntity(event: PlayerInteractEntityEvent) {}
}