package cc.fyre.modsuite.mod.item.type

import cc.fyre.modsuite.mod.ModHandler
import cc.fyre.modsuite.mod.item.ModModeItem
import cc.fyre.modsuite.mod.item.menu.OnlineStaffMenu
import cc.fyre.proton.util.ItemBuilder
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.SkullType
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import kotlin.math.max

object OnlineStaffItem : ModModeItem {

    override val slot = 7
    override val data = SkullType.PLAYER.ordinal.toByte()
    override val type = Material.SKULL_ITEM
    override val name = "${ChatColor.AQUA}Online Staff"

    override fun getKey(): String {
        return "ONLINE_STAFF"
    }

    override fun isRefresh(): Boolean {
        return true
    }

    override fun hasPermission(player: Player): Boolean {
        return true
    }

    override fun isPersonalized(): Boolean {
        return true
    }

    override fun handleInteract(event: PlayerInteractEvent) {
        OnlineStaffMenu().openMenu(event.player)
    }

    override fun handleInteractEntity(event: PlayerInteractEntityEvent) {}

    override fun getItemBuilder(player: Player): ItemBuilder {
        return super.getItemBuilder(player)
            .amount(max(ModHandler.getOnlineStaff().size,1))
            .skull(player.name)
    }
}