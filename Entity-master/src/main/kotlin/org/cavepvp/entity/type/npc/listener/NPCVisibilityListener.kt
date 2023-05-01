package org.cavepvp.entity.type.npc.listener

import org.cavepvp.entity.event.NPCNameTagStateEvent
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

/**
 * @project carnage
 *
 * @date 03/21/21
 * @author xanderume@gmail.com
 */
object NPCVisibilityListener: Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    private fun onStateChange(event: NPCNameTagStateEvent) {
        event.npc.sendToAll{event.npc.ensureTagVisibility(it)}
    }

}