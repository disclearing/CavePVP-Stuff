package cc.fyre.modsuite.mod.command

import cc.fyre.modsuite.ModSuite
import cc.fyre.modsuite.mod.ModHandler
import cc.fyre.modsuite.mod.editor.ModLayoutEditor
import cc.fyre.modsuite.mod.editor.ModLayoutHandler
import cc.fyre.modsuite.mod.item.ModModeItem
import cc.fyre.proton.command.Command
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object EditModModeCommand {

    @JvmStatic
    @Command(names = ["editmodmode"], permission = "modsuite.command.modmode.edit", hidden = true)
    fun execute(sender: Player) {

        if (!ModHandler.isInModMode(sender.uniqueId)) {
            sender.sendMessage("${ChatColor.RED}You must be in mod mode to do edit your layout.")
            return
        }


        ModLayoutEditor(sender).also{
            ModLayoutHandler.setLayoutEditor(sender,it)

            Bukkit.getServer().scheduler.runTaskLater(ModSuite.instance,{
                sender.inventory.setItem(ModModeItem.EDIT_ITEM_SLOT,null)
                sender.openInventory(it.getInventory())
            },2L)
        }
    }

}