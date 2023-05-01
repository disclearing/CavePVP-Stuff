package org.cavepvp.entity.event

import org.cavepvp.entity.EntityVisibility
import org.cavepvp.entity.type.npc.NPC
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/**
 * @project carnage
 *
 * @date 03/21/21
 * @author xanderume@gmail.com
 */
class NPCNameTagStateEvent(val npc: NPC, val new: EntityVisibility) : Event() {

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