package net.frozenorb.foxtrot.brewer.button.type

import cc.fyre.proton.util.TimeUtils
import net.frozenorb.foxtrot.brewer.FancyBrewer
import net.frozenorb.foxtrot.brewer.FancyBrewerResource
import net.frozenorb.foxtrot.brewer.FancyBrewerState
import net.frozenorb.foxtrot.brewer.button.FancyBrewerButton
import net.frozenorb.foxtrot.util.ThePotionUtil
import net.frozenorb.foxtrot.util.UnicodeUtil
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.ItemStack

object BrewerStartButton : FancyBrewerButton() {

    override fun render(brewer: FancyBrewer): ItemStack {

        val lore = arrayListOf<String>()
        val displayName: String

        when (brewer.state) {
            FancyBrewerState.IDLE -> {
                displayName = "${ChatColor.GREEN}${ChatColor.BOLD}Start Brewing"

                val eta = brewer.getEstimatedTime()

                lore.add("${ChatColor.GRAY}Estimated Time: ${ChatColor.YELLOW}${if (eta == -1) "N/A" else TimeUtils.formatIntoHHMMSS(eta)}")
            }
            FancyBrewerState.BREWING -> {
                displayName = "${ChatColor.GREEN}${ChatColor.BOLD}Brewing"

                lore.add(" ")

                if (brewer.lastResource != null) {

                    val type = brewer.inventory.getItem(FancyBrewerResource.TYPE.getSlot())?.type

                    for (resource in FancyBrewerResource.getAllResources()) {

                        val item = brewer.inventory.getItem(resource.getSlot())?.type ?: continue

                        lore.add(buildString{
                            append(when {
                                brewer.lastResource!! > resource -> "${ChatColor.GREEN}"
                                brewer.lastResource!! == resource -> "${ChatColor.YELLOW}"
                                brewer.lastResource!! < resource -> "${ChatColor.GRAY}"
                                else -> { "" }
                            })
                            append(UnicodeUtil.SMALL_ARROW)
                            append(" ")
                            append("${ChatColor.WHITE}")
                            append(when {
                                resource ==FancyBrewerResource.WART -> "Awkward"
                                resource ==FancyBrewerResource.TYPE && type != null -> ThePotionUtil.getName(FancyBrewerResource.RESULT_TABLE[type]!!)
                                resource == FancyBrewerResource.INCREASE && type != null -> when (item) {
                                    Material.REDSTONE -> "Extend"
                                    Material.GLOWSTONE_DUST -> "${ThePotionUtil.getName(FancyBrewerResource.RESULT_TABLE[type]!!)} II"
                                    else -> ""
                                }
                                resource == FancyBrewerResource.SPLASH -> "Splash"
                                else -> {}
                            })
                        })
                    }

                    lore.add(" ")
                    lore.add("${ChatColor.RED}Right click to stop brewing")
                } else {
                    lore.add("${ChatColor.GRAY}Loading..")
                }

            }
        }

        return ItemStack(Material.BREWING_STAND_ITEM).apply {
            val itemMeta = this.itemMeta

            itemMeta.displayName = displayName
            itemMeta.lore = lore

            this.itemMeta = itemMeta
        }
    }

    override fun handleClick(player: Player, brewer: FancyBrewer,click: ClickType) {

        if (brewer.state != FancyBrewerState.IDLE && click.isRightClick) {
            when (brewer.state) {
                FancyBrewerState.BREWING -> {
                    brewer.state = FancyBrewerState.IDLE
                    brewer.refreshButtons()
                }
                else -> {}
            }
            return
        }

        if (brewer.getEstimatedTime() == -1) {
            player.sendMessage("${ChatColor.RED}This brewer does not have enough resources!")
            return
        }

        brewer.state = FancyBrewerState.BREWING
    }

}