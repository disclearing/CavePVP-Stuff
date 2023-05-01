package cc.fyre.modsuite.mod.item.type

import cc.fyre.modsuite.mod.item.ModModeItem
import cc.fyre.proton.util.ItemBuilder
import org.bukkit.DyeColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.cavepvp.profiles.Profiles

object CarpetItem : ModModeItem {

    override val slot = 3
    override val data = DyeColor.ORANGE.woolData
    override val type = Material.CARPET
    override val name = " "

    override fun getKey(): String {
        return "CARPET"
    }

    override fun hasPermission(player: Player): Boolean {
        return true
    }

    override fun isPersonalized(): Boolean {
        return true
    }

    override fun handleInteract(event: PlayerInteractEvent) {}
    override fun handleInteractEntity(event: PlayerInteractEntityEvent) {}

    override fun getItemBuilder(player: Player): ItemBuilder {

        val setting = Profiles.getInstance().playerProfileHandler.fromUuid(player.uniqueId)?.orElseGet{null}?.modLayout

        if (setting != null) {
            return super.getItemBuilder(player)
                .data(setting.carpetColor.woolData.toShort())
        }

        return super.getItemBuilder(player)
    }

}