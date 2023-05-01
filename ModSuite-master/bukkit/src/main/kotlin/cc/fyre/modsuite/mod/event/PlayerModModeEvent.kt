package cc.fyre.modsuite.mod.event

import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.event.HandlerList
import org.bukkit.event.player.PlayerEvent

class PlayerModModeEvent(player: Player,val stage: ModModeStage,val executedBy: CommandSender,val force: Boolean) : PlayerEvent(player), Cancellable {

    private var cancelled = false
    var cancelledMessage: String? = null

    override fun isCancelled(): Boolean {
        return this.cancelled
    }

    @Deprecated(message = "Use setCancelled(boolean,message?) instead.",replaceWith = ReplaceWith("this.setCancelled(boolean,message?)"),level = DeprecationLevel.HIDDEN)
    override fun setCancelled(value: Boolean) {
        this.cancelled = value
    }

    fun setCancelled(value: Boolean,message: String?) {
        this.cancelled = value
        this.cancelledMessage = message
    }

    fun call():PlayerModModeEvent {
        Bukkit.getServer().pluginManager.callEvent(this)
        return this
    }

    enum class ModModeStage {

        ENTER,
        LEAVE

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