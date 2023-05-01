package cc.fyre.modsuite.mod.item

import cc.fyre.proton.util.ItemBuilder
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.cavepvp.profiles.Profiles

interface ModModeItem {

    val slot: Int
    val data: Byte
    val type: Material
    val name: String
    val amount: Int
        get() = 1

    private var itemBuilder: ItemBuilder
        get() = ItemBuilder.of(this.type)
            .name(this.name)
            .data(this.data.toShort())
            .setLore(arrayListOf("${ChatColor.DARK_GRAY}${this.getKey()}"))


        set(value) = throw IllegalStateException("Cannot override ItemBuilder for mod item.")

    private var itemStack: ItemStack
        get() = this.itemBuilder.build()
        set(value) = throw IllegalStateException("Cannot override ItemStack for mod item.")

    fun getKey():String
    fun hasPermission(player: Player):Boolean

    fun isRefresh():Boolean {
        return false
    }

    fun isDefault():Boolean {
        return true
    }

    fun isPersonalized():Boolean

    fun handleInteract(event: PlayerInteractEvent)
    fun handleInteractEntity(event: PlayerInteractEntityEvent)

    fun getSlot(player: Player):Int {

        val setting = Profiles.getInstance().playerProfileHandler.fromUuid(player.uniqueId).orElseGet(null)?.modLayout

        if (setting != null) {
            return setting.getSlotByItem(this.getKey(),this.slot)
        }

        return this.slot
    }

    fun getItemStack(player: Player):ItemStack {

        if (this.isPersonalized()) {
            return this.getItemBuilder(player).build()
        }

        return this.itemStack
    }

    fun getItemBuilder(player: Player):ItemBuilder {

        if (this.isPersonalized()) {
            return ItemBuilder.of(this.type)
                .name(this.name)
                .data(this.data.toShort())
                .setLore(arrayListOf("${ChatColor.DARK_GRAY}${this.getKey()}"))
        }

        return this.itemBuilder
    }

    companion object {

        val EDIT_ITEM = ItemBuilder.of(Material.EMERALD)
            .name("${ChatColor.GREEN}${ChatColor.BOLD}Edit Mod Mode")
            .build()

        val EDIT_ITEM_SLOT = 22
    }

}