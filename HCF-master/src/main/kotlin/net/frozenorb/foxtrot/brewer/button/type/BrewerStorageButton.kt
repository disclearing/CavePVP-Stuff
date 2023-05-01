package net.frozenorb.foxtrot.brewer.button.type

import net.frozenorb.foxtrot.brewer.FancyBrewer
import net.frozenorb.foxtrot.brewer.button.FancyBrewerButton
import net.frozenorb.foxtrot.util.ThePotionUtil
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.Potion
import org.bukkit.potion.PotionType
import kotlin.math.min

object BrewerStorageButton : FancyBrewerButton() {

    override fun render(brewer: FancyBrewer): ItemStack {

        if (brewer.brewed.isEmpty()) {
            return ItemStack(Material.MINECART).apply {
                val itemMeta = this.itemMeta

                itemMeta.displayName = "${org.bukkit.ChatColor.BLUE}${org.bukkit.ChatColor.BOLD}Storage"
                itemMeta.lore = listOf("${ChatColor.GRAY}Empty")

                this.itemMeta = itemMeta
            }
        }

        val lore = arrayListOf<String>()
        var total = 0

        for ((damage,amount) in brewer.brewed.entries.sortedBy{it.value.get()}) {
            total += amount.get()

            if (damage == 0) {
                continue
            }

            val potion = Potion.fromDamage(damage)

            if (potion.type == PotionType.WATER) {
                continue
            }

            lore.add(buildString {
                append("${ChatColor.GRAY}${amount.get()}x")
                append(" ")

                if (potion == null || potion.type == null) {
                    append("${ChatColor.WHITE}???")
                } else {
                    append(ThePotionUtil.getColor(potion.type.effectType))

                    if (potion.isSplash) {
                        append("Splash")
                        append(" ")
                    }

                    append(ThePotionUtil.getDisplayName(potion.type.effectType))
                    append(" ")
                    append(if (potion.level == 2) "II" else "")
                }

            })
        }

        return ItemStack(Material.HOPPER_MINECART).apply {

            val itemMeta = this.itemMeta

            itemMeta.displayName = "${ChatColor.BLUE}${ChatColor.BOLD}Storage"
            itemMeta.lore = lore

            this.itemMeta = itemMeta
        }
    }

    override fun handleClick(player: Player, brewer: FancyBrewer,click: ClickType) {

    }

}