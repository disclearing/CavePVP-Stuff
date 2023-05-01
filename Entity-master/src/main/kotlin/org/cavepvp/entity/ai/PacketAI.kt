package org.cavepvp.entity.ai

import net.minecraft.util.com.mojang.authlib.GameProfile
import org.bukkit.Effect
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

interface PacketAI {

    fun sendPacket(player: Player,packet: Any)

    fun createChat(text: String,type: Int,flag: Boolean):Any
    fun createTabHeaderFooter(header: String,footer: String):Any

    fun createEquipment(id: Int,slot: Int,item: ItemStack?):Any

    fun createPlayerInfoAdd(ping: Int,gameMode: GameMode,username: String,profile: GameProfile):Any
    fun createPlayerInfoRemove(username: String,profile: GameProfile):Any
    fun createPlayerUpdateLatency(latency: Int,username: String,profile: GameProfile):Any
    fun createPlayerUpdateDisplayName(displayName: String,profile: GameProfile):Any

    fun createOpenSign(location: Location):Any
    fun createOpenWindow(window: Int,inventory: Inventory):Any

    fun createWindowItems(window: Int,items: List<ItemStack>):Any

    fun createPlayerChat(message: String):Any
    fun createBlockChange(type: Material,location: Location,fake: Boolean):Any

    fun createSpawnEntity(id: Int,location: Location,j: Int):Any
    fun createNamedEntitySpawn(id: Int,location: Location,profile: GameProfile,watcher: Any,item: ItemStack? = null):Any
    fun createSpawnEntityLiving(id: Int, typeId: Int, location: Location, watcher: Any,legacy: Boolean):Any

    fun createEntityAttach(id:Int,id1: Int,id2: Int):Any
    fun createEntityTeleport(id: Int,location: Location):Any
    fun createEntityAnimation(id: Int,animation: Int):Any

    fun createEntityLook(id: Int,yaw: Float,pitch: Float,onGround: Boolean):Any
    fun createEntityHeadRotation(id: Int,yaw: Float):Any
    fun createEntityDestroy(ids: IntArray):Any
    fun createEntityMetadata(id: Int,watcher: Any):Any

    fun createWorldParticles(effect: Effect,location: Location,offsetX: Float,offsetY: Float,offsetZ: Float,speed: Float,amount: Int):Any
}