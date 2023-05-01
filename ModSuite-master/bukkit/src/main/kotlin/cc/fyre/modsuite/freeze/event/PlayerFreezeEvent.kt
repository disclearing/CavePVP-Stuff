package cc.fyre.modsuite.freeze.event

import org.bukkit.entity.Player
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent

class PlayerFreezeEvent(player: Player,val frozen: Boolean) : PlayerEvent(player) {

    private var cancelled = false
    var cancelledMessage: String? = null

    fun setCancelled(value: Boolean,message: String?) {
        this.cancelled = value
        this.cancelledMessage = message
    }

    fun call():PlayerFreezeEvent {
        return this
    }

    override fun getHandlers(): HandlerList {
        return handlerList
    }

    companion object {

        @JvmStatic private val handlerList = HandlerList()

        @JvmStatic
        fun getHandlerList(): HandlerList {
            return handlerList
        }

    }

}