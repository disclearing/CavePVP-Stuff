package net.frozenorb.foxtrot.brewer.button.type

import net.frozenorb.foxtrot.brewer.FancyBrewer
import net.frozenorb.foxtrot.brewer.button.FancyBrewerButton
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack

object BrewerTransferButton : FancyBrewerButton() {

    override fun render(brewer: FancyBrewer): ItemStack {

        val lore = arrayListOf<String>()

        when {
            brewer.hopper == null -> lore.add("${ChatColor.GRAY}No Hopper")
            brewer.locked -> lore.add("${ChatColor.RED}Paused")
            else -> lore.add("${ChatColor.GREEN}Enabled")
        }

        return ItemStack(Material.HOPPER).apply {
            val itemMeta = this.itemMeta

            itemMeta.displayName = "${ChatColor.YELLOW}${ChatColor.BOLD}Transfer"
            itemMeta.lore = lore

            this.itemMeta = itemMeta
        }
    }

    override fun handleClick(player: Player, brewer: FancyBrewer,click: ClickType) {

        if (brewer.hopper == null) {
            return
        }

        brewer.locked = !brewer.locked
        brewer.refreshButtons()
    }

}