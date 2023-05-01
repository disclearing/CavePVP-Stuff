package org.cavepvp.entity.command

import cc.fyre.proton.command.Command
import org.bukkit.entity.Player
import org.cavepvp.entity.menu.EntityMenu

object EntityCommand2 {

    @JvmStatic
    @Command(names = ["entity","entities"],permission = "essentials.command.entity")
    fun execute(sender: Player) {
        EntityMenu().openMenu(sender)
    }

}