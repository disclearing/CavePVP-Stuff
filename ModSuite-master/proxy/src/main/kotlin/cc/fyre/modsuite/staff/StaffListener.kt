package cc.fyre.modsuite.staff

import cc.fyre.core.Core
import cc.fyre.core.profile.ProfileHandler
import cc.fyre.core.staff.StaffHandler
import cc.fyre.modsuite.ModSuite
import net.md_5.bungee.BungeeCord
import net.md_5.bungee.api.config.ServerInfo
import net.md_5.bungee.api.event.PlayerDisconnectEvent
import net.md_5.bungee.api.event.ServerConnectedEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler
import net.md_5.bungee.event.EventPriority
import java.util.*
import java.util.concurrent.ConcurrentHashMap

object StaffListener : Listener {

    private val servers = ConcurrentHashMap<UUID,ServerInfo>()

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onPlayerJoin(event: ServerConnectedEvent) {

        val profile = ProfileHandler.getProfileById(event.player.uniqueId)

        if (profile == null || !profile.rank.isStaff()) {
            return
        }

        val payload = mutableMapOf<String,String>()

        payload["to"] = event.server.info.name

        if (event.player.server != null) {
            payload["from"] = event.player.server.info.name
        }

        payload["player"] = event.player.uniqueId.toString()
        payload["displayName"] = profile.getDisplayName()

        BungeeCord.getInstance().scheduler.runAsync(ModSuite.instance) {
            Core.instance.circuit.sendPacket(StaffHandler.STAFF_SERVER_PACKET,payload)
        }

        this.servers[event.player.uniqueId] = event.server.info
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onPlayerDisconnect(event: PlayerDisconnectEvent) {

        val server = this.servers.remove(event.player.uniqueId) ?: return
        val profile = ProfileHandler.getProfileById(event.player.uniqueId)

        if (profile == null || !profile.rank.isStaff()) {
            return
        }

        BungeeCord.getInstance().scheduler.runAsync(ModSuite.instance) {
            Core.instance.circuit.sendPacket(StaffHandler.STAFF_SERVER_PACKET,mapOf(
                "from" to server.name,
                "player" to event.player.uniqueId.toString(),
                "displayName" to profile.getDisplayName()
            ))
        }
    }

}