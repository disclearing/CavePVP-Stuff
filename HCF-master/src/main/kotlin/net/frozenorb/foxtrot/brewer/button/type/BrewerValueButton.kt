package net.frozenorb.foxtrot.brewer.button.type

import net.frozenorb.foxtrot.brewer.FancyBrewer
import net.frozenorb.foxtrot.brewer.button.FancyBrewerButton
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack

object BrewerValueButton : FancyBrewerButton() {

    override fun render(brewer: FancyBrewer): ItemStack? {

        if (brewer.currentlyBrewing == null) {
            return ItemStack(Material.POTION)
        }

        return brewer.currentlyBrewing!!.toItemStack(brewer.currentAmount ?: 1)
    }

    override fun handleClick(player: Player, brewer: FancyBrewer, click: ClickType) {}

}