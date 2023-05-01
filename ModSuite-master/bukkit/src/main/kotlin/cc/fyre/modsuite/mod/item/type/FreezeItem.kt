package cc.fyre.modsuite.mod.item.type

import cc.fyre.modsuite.freeze.FreezeHandler
import cc.fyre.modsuite.freeze.command.FreezeCommand
import cc.fyre.modsuite.mod.item.ModModeItem
import cc.fyre.neutron.Neutron
import cc.fyre.proton.util.ItemBuilder
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.SkullType
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent

object FreezeItem : ModModeItem {

    override val slot = 4
    override val data = 3.toByte()
    override val type = Material.SKULL_ITEM
    override val name = "${ChatColor.AQUA}Freeze"

    override fun isDefault(): Boolean {
        return false
    }

    override fun getKey(): String {
        return "FREEZE"
    }

    override fun hasPermission(player: Player): Boolean {
        return player.hasPermission(FreezeCommand.PERMISSION)
    }

    override fun isPersonalized(): Boolean {
        return true
    }

    override fun getItemBuilder(player: Player): ItemBuilder {
        return ITEM
    }

    override fun handleInteract(event: PlayerInteractEvent) {}

    override fun handleInteractEntity(event: PlayerInteractEntityEvent) {

        val entity = event.rightClicked

        if (entity !is Player) {
            return
        }

        if (!FreezeHandler.isAbleToFreeze(event.player,entity)) {
            event.player.sendMessage("${ChatColor.RED}You are not able to freeze ${Neutron.getInstance().profileHandler.findDisplayName(entity.uniqueId)}${ChatColor.RED}.")
            return
        }

        if (FreezeHandler.isFrozen(entity)) {
            FreezeHandler.setFrozen(entity,false)

            event.player.sendMessage("${Neutron.getInstance().profileHandler.findDisplayName(entity.uniqueId)} ${ChatColor.GREEN}has been un-frozen.")
            entity.sendMessage("${ChatColor.GREEN}${ChatColor.BOLD}You have been un-frozen by a staff member. Sorry for the inconvenience!")
            return
        }

        FreezeHandler.setFrozen(entity,true)

        event.player.sendMessage("${Neutron.getInstance().profileHandler.findDisplayName(entity.uniqueId)} ${ChatColor.GREEN}has been frozen.")
        entity.sendMessage("${ChatColor.GREEN}${ChatColor.BOLD}You have been frozen by a staff member${ChatColor.GREEN}.")
        entity.sendMessage("${ChatColor.GREEN}${ChatColor.BOLD}Please join our teamspeak ${ChatColor.YELLOW}${ChatColor.BOLD}${ChatColor.UNDERLINE}${"ts.cavepvp.org"}${ChatColor.GREEN}.")
        return
    }

    private const val TEXTURE = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzBlYmFkY2EzNjg4ZGE3ZjZmMjZmNmRiYWVhMzQ2ZDdhNDlmZmRmNDg5N2RkMTljNTUxODkwY2UwZWFhYTVkNSJ9fX0="
    private val ITEM = ItemBuilder.of(Material.SKULL_ITEM)
        .name(this.name)
        .data(SkullType.PLAYER.ordinal.toShort())
        .setLore(arrayListOf("${ChatColor.DARK_GRAY}${this.getKey()}"))
        .texture(TEXTURE)
}