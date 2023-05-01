package cc.fyre.modsuite.mod.editor

import cc.fyre.modsuite.ModSuite
import cc.fyre.modsuite.mod.ModHandler
import cc.fyre.modsuite.mod.item.ModModeItem
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.DyeColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.cavepvp.profiles.Profiles

object ModLayoutListener : Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    private fun onInventoryClick(event: InventoryClickEvent) {

        val whoClicked = event.whoClicked

        if (whoClicked !is Player) {
            return
        }

        if (!ModHandler.isInModMode(whoClicked.uniqueId)) {
            return
        }

        if (event.currentItem != ModModeItem.EDIT_ITEM) {
            return
        }

        ModLayoutEditor(whoClicked).also{
            ModLayoutHandler.setLayoutEditor(whoClicked,it)

            Bukkit.getServer().scheduler.runTaskLater(ModSuite.instance,{
                whoClicked.inventory.setItem(ModModeItem.EDIT_ITEM_SLOT,null)
                whoClicked.openInventory(it.getInventory())
            },2L)

        }

    }

    @EventHandler(priority = EventPriority.NORMAL)
    private fun onInventoryClickHandle(event: InventoryClickEvent) {

        if (event.isCancelled) {
            return
        }

        val player = event.whoClicked

        if (player !is Player) {
            return
        }

        val editor = ModLayoutHandler.getLayoutEditor(player) ?: return

        if (event.slot < 9) {

            if (event.cursor.type != Material.AIR) {

                if (event.currentItem != null) {

                    if (event.currentItem.type == Material.AIR) {

                        val item = ModHandler.getModModeItemByStack(event.cursor) ?: return

                        editor.setItemEnabled(item,event.slot)
                        return
                    }

                    ModHandler.getModModeItemByStack(event.currentItem)?.also{
                        editor.getLayout().setItemEnabled(it.getKey(),false)
                    }
                }

            }

            if (event.click.isShiftClick) {
                event.isCancelled = true
                ModHandler.getModModeItemByStack(event.currentItem)?.also{
                    editor.setItemDisabled(event.slot,it)
                }
                return
            }

            return
        }

        if (ModLayoutEditor.GLASS_SLOTS.contains(event.slot)) {
            event.isCancelled = true
            return
        }

        if (ModLayoutEditor.CARPET_SLOTS.containsKey(event.slot)) {
            event.isCancelled = true

            val color = DyeColor.getByWoolData(ModLayoutEditor.CARPET_SLOTS[event.slot]!!.durability.toByte())

            if (color == editor.getLayout().carpetColor) {
                return
            }

            editor.setCarpetColor(player,color)
            return
        }

        if (ModLayoutEditor.DISABLED_SLOTS.contains(event.slot)) {

            if (event.isShiftClick) {

                val item = ModHandler.getModModeItemByStack(event.currentItem) ?: return

                event.isCancelled = true

                val slot = IntRange(0,8).firstOrNull{editor.getInventory().getItem(it) == null} ?: return

                editor.setItemEnabled(item,slot)
                return
            }

            if (event.cursor.type != Material.AIR) {
                ModHandler.getModModeItemByStack(event.cursor)?.also{
                    editor.setItemDisabled(event.slot,it,event.slot)
                }
            }

            return
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private fun onInventoryClose(event: InventoryCloseEvent) {

        val player = event.player

        if (player !is Player) {
            return
        }

        if (event.inventory.title != ModLayoutEditor.TITLE) {
            return
        }

        val editor = ModLayoutHandler.removeLayoutEditor(player) ?: return

        Bukkit.getServer().scheduler.runTaskLater(ModSuite.instance,{

            if (!player.isOnline) {
                return@runTaskLater
            }

            val modMode = ModHandler.getModModeById(player.uniqueId) ?: return@runTaskLater

            if (!modMode.enabled) {
                return@runTaskLater
            }

            player.inventory.setItem(ModModeItem.EDIT_ITEM_SLOT,ModModeItem.EDIT_ITEM)

            modMode.refreshItems(player)
        },5L)

        if (!editor.isModified()) {
            return
        }

        Bukkit.getServer().scheduler.runTaskAsynchronously(ModSuite.instance) {
            Profiles.getInstance().playerProfileHandler.fromUuid(player.uniqueId).ifPresent{it.save()}
            player.sendMessage("${ChatColor.GREEN}Your mod mode layout has been updated.")
        }

    }

}