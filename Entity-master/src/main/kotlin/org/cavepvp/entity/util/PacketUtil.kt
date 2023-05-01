package org.cavepvp.entity.util

import com.comphenix.protocol.ProtocolLibrary
import com.comphenix.protocol.events.PacketContainer
import org.bukkit.entity.Player
import java.lang.reflect.InvocationTargetException

object PacketUtil {

    const val ADD_PLAYER = 0
    const val REMOVE_PLAYER = 4

    const val UPDATE_LATENCY = 2
    const val UPDATE_GAMEMODE = 1
    const val UPDATE_DISPLAY_NAME = 3

    @JvmStatic
    fun convertYawOrPitchAsByte(value: Float):Byte {
        return (value * 256.0F / 360.0F).toInt().toByte()
    }

    @JvmStatic
    fun convertYawOrPitch(value: Float):Float {
        return (value * 256.0F / 360.0F)
    }

    @JvmStatic
    fun convertYawOrPitchAsByte(value: Byte):Float {
        return (value * 256.0F / 360.0F)
    }

    @JvmStatic
    fun broadcast(packet: PacketContainer) {
        ProtocolLibrary.getProtocolManager().broadcastServerPacket(packet)
    }

    @JvmStatic
    fun sendPacket(player: Player,packet: PacketContainer) {

        try {
            ProtocolLibrary.getProtocolManager().sendServerPacket(player,packet)
        } catch (ex: InvocationTargetException) {
            throw RuntimeException("Cannot send packet.",ex)
        }

    }

    @JvmStatic
    fun receivePacket(sender: Player,packet: PacketContainer) {

        try {
            ProtocolLibrary.getProtocolManager().recieveClientPacket(sender,packet)
        } catch (ex: Exception) {
            throw RuntimeException("Cannot receive packet.",ex)
        }

    }
}