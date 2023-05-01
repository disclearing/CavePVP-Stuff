package cc.fyre.modsuite.mod.command

import cc.fyre.modsuite.mod.ModHandler
import cc.fyre.modsuite.mod.item.inventory.ModSuiteInventory
import cc.fyre.proton.command.Command
import cc.fyre.proton.command.param.Parameter
import cc.fyre.proton.command.param.defaults.offlineplayer.OfflinePlayerWrapper
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object InvseeCommand {

    const val PERMISSION = "modsuite.command.invsee"

    @JvmStatic
    @Command(names = ["invsee","inv","viewinv"],permission = PERMISSION)
    fun execute(sender: Player,@Parameter(name = "player") player: OfflinePlayerWrapper) {

        player.loadAsync{
            var inventory = ModHandler.getInventoryByPlayer(it)

            if (inventory == null) {
                inventory = ModSuiteInventory(it)
            }

            inventory.addViewer(sender)

            sender.sendMessage("${ChatColor.GOLD}Opening inventory of ${inventory.displayName}${ChatColor.GOLD}.")
        }


    }

}