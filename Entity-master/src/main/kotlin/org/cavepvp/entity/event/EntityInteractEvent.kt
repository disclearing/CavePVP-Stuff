package org.cavepvp.entity.event

import org.cavepvp.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/**
 * @author brew@atheist.com
 *
 * @date 4/24/2021
 * @project carnage
 */
class EntityInteractEvent(val entity: Entity,val player: Player,val action: EntityInteractAction) : Event() {

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

    enum class EntityInteractAction {
        RIGHT_CLICK,
        LEFT_CLICK
    }

}