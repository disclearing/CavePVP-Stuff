package org.cavepvp.entity.command

import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.cavepvp.entity.menu.EntityMenu

class EntityCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender?, p1: Command?, p2: String?, p3: Array<out String>?): Boolean {

        if (sender == null || sender !is Player) {
            return false
        }

        if (!sender.hasPermission("command.oplol")) {
            sender.sendMessage("no.")
            return false
        }

        EntityMenu().openMenu(sender as Player)
        return false
    }
}