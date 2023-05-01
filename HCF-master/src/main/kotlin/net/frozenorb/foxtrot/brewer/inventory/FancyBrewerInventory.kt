package net.frozenorb.foxtrot.brewer.inventory

import net.frozenorb.foxtrot.brewer.FancyBrewer
import net.frozenorb.foxtrot.brewer.FancyBrewerResource
import net.minecraft.server.v1_7_R4.ChatComponentText
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftInventory

class FancyBrewerInventory(private val brewer: FancyBrewer) : CraftInventory(IFancyBrewerInventory(brewer)) {

    init {
        this.brewer.resources.forEach{

            if (it == null) {
                return@forEach
            }

            val resource = FancyBrewerResource.getResourceByType(it.type)
                ?: return@forEach

            this.setItem(resource.getSlot(),it)
        }
    }

    fun fill() {
        IntRange(0,this.size - 1)
            .filter{!NON_EMPTY_SLOTS.contains(it)}
            .forEach{this.setItem(it, FancyBrewer.BLANK_BUTTON)}
    }

    companion object {

        const val SIZE = 6*9
        val TITLE = "Fancy Brewer"

        val NON_EMPTY_SLOTS = FancyBrewerResource.getAllSlots()
            .toMutableSet()
            .apply{
                this.addAll(FancyBrewer.GLASS_SLOTS.toSet())
                this.addAll(FancyBrewer.BUTTONS.keys.toSet())
            }
            .toTypedArray()
    }

}