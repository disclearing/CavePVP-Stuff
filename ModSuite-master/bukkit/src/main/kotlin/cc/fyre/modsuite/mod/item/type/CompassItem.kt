package cc.fyre.modsuite.mod.item.type

import cc.fyre.modsuite.ModSuite
import cc.fyre.modsuite.mod.ModHandler
import cc.fyre.modsuite.mod.item.ModModeItem
import cc.fyre.neutron.Neutron
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.cavepvp.profiles.Profiles

object CompassItem : ModModeItem,Listener {

    private val PERMISSIONS = arrayOf(
        "worldedit.navigation.thru.tool",
        "worldedit.navigation.jumpto.tool"
    ).map{it.toLowerCase()}

    override val slot = 0
    override val data = 0.toByte()
    override val type = Material.COMPASS
    override val name = "${ChatColor.AQUA}Compass"

    override fun getKey(): String {
        return "COMPASS"
    }

    override fun hasPermission(player: Player): Boolean {
        return ModSuite.instance.isWorldEditEnabled() && PERMISSIONS.any{player.hasPermission(it)}
    }

    override fun isPersonalized(): Boolean {
        return false
    }

    override fun handleInteract(event: PlayerInteractEvent) {}
    override fun handleInteractEntity(event: PlayerInteractEntityEvent) {}

    @EventHandler(priority = EventPriority.MONITOR)
    private fun onPlayerDropItem(event: PlayerDropItemEvent) {

        if (!ModHandler.isInModMode(event.player.uniqueId)) {
            return
        }

        if (ModHandler.getModModeItemByStack(event.itemDrop.itemStack) != this) {
            return
        }

        val player = Bukkit.getServer().onlinePlayers
            .filter{it.uniqueId != event.player.uniqueId && !ModHandler.getOnlineStaff().contains(it.uniqueId)}
            .randomOrNull()

        if (player == null) {
            event.player.sendMessage("${ChatColor.RED}There are no players online.")
            return
        }

        event.player.teleport(player)
        event.player.sendMessage("${ChatColor.GOLD}Teleporting to ${Neutron.getInstance().profileHandler.findDisplayName(player.uniqueId)}${ChatColor.GOLD}.")
    }
}