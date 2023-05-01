package net.frozenorb.foxtrot.brewer.button.type

import cc.fyre.proton.util.ItemBuilder
import cc.fyre.proton.util.ItemUtils
import net.frozenorb.foxtrot.brewer.FancyBrewer
import net.frozenorb.foxtrot.brewer.button.FancyBrewerButton
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack

object BrewerInfoButton : FancyBrewerButton() {

    override fun render(brewer: FancyBrewer): ItemStack {

        val lore = arrayListOf<String>()

        if (brewer.isEmpty()) {
            lore.add("${ChatColor.GRAY}Empty")
        } else {

            for (bottle in brewer.bottles) {

                if (bottle == null) {
                    continue
                }

                lore.add(this.format(bottle))
            }

            for (resource in brewer.resources) {

                if (resource == null) {
                    continue
                }

                lore.add(this.format(resource))
            }

        }

        return ItemBuilder.of(Material.NAME_TAG)
            .name("${ChatColor.GREEN}${ChatColor.BOLD}Resources")
            .setLore(lore)
            .build()
    }

    override fun handleClick(player: Player,brewer: FancyBrewer,click: ClickType) {}

    private fun format(item: ItemStack):String {
        return buildString{
            append("${ChatColor.GRAY}${item.amount}x ")
            append("${ChatColor.WHITE}${ItemUtils.getName(item)}")
        }
    }
}