package org.cavepvp.entity.type.npc

import cc.fyre.proton.menu.Button
import org.cavepvp.entity.Entity
import org.cavepvp.entity.EntityVisibility
import org.cavepvp.entity.event.NPCNameTagStateEvent
import org.cavepvp.entity.type.hologram.Hologram
import com.squareup.moshi.JsonClass
import net.minecraft.util.com.mojang.authlib.GameProfile
import net.minecraft.util.com.mojang.authlib.properties.Property
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.cavepvp.entity.ai.ShardEntityAI
import org.cavepvp.entity.ai.ShardPacketAI
import org.cavepvp.entity.util.EntityUtil
import org.cavepvp.entity.util.ItemPart
import org.cavepvp.entity.util.NMSUtil
import org.cavepvp.entity.util.PlayerUtil
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.abs
import kotlin.math.acos
import kotlin.math.sqrt
import kotlin.reflect.jvm.internal.impl.builtins.StandardNames.FqNames.target

/**
 * @project carnage
 *
 * @date 15/02/2021
 * @author xanderume@gmail.com
 */
@JsonClass(generateAdapter = true)
open class NPC(name: String,location: Location) : Entity(name,location,null) {

    val uuid: UUID = UUID.randomUUID()

    var swing = true
    var faces = false

    var commands = mutableListOf<String>()
    var hologram = Hologram("NPC_${this.name}_HOLOGRAM",location.clone().add(0.0,2.0,0.0),this.id)
    var equipment = arrayOfNulls<ItemStack>(5)

    var texture: String? = null
    var signature: String? = null
    var skinUsername: String? = null

    var tabVisibility = EntityVisibility.VISIBLE
    var tagVisibility = EntityVisibility.VISIBLE

    @Transient var batId = EntityUtil.getNewEntityId()
    @Transient var profile = GameProfile(this.uuid,this.name)
    @Transient val lookingTowards: MutableSet<UUID> = ConcurrentHashMap.newKeySet()

    override fun init() {

        if (this.name.length > 16) {
            this.name = this.name.substring(0,15)
        }

        this.profile.properties["textures"].add(Property("textures",this.texture ?: "",this.signature ?: ""))
    }

    override fun getEntityType(): EntityType {
        return EntityType.PLAYER
    }

    override fun sendCreatePacket(player: Player) {
        ShardEntityAI.sendNPCCreatePacket(player,this)

        this.sendEquipment(player)
        this.hologram.sendCreatePacket(player)
    }

    override fun sendUpdatePacket(player: Player) {
        ShardEntityAI.sendNPCUpdatePacket(player,this)
    }

    override fun sendRefreshPacket(player: Player) {
        ShardEntityAI.sendNPCRefreshPacket(player,this)
    }

    override fun sendDestroyPacket(player: Player) {
        ShardEntityAI.sendNPCDestroyPacket(player,this)
    }

    override fun updateLocation(location: Location) {
        this.location = location
        this.hologram.updateLocation(location.clone().add(
            0.0,
            if (this.tagVisibility == EntityVisibility.HIDDEN) 2+HOLOGRAM_DISTANCE else 2+HOLOGRAM_DISTANCE_WITH_NAME_TAG,
            0.0
        ))
    }

    fun setSkin(player: Player) {

        val property = NMSUtil.getGameProfile(player).properties["textures"].firstOrNull() ?: return

        this.texture = property.value
        this.signature = property.signature
        this.skinUsername = player.name

        val properties = this.profile.properties["textures"]

        properties.clear()
        properties.add(Property("textures",this.texture ?: "",this.signature ?: ""))

        this.sendToAll{
            this.sendDestroyPacket(it)
            this.sendCreatePacket(it)
        }
    }

    fun setSkin(username: String?,texture: String?,signature: String?) {

        this.texture = texture
        this.signature = signature
        this.skinUsername = username

        val properties = this.profile.properties["textures"]

        properties.clear()
        properties.add(Property("textures",this.texture ?: "",this.signature ?: ""))

        this.sendToAll{
            this.sendDestroyPacket(it)
            this.sendCreatePacket(it)
        }
    }

    fun updateTabVisibility(visibility: EntityVisibility) {

        if (this.tabVisibility == visibility) {
            return
        }

        val packet = if (visibility == EntityVisibility.HIDDEN) {
            ShardPacketAI.createPlayerInfoRemove(this.name,this.profile)
        } else {
            ShardPacketAI.createPlayerInfoAdd(0,GameMode.CREATIVE,this.name,this.profile)
        }

        this.tabVisibility = visibility
        this.sendToAll{PlayerUtil.sendPacket(it,packet)}
    }

    fun updateTagVisibility(visibility: EntityVisibility) {

        if (this.tagVisibility == visibility) {
            return
        }

        this.hologram.updateLocation(this.location.clone().add(
            0.0,
            if (this.tagVisibility == EntityVisibility.HIDDEN) HOLOGRAM_DISTANCE else HOLOGRAM_DISTANCE_WITH_NAME_TAG,
            0.0
        ))
        this.tagVisibility = visibility

        Bukkit.getPluginManager().callEvent(NPCNameTagStateEvent(this,visibility))
    }

    open fun getEquipment(slot: Int):ItemStack? {
        return this.equipment[slot]
    }

    open fun getEquipment(part: ItemPart):ItemStack? {
        return this.getEquipment(part.ordinal)
    }

    open fun setEquipment(slot: Int,item: ItemStack?) {

        if (this.equipment[slot] == item) {
            return
        }

        this.equipment[slot] = item

        ShardPacketAI.createEquipment(this.id,slot,item).also{packet ->
            this.sendToAll{PlayerUtil.sendPacket(it,packet)}
        }
    }

    open fun setEquipment(part: ItemPart,item: ItemStack?) {
        this.setEquipment(part.ordinal,item)
    }

    fun setEquipmentInternal(slot: Int,item: ItemStack?) {

        if (this.equipment[slot] == item) {
            return
        }

        this.equipment[slot] = item
    }

    fun setEquipmentInternal(part: ItemPart,item: ItemStack?) {
        this.setEquipmentInternal(part.ordinal,item)
    }

    fun sendEquipment(player: Player) {

        for (i in 0..this.equipment.lastIndex) {

            var item = this.equipment[i]

            if (item == null) {
                item = ItemStack(Material.AIR)
            }

            PlayerUtil.sendPacket(player,ShardPacketAI.createEquipment(this.id,i,item))
        }

    }

    fun ensureTagVisibility(player: Player) {
        ShardEntityAI.sendNPCTagVisibility(player,this)
    }

    @JvmName("setFaces1")
    fun setFaces(value: Boolean) {

        if (this.faces == value) {
            return
        }


        if (!value) {

            val packets = arrayOf(
                ShardPacketAI.createEntityHeadRotation(this.id,this.location.yaw),
                ShardPacketAI.createEntityLook(this.id,this.location.yaw,this.location.pitch,true),
                ShardPacketAI.createEntityAnimation(this.id,NPCAnimationType.SWING.id)
            )

            this.sendToAll{packets.forEach{packet -> PlayerUtil.sendPacket(it,packet)}}
        }

        this.faces = value
    }

    fun lookTowardsPlayer(player: Player,headOnly: Boolean) {

        val to = player.location.clone()
        val fromLocation = this.location.clone()

        val xDiff: Double = to.x - fromLocation.x
        val yDiff: Double = to.y - fromLocation.y
        val zDiff: Double = to.z - fromLocation.z

        val distanceXZ = sqrt(xDiff * xDiff + zDiff * zDiff)
        val distanceY = sqrt(distanceXZ * distanceXZ + yDiff * yDiff)

        var yaw = Math.toDegrees(acos(xDiff / distanceXZ))
        val pitch = Math.toDegrees(acos(yDiff / distanceY)) - 90

        if (zDiff < 0.0) {
            yaw += abs(180 - yaw) * 2
        }

        yaw -= 90

        if (headOnly) {
            PlayerUtil.sendPacket(player,ShardPacketAI.createEntityHeadRotation(this.id,yaw.toFloat()))
        } else {
            PlayerUtil.sendPacket(player,ShardPacketAI.createEntityHeadRotation(this.id,yaw.toFloat()))
            PlayerUtil.sendPacket(player,ShardPacketAI.createEntityLook(this.id,yaw.toFloat(),pitch.toFloat(),true))
        }

        if (this.lookingTowards.contains(player.uniqueId)) {
            return
        }

        this.lookingTowards.add(player.uniqueId)
    }

    open fun getEditorButtons():Map<Int,Button> {
        return mapOf()
    }

    companion object {

        const val FACE_DISTANCE = 5.0

        const val HOLOGRAM_DISTANCE = 0.095
        const val HOLOGRAM_DISTANCE_WITH_NAME_TAG = 0.3

    }

}