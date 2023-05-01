package cc.fyre.modsuite.mod.command

import cc.fyre.modsuite.mod.ModHandler
import cc.fyre.neutron.Neutron
import cc.fyre.neutron.profile.ProfileHandler
import cc.fyre.proton.command.Command
import cc.fyre.proton.command.param.Parameter
import org.bukkit.ChatColor

import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

object ModCommand {

    const val PERMISSION = "modsuite.command.modmode"

    @JvmStatic
    @Command(names = ["modmode","mod","staff","h","staffmode"],permission = PERMISSION)
    fun execute(sender: CommandSender,@Parameter(name = "player",defaultValue = "self") player: Player) {

        if (sender is Player && sender.uniqueId != player.uniqueId && !sender.hasPermission("$PERMISSION.other")) {
            sender.sendMessage("${ChatColor.RED}No permission.")
            return
        }

        val modMode = ModHandler.loadOrCreateByPlayer(player,false)

        modMode.setModMode(!modMode.enabled,player)

        if (sender is Player && sender.uniqueId != player.uniqueId) {
            sender.sendMessage("${ChatColor.GOLD}You have ${if (modMode.enabled) "${ChatColor.GREEN}enabled" else "${ChatColor.RED}disabled"}${ChatColor.GOLD} mod mode for ${Neutron.getInstance().profileHandler.findDisplayName(player.uniqueId)}${ChatColor.GOLD}.")
        }

        player.sendMessage("${ChatColor.GOLD}Mod Mode: ${if (modMode.enabled) "${ChatColor.GREEN}Enabled" else "${ChatColor.RED}Disabled"}")
    }

}