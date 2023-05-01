package cc.fyre.modsuite.mod.listener

import cc.fyre.modsuite.ModSuite
import cc.fyre.modsuite.mod.command.BungeeTPCommand
import cc.fyre.modsuite.mod.packet.PidginNiggerPacket
import cc.fyre.proton.Proton
import cc.fyre.proton.pidgin.packet.handler.IncomingPacketHandler
import cc.fyre.proton.pidgin.packet.listener.PacketListener
import net.minecraft.util.com.google.common.cache.CacheBuilder
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import java.util.*
import java.util.concurrent.TimeUnit

object ModTeleportListener : Listener, PacketListener {

    private val queue = CacheBuilder.newBuilder()
        .expireAfterWrite(10L,TimeUnit.SECONDS)
        .build<UUID,UUID>()

    init {
        Proton.getInstance().pidginHandler.registerListener(this)
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private fun onPlayerJoin(event: PlayerJoinEvent) {

        if (!event.player.hasPermission(BungeeTPCommand.PERMISSION)) {
            return
        }

        val target = this.queue.getIfPresent(event.player.uniqueId) ?: return

        this.queue.invalidate(event.player.uniqueId)

        val player = Bukkit.getServer().getPlayer(target)

        if (player == null || !player.isOnline) {
            event.player.sendMessage("${ChatColor.RED}It appears this player has logged out..")
            return
        }

        Bukkit.getServer().scheduler.runTaskLater(ModSuite.instance,{

            if (!event.player.isOnline) {
                return@runTaskLater
            }

            event.player.teleport(player)
        },5L)

    }

    @IncomingPacketHandler
    fun onPlayerTeleport(packet: PidginNiggerPacket) {

        if (Bukkit.getServer().port != packet.port) {
            return
        }

        this.queue.put(packet.sender,packet.player)
    }

}