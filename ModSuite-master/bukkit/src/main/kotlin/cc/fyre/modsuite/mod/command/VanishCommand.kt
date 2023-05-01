package cc.fyre.modsuite.mod.command

import cc.fyre.modsuite.mod.ModHandler
import cc.fyre.modsuite.mod.ModVisibility
import cc.fyre.modsuite.mod.item.type.VisibilityItem
import cc.fyre.neutron.Neutron
import cc.fyre.proton.command.Command
import cc.fyre.proton.command.param.Parameter
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object VanishCommand {

    const val PERMISSION = "modsuite.command.vanish"

    @JvmStatic
    @Command(names = ["vanish","v","visibility"],permission = ModCommand.PERMISSION)
    fun execute(sender: CommandSender, @Parameter(name = "player",defaultValue = "self") player: Player) {

        if (sender is Player && sender.uniqueId != player.uniqueId && !sender.hasPermission("${PERMISSION}.other")) {
            sender.sendMessage("${ChatColor.RED}No permission.")
            return
        }

        val modMode = ModHandler.loadOrCreateByPlayer(player,false)

        modMode.setVisibility(modMode.getNextVisibility(),player)

        if (modMode.enabled) {
            modMode.refreshItem(player,VisibilityItem)
        }

        if (sender is Player && sender.uniqueId != player.uniqueId) {
            sender.sendMessage("${ChatColor.GOLD}You have ${if (modMode.visibility != ModVisibility.VISIBLE) "${ChatColor.GREEN}enabled" else "${ChatColor.RED}disabled"}${ChatColor.GOLD} vanish for ${Neutron.getInstance().profileHandler.findDisplayName(player.uniqueId)}${ChatColor.GOLD}.")
        }

        when (modMode.visibility) {
            ModVisibility.VISIBLE -> {

                if (sender is Player && sender.uniqueId != player.uniqueId) {
                    sender.sendMessage("${ChatColor.GOLD}You have ${ChatColor.GREEN}enabled${ChatColor.GOLD} vanish mode for ${Neutron.getInstance().profileHandler.findDisplayName(player.uniqueId)}${ChatColor.GOLD}.")
                } else {
                    sender.sendMessage("${ChatColor.GOLD}Vanish: ${ChatColor.RED}Visible")
                }

            }
            ModVisibility.INVISIBLE -> {

                if (sender is Player && sender.uniqueId != player.uniqueId) {
                    sender.sendMessage("${ChatColor.GOLD}You have ${ChatColor.RED}disabled${ChatColor.GOLD} vanish mode for ${Neutron.getInstance().profileHandler.findDisplayName(player.uniqueId)}${ChatColor.GOLD}.")
                } else {
                    sender.sendMessage("${ChatColor.GOLD}Vanish: ${ChatColor.GREEN}Invisible")
                }

            }
            ModVisibility.LOWER_STAFF -> {
                if (sender is Player && sender.uniqueId != player.uniqueId) {
                    sender.sendMessage("${ChatColor.GOLD}You have ${ChatColor.DARK_AQUA}staff${ChatColor.GOLD} vanish mode for ${Neutron.getInstance().profileHandler.findDisplayName(player.uniqueId)}${ChatColor.GOLD}.")
                } else {
                    sender.sendMessage("${ChatColor.GOLD}Vanish: ${ChatColor.DARK_AQUA}Staff")
                }
            }

        }
    }

}
