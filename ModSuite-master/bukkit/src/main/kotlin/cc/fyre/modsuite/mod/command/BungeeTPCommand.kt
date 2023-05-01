package cc.fyre.modsuite.mod.command

import cc.fyre.modsuite.mod.ModHandler
import cc.fyre.modsuite.mod.packet.PidginNiggerPacket
import cc.fyre.neutron.Neutron
import cc.fyre.neutron.profile.Profile
import cc.fyre.proton.Proton
import cc.fyre.proton.command.Command
import cc.fyre.proton.command.param.Parameter
import cc.fyre.universe.UniverseAPI
import cc.fyre.universe.util.BungeeUtil
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.util.*

object BungeeTPCommand {

    const val PERMISSION = "modsuite.command.bungeetp"

    @JvmStatic
    @Command(names = ["bungeeteleport","bungeetp","btp","proxytp","ptp"],async = true,permission = PERMISSION)
    fun execute(sender: Player,@Parameter(name = "player") argument: UUID) {

        val player = Bukkit.getServer().getPlayer(argument)

        if (player != null) {
            sender.chat("/teleport ${player.name}")
            return
        }

        val profile = Neutron.getInstance().profileHandler.fromUuid(argument)

        if (!profile.serverProfile.isOnline || profile.serverProfile.currentServer == null) {
            sender.sendMessage(buildString{
                append(profile.fancyName)
                append(" ")
                append("${ChatColor.RED}is currently offline.")
            })
            return
        }

        val server =  UniverseAPI.serverFromName(profile.serverProfile.currentServer!!)

        sender.sendMessage(buildString {
            append("${ChatColor.GOLD}Teleporting you to")
            append(" ")
            append(profile.fancyName)
            append(" ")
            append("${ChatColor.GOLD}on")
            append(" ")
            append(ChatColor.translateAlternateColorCodes('&',profile.serverProfile.currentServer))
            append("${ChatColor.GOLD}.")
        })

        Proton.getInstance().pidginHandler.sendPacket(PidginNiggerPacket(
            server.port,
            sender.uniqueId,
            profile.uuid
        ))

        BungeeUtil.sendToServer(sender,server.name)
    }

}