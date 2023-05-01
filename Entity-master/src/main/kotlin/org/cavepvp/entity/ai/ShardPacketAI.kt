package org.cavepvp.entity.ai

import net.minecraft.server.v1_7_R4.*
import net.minecraft.util.com.mojang.authlib.GameProfile
import org.bukkit.Effect
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftInventory
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.cavepvp.entity.util.PacketUtil
import org.cavepvp.entity.util.ReflectionUtil
import org.spigotmc.ProtocolInjector

object ShardPacketAI : PacketAI {

    override fun sendPacket(player: Player,packet: Any) {

        if (packet !is Packet) {
            throw IllegalStateException("${packet.javaClass.simpleName} is not a Packet!")
        }

        (player as CraftPlayer).handle.playerConnection.sendPacket(packet)
    }

    override fun createChat(text: String, type: Int, flag: Boolean): Any {
        return PacketPlayOutChat(ChatComponentText(text),2,flag)
    }

    override fun createTabHeaderFooter(header: String, footer: String): Any {
        return ProtocolInjector.PacketTabHeader(
            ChatSerializer.a(footer),
            ChatSerializer.a(header)
        )
    }

    override fun createEquipment(id: Int,slot: Int,item: ItemStack?): Any {
        return PacketPlayOutEntityEquipment(id,slot,CraftItemStack.asNMSCopy(item))
    }

    override fun createPlayerInfoAdd(ping: Int,gameMode: GameMode,username: String,profile: GameProfile): Any {

        val packet = PacketPlayOutPlayerInfo()

        ReflectionUtil.setDeclaredField(packet,"ping",ping)
        ReflectionUtil.setDeclaredField(packet,"action",PacketUtil.ADD_PLAYER)
        ReflectionUtil.setDeclaredField(packet,"player",profile)
        ReflectionUtil.setDeclaredField(packet,"gamemode",gameMode.ordinal)
        ReflectionUtil.setDeclaredField(packet,"username",username)

        return packet
    }

    override fun createPlayerInfoRemove(username: String,profile: GameProfile): Any {

        val packet = PacketPlayOutPlayerInfo()

        ReflectionUtil.setDeclaredField(packet,"action",PacketUtil.REMOVE_PLAYER)
        ReflectionUtil.setDeclaredField(packet,"player",profile)
        ReflectionUtil.setDeclaredField(packet,"username",username)

        return packet
    }

    override fun createPlayerUpdateLatency(latency: Int, username: String, profile: GameProfile): Any {

        val packet = PacketPlayOutPlayerInfo()

        ReflectionUtil.setDeclaredField(packet,"ping",latency)
        ReflectionUtil.setDeclaredField(packet,"action",PacketUtil.UPDATE_LATENCY)
        ReflectionUtil.setDeclaredField(packet,"player",profile)
        ReflectionUtil.setDeclaredField(packet,"username",username)

        return packet
    }

    override fun createPlayerUpdateDisplayName(displayName: String,profile: GameProfile): Any {

        val packet = PacketPlayOutPlayerInfo()

        ReflectionUtil.setDeclaredField(packet,"action",PacketUtil.UPDATE_DISPLAY_NAME)
        ReflectionUtil.setDeclaredField(packet,"player",profile)
        ReflectionUtil.setDeclaredField(packet,"username",displayName)

        return packet
    }

    override fun createOpenSign(location: Location):Any {
        return PacketPlayOutOpenSignEditor(location.blockX,location.blockY,location.blockZ)
    }

    override fun createOpenWindow(window: Int,inventory: Inventory): Any {

        if (inventory !is CraftInventory) {
            throw java.lang.IllegalStateException("Invalid Inventory?")
        }

        return PacketPlayOutOpenWindow(window,0,inventory.inventory.inventoryName,inventory.size,inventory.inventory.k_())
    }

    override fun createWindowItems(window: Int, items: List<ItemStack>): Any {
        return PacketPlayOutWindowItems(window,items.map{CraftItemStack.asNMSCopy(it)})
    }

    override fun createPlayerChat(message: String): Any {
        return PacketPlayOutChat(ChatSerializer.a(message))
    }

    override fun createBlockChange(type: Material, location: Location, fake: Boolean):Any {

        val packet = PacketPlayOutBlockChange()

        ReflectionUtil.setField(packet,"a",location.blockX)
        ReflectionUtil.setField(packet,"b",location.blockY)
        ReflectionUtil.setField(packet,"c",location.blockZ)
        ReflectionUtil.setField(packet,"fake",fake)
        ReflectionUtil.setField(packet,"data",(location.world as CraftWorld).handle.getData(location.blockX,location.blockY,location.blockZ))
        ReflectionUtil.setField(packet,"block",Block.getById(type.id))

        return packet
    }

    override fun createEntityLook(id: Int, yaw: Float, pitch: Float,onGround: Boolean): Any {

        val packet = PacketPlayOutEntityLook()

        ReflectionUtil.setField(packet,"a",id)
        ReflectionUtil.setField(packet,"b",MathHelper.d(PacketUtil.convertYawOrPitch(yaw)).toByte())
        ReflectionUtil.setField(packet,"c",MathHelper.d(PacketUtil.convertYawOrPitch(pitch)).toByte())
        ReflectionUtil.setDeclaredField(packet,"onGround",onGround)

        return packet
    }

    override fun createEntityHeadRotation(id: Int,yaw: Float):Any {

        val packet = PacketPlayOutEntityHeadRotation()

        ReflectionUtil.setDeclaredField(packet,"a",id)
        ReflectionUtil.setDeclaredField(packet,"b",PacketUtil.convertYawOrPitchAsByte(yaw))

        return packet
    }

