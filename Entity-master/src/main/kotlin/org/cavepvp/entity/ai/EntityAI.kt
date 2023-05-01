package org.cavepvp.entity.ai

import org.cavepvp.entity.Entity
import org.cavepvp.entity.type.hologram.Hologram
import org.cavepvp.entity.type.hologram.line.type.HologramTextLine
import org.cavepvp.entity.type.npc.NPC
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

interface EntityAI {

    fun sendNPCCreatePacket(player: Player,npc: NPC)
    fun sendNPCUpdatePacket(player: Player,npc: NPC)
    fun sendNPCRefreshPacket(player: Player,npc: NPC)
    fun sendNPCDestroyPacket(player: Player,npc: NPC)

    fun sendNPCTagVisibility(player: Player,npc: NPC)

    fun sendHologramCreatePacket(player: Player,hologram: Hologram)
    fun sendHologramUpdatePacket(player: Player,hologram: Hologram)
    fun sendHologramRefreshPacket(player: Player,hologram: Hologram)
    fun sendHologramDestroyPacket(player: Player,hologram: Hologram)

    fun renderHologramTextLine(player: Player,text: String,line: HologramTextLine)
    fun updateHologramTextLine(player: Player,text: String,line: HologramTextLine)
    fun destroyHologramTextLine(player: Player,line: HologramTextLine)

    fun renderHologramItemLine(player: Player,item: ItemStack,line: HologramTextLine)

    fun handleEntityViewerTick(entity: Entity)
}