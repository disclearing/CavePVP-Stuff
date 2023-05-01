package cc.fyre.modsuite.mod.listener

import cc.fyre.modsuite.mod.ModHandler
import cc.fyre.modsuite.mod.item.ModModeItem
import cc.fyre.modsuite.mod.editor.ModLayoutEditor
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.*


object ModInventoryListener : Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    private fun onInventoryDrag(event: InventoryDragEvent) {

        if (event.whoClicked !is Player) {
            return
        }

        if (!ModHandler.isInModMode(event.whoClicked.uniqueId)) {
            return
        }

        event.isCancelled = true
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private fun onInventoryClick(event: InventoryClickEvent) {

        val source = event.whoClicked

        if (source !is Player) {
            return
        }

        if (!ModHandler.isInModMode(source.uniqueId)) {
            return
        }

        if (event.currentItem == null && event.cursor == null) {
            return
        }

        if (event.currentItem == ModModeItem.EDIT_ITEM) {
            //TODO EditModLayoutMenu().open(source)
        }

        if (event.inventory.title == ModLayoutEditor.TITLE) {
            return
        }

        event.isCancelled = true
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private fun onInventoryCreative(event: InventoryCreativeEvent) {

        val source = event.whoClicked

        if (source !is Player) {
            return
        }

        if (!ModHandler.isInModMode(source.uniqueId)) {
            return
        }

        val modModeItem = ModHandler.getModModeItemByStack(event.cursor) ?: return

        if (event.cursor.amount != 64 && event.cursor.amount == modModeItem.getItemStack(source).amount) {
            return
        }

        event.isCancelled = true
    }

}