    override fun createEntityDestroy(ids: IntArray): Any {
        return PacketPlayOutEntityDestroy(*ids)
    }

    override fun createEntityMetadata(id: Int, watcher: Any): Any {
        return PacketPlayOutEntityMetadata(id,watcher as DataWatcher,true)
    }

    override fun createWorldParticles(
        effect: Effect,
        location: Location,
        offsetX: Float,
        offsetY: Float,
        offsetZ: Float,
        speed: Float,
        amount: Int
    ): Any {
        return PacketPlayOutWorldParticles(effect.getName(),location.x.toFloat(),location.y.toFloat(),location.z.toFloat(),offsetX,offsetY,offsetZ,speed,amount)
    }

    override fun createNamedEntitySpawn(id: Int,location: Location,profile: GameProfile,watcher: Any,item: ItemStack?):Any {

        val packet = PacketPlayOutNamedEntitySpawn()

        ReflectionUtil.setDeclaredField(packet,"a",id)
        ReflectionUtil.setDeclaredField(packet,"b",profile)
        ReflectionUtil.setDeclaredField(packet,"c",MathHelper.floor(location.x * 32.0))
        ReflectionUtil.setDeclaredField(packet,"d",MathHelper.floor(location.y * 32.0))
        ReflectionUtil.setDeclaredField(packet,"e",MathHelper.floor(location.z * 32.0))
        ReflectionUtil.setDeclaredField(packet,"f",PacketUtil.convertYawOrPitchAsByte(location.yaw))
        ReflectionUtil.setDeclaredField(packet,"g",PacketUtil.convertYawOrPitchAsByte(location.pitch))
        ReflectionUtil.setDeclaredField(packet,"h",if (item == null) 0 else Item.getId(CraftItemStack.asNMSCopy(item).item))
        ReflectionUtil.setDeclaredField(packet,"i",watcher as DataWatcher)

        return packet
    }

    override fun createSpawnEntity(id: Int, location: Location, j: Int): Any {

        val packet = PacketPlayOutSpawnEntity()

        ReflectionUtil.setField(packet,"a",id)
        ReflectionUtil.setField(packet,"j",j)
        ReflectionUtil.setDeclaredField(packet,"b",(location.x * 32.0).toInt())
        ReflectionUtil.setDeclaredField(packet,"c",MathHelper.floor((location.y - 0.13 + 55.0) * 32.0))
        ReflectionUtil.setDeclaredField(packet,"d",(location.z * 32.0).toInt())
        ReflectionUtil.setDeclaredField(packet,"h",MathHelper.d(PacketUtil.convertYawOrPitch(location.pitch)))
        ReflectionUtil.setDeclaredField(packet,"i",MathHelper.d(PacketUtil.convertYawOrPitch(location.yaw)))

        return packet
    }

    override fun createSpawnEntityLiving(id: Int, typeId: Int, location: Location,watcher: Any,legacy: Boolean): Any {

        val packet = PacketPlayOutSpawnEntityLiving()

        ReflectionUtil.setDeclaredField(packet,"a",id)
        ReflectionUtil.setDeclaredField(packet,"b",typeId)
        ReflectionUtil.setDeclaredField(packet,"c",MathHelper.floor(location.x * 32.0))

        if (legacy) {
            ReflectionUtil.setDeclaredField(packet,"d",MathHelper.floor((location.y + 55.0) * 32.0))
        } else {
            ReflectionUtil.setDeclaredField(packet,"d",MathHelper.floor((location.y - 2.0) * 32.0))
        }

        ReflectionUtil.setDeclaredField(packet,"e",MathHelper.floor(location.z * 32.0))
        ReflectionUtil.setDeclaredField(packet,"i",PacketUtil.convertYawOrPitch(location.yaw).toInt().toByte())
        ReflectionUtil.setDeclaredField(packet,"j",PacketUtil.convertYawOrPitch(location.pitch).toInt().toByte())
        ReflectionUtil.setDeclaredField(packet,"l",watcher as DataWatcher)

        return packet
    }

    override fun createEntityAttach(id: Int, id1: Int, id2: Int):Any {

        val packet = PacketPlayOutAttachEntity()

        ReflectionUtil.setDeclaredField(packet,"a",id)
        ReflectionUtil.setDeclaredField(packet,"b",id1)
        ReflectionUtil.setDeclaredField(packet,"c",id2)

        return packet
    }

    override fun createEntityAnimation(id: Int,animation: Int):Any {

        val packet = PacketPlayOutAnimation()

        ReflectionUtil.setDeclaredField(packet,"a",id)
        ReflectionUtil.setDeclaredField(packet,"b",animation)

        return packet
    }

    override fun createEntityTeleport(id: Int, location: Location): Any {

        val packet = PacketPlayOutEntityTeleport()

        ReflectionUtil.setField(packet,"a",id)
        ReflectionUtil.setField(packet,"b",MathHelper.floor(location.x * 32.0))
        ReflectionUtil.setField(packet,"c",MathHelper.floor(location.y * 32.0))
        ReflectionUtil.setField(packet,"d",MathHelper.floor(location.z * 32.0))
        ReflectionUtil.setField(packet,"e",PacketUtil.convertYawOrPitch(location.yaw).toInt().toByte())
        ReflectionUtil.setField(packet,"f",PacketUtil.convertYawOrPitch(location.pitch).toInt().toByte())

        return packet
    }

}