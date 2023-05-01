package cc.fyre.modsuite.freeze.command

import cc.fyre.modsuite.freeze.FreezeHandler
import cc.fyre.proton.command.Command
import cc.fyre.proton.command.param.Parameter
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object FreezeCommand {

    const val PERMISSION = "modsuite.command.freeze"

    @JvmStatic
    @Command(names = ["freeze","ss"],permission = PERMISSION)
    fun execute(sender: CommandSender,@Parameter(name = "player")player: Player) {

        if (sender is Player && !FreezeHandler.isAbleToFreeze(sender,player)) {
            sender.sendMessage("${ChatColor.RED}You are not able to freeze ${player.displayName}${ChatColor.RED}.")
            return
        }

        if (FreezeHandler.isFrozen(player)) {
            FreezeHandler.setFrozen(player,false)

            sender.sendMessage("${player.displayName}${ChatColor.GREEN}has been un-frozen.")
            player.sendMessage("${ChatColor.GREEN}${ChatColor.BOLD}You have been un-frozen by a staff member. Sorry for the inconvenience!")
            return
        }

        FreezeHandler.setFrozen(player,true)

        sender.sendMessage("${player.displayName} ${ChatColor.GREEN}has been frozen.")
        player.sendMessage("${ChatColor.GREEN}${ChatColor.BOLD}You have been frozen by a staff member${ChatColor.GREEN}.")
        player.sendMessage("${ChatColor.GREEN}${ChatColor.BOLD}Please join our teamspeak ${ChatColor.YELLOW}${ChatColor.BOLD}${ChatColor.UNDERLINE}${"ts.cavepvp.org"}${ChatColor.GREEN}.")
        return
    }

}