package cc.fyre.modsuite.mod.item.menu.button

import cc.fyre.modsuite.mod.ModHandler
import cc.fyre.modsuite.mod.ModVisibility
import cc.fyre.neutron.Neutron
import cc.fyre.proton.menu.Button
import cc.fyre.proton.util.ItemBuilder
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

class OnlineStaffButton(private val player: Player) : Button() {

    private val displayName = Neutron.getInstance().profileHandler.findDisplayName(this.player.uniqueId)
    override fun getButtonItem(player: Player): ItemStack {

        val lore = arrayListOf<String>()
        val modMode = ModHandler.loadOrCreateByPlayer(this.player,false)

        lore.add(" ")

        lore.add("${ChatColor.GOLD}Vanish: ${if (modMode.visibility != ModVisibility.VISIBLE) "${ChatColor.GREEN}Yes" else "${ChatColor.RED}No"}")
        lore.add("${ChatColor.GOLD}Mod Mode: ${if (modMode.enabled) "${ChatColor.GREEN}Yes" else "${ChatColor.RED}No"}")
        lore.add(" ")

        lore.add("${ChatColor.YELLOW}Click to teleport")

        return ItemBuilder.of(Material.SKULL_ITEM)
            .data(3)
            .name(this.displayName)
            .skull(this.player.name)
            .setLore(lore)
            .build()
    }
    override fun clicked(player: Player, slot: Int, clickType: ClickType?) {

        if (this.player.uniqueId == player.uniqueId) {
            return
        }

        player.teleport(this.player)
        player.sendMessage("${ChatColor.GOLD}Teleporting you to ${this.displayName}${ChatColor.GOLD}.")
    }

    override fun getName(p0: Player?): String {
        TODO("Not yet implemented")
    }

    override fun getDescription(p0: Player?): MutableList<String> {
        TODO("Not yet implemented")
    }

    override fun getMaterial(p0: Player?): Material {
        TODO("Not yet implemented")
    }

}