package org.cavepvp.entity.listener

import org.cavepvp.entity.EntityHandler
import org.cavepvp.entity.event.EntityInteractEvent
import org.cavepvp.entity.type.npc.NPC
import com.comphenix.protocol.PacketType
import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketAdapter
import com.comphenix.protocol.events.PacketEvent
import com.comphenix.protocol.wrappers.EnumWrappers
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import org.cavepvp.entity.EntityPlugin
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * @project carnage
 *
 * @date 04/01/21
 * @author xanderume@gmail.com
 */
object EntityPacketListener : PacketAdapter(EntityPlugin.instance,PacketType.Play.Client.USE_ENTITY),Listener {

    private val cache = hashMapOf<UUID,Long>()
    private val cooldown = TimeUnit.SECONDS.toMillis(1L)

    init {
        ProtocolLibrary.getProtocolManager().addPacketListener(this)
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private fun onPlayerQuit(event: PlayerQuitEvent) {
        this.cache.remove(event.player.uniqueId)
    }

    override fun onPacketReceiving(event: PacketEvent) {

        if (event.packet.type != PacketType.Play.Client.USE_ENTITY) {
            return
        }

        val entity = EntityHandler.getEntityById(event.packet.integers.read(0)) ?: return

        if (entity.location.distanceSquared(event.player.location) > 6.0) {
            return
        }

        if (this.cache.containsKey(event.player.uniqueId) && ((System.currentTimeMillis() - this.cache[event.player.uniqueId]!! <= this.cooldown))) {
            return
        }

        val action = try {
            event.packet.entityUseActions.read(0)
            // has to be java nullpointer
        } catch (ex: java.lang.NullPointerException) {
            EnumWrappers.EntityUseAction.INTERACT
        }

        /*
        if (action == null || action == EnumWrappers.EntityUseAction.INTERACT_AT) {
            return
        }*/

        this.cache[event.player.uniqueId] = System.currentTimeMillis()

        if (action == EnumWrappers.EntityUseAction.ATTACK) {
            entity.onLeftClick(event.player)
        } else {

            if (entity is NPC) {
                entity.commands.forEach{event.player.chat("/$it")}
            }

            entity.onRightClick(event.player)
        }

        Bukkit.getServer().pluginManager.callEvent(EntityInteractEvent(entity,event.player,if (action == EnumWrappers.EntityUseAction.ATTACK) EntityInteractEvent.EntityInteractAction.LEFT_CLICK else EntityInteractEvent.EntityInteractAction.RIGHT_CLICK))
    }

}