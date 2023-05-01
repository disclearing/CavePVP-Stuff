package cc.fyre.modsuite.mod.item.type

import cc.fyre.modsuite.mod.ModHandler
import cc.fyre.modsuite.mod.item.ModModeItem
import cc.fyre.modsuite.mod.item.inventory.ModSuiteInventory
import cc.fyre.proton.util.ItemBuilder
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.SkullType
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent

object BookItem : ModModeItem {

    override val slot = 1
    override val data = 0.toByte()
    override val type = Material.BOOK
    override val name = "${ChatColor.AQUA}Inspect Inventory"

    override fun getKey(): String {
        return "BOOK"
    }

    override fun hasPermission(player: Player): Boolean {
        return true
    }

    override fun isPersonalized(): Boolean {
        return true
    }

    override fun getItemBuilder(player: Player): ItemBuilder {
        return ITEM
    }

    override fun handleInteract(event: PlayerInteractEvent) {}
    override fun handleInteractEntity(event: PlayerInteractEntityEvent) {

        if (event.rightClicked !is Player) {
            return
        }

        event.player.openInventory((ModHandler.getInventoryByPlayer(event.rightClicked as Player) ?: ModSuiteInventory(event.rightClicked as Player)).getInventory())
    }

    private const val TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjdhZTk4MWNjYjg3NGU2ZThhNmFjOGY3MTcxYmFjZmE0NTBmZDg3NTk0N2U0MDc0ODMyMmU2OWFiNmYyNzA0ZiJ9fX0="
    private val ITEM = ItemBuilder.of(Material.SKULL_ITEM)
        .name(this.name)
        .data(SkullType.PLAYER.ordinal.toShort())
        .setLore(arrayListOf("${ChatColor.DARK_GRAY}${this.getKey()}"))
        .texture(TEXTURE)

}