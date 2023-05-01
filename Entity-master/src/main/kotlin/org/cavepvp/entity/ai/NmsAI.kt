package org.cavepvp.entity.ai

import org.bukkit.World
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import java.util.*

interface NmsAI {

    fun getLatency(player: Player):Int
    fun getProtocolVersion(player: Player):Int

    fun loadOfflinePlayerData(uuid: UUID,username: String):Player

    fun getName(item: ItemStack):String
    fun getInventoryName(inventory: Inventory):String

    fun getWorldData(world: World,x: Int,y: Int,z: Int):Int
    fun setSkullTexture(item: ItemStack,texture: String):ItemStack

    fun isActive(entity: Entity):Boolean
    fun isMobSpawner(entity: Entity):Boolean
    fun setMobSpawner(entity: Entity,spawner: Boolean)

    fun getWindowId(player: Player):Int
    fun getContainerCounter(player: Player):Int

    fun updateInventory(player: Player)
    fun updateInventoryWithoutEvent(player: Player,inventory: Inventory)

}