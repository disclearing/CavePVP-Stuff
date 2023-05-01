package net.frozenorb.foxtrot.brewer.button

import net.frozenorb.foxtrot.brewer.FancyBrewer
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack

abstract class FancyBrewerButton {

    abstract fun render(brewer: FancyBrewer):ItemStack?
    abstract fun handleClick(player: Player,brewer: FancyBrewer,click: ClickType)

}