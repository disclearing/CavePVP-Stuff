package cc.fyre.modsuite.freeze.command

import cc.fyre.modsuite.freeze.FreezeHandler
import cc.fyre.modsuite.mod.ModHandler
import cc.fyre.proton.command.Command
import mkremins.fanciful.FancyMessage
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player

object PanicCommand {

    @JvmStatic
    @Command(names = ["panic"],permission = "modsuite.command.panic")
    fun execute(sender: Player) {

        if (FreezeHandler.isFrozen(sender)) {
            sender.sendMessage("${ChatColor.RED}You are already in ${ChatColor.BOLD}Panic ${ChatColor.RED}mode.")
            return
        }

        val message = FancyMessage("${ChatColor.RED}${ChatColor.BOLD}[Panic] ${sender.displayName}${ChatColor.YELLOW} has used ${ChatColor.RED}${ChatColor.BOLD}/panic${ChatColor.YELLOW}!")
            .tooltip("${ChatColor.YELLOW}Click here to teleport.")
            .command("/teleport ${sender.name}")

        ModHandler.getAllOnlineStaffMembers()
            .also{Bukkit.getServer().consoleSender}
            .forEach{message.send(it)}

        FreezeHandler.setFrozen(sender,true)

        sender.sendMessage("${ChatColor.GREEN}You have been frozen as you used ${ChatColor.BOLD}/panic${ChatColor.GREEN}.")
    }

}