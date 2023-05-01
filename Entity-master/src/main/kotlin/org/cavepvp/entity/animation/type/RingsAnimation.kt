package org.cavepvp.entity.animation.type

import cc.fyre.shard.util.bukkit.ColorUtil
import org.cavepvp.entity.Entity
import org.cavepvp.entity.animation.EntityAnimation
import org.bukkit.ChatColor
import org.bukkit.Effect
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.NPC
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.cavepvp.entity.ai.ShardPacketAI
import org.cavepvp.entity.util.ItemBuilder
import org.cavepvp.entity.util.NMSUtil
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin

class RingsAnimation(private val color: ChatColor) : EntityAnimation {

    private var tick = 0
    private val distance = 0.8

    private val dyeColor = ColorUtil.getDyeColor(this.color)
    private val particleColor = this.dyeColor.color

    private var offsetX: Float = ColorUtil.convertColorValue(this.particleColor.red.toDouble()).toFloat()
    private var offsetY: Float = ColorUtil.convertColorValue(this.particleColor.green.toDouble()).toFloat()
    private var offsetZ: Float = ColorUtil.convertColorValue(this.particleColor.blue.toDouble()).toFloat()
    private val packets = arrayOfNulls<Any>(2)

    private lateinit var yin: Location
    private lateinit var yang: Location

    override fun onTick(entity: Entity) {
        this.tick++

        val location = entity.location.clone()

        location.x = floor(location.x) + 0.5
        location.y = location.y + 1.0
        location.z = floor(location.z) + 0.5

        val yAngle = if (this.tick + 1 >= 40) {
            (this.tick - 40 + 1) * (2 * Math.PI) / 80
        } else {
            (this.tick + 40 + 1) * (2 * Math.PI) / 80
        }

        val yinAngle = (this.tick + 1) * (2 * Math.PI) / 80

        this.yin = location.clone().add(this.distance * cos(yinAngle),0.0,this.distance * sin(yinAngle))
        this.yang = location.clone().add(this.distance * cos(yAngle),0.0,this.distance * sin(yAngle))
        this.packets[0] = ShardPacketAI.createWorldParticles(Effect.COLOURED_DUST,this.yin,this.offsetX,this.offsetY,this.offsetZ,1.0F,0)
        this.packets[1] = ShardPacketAI.createWorldParticles(Effect.COLOURED_DUST,this.yang,this.offsetX,this.offsetY,this.offsetZ,1.0F,0)

        if (this.tick >= 80) {
            this.tick = 0
        }

    }

    override fun onTick(entity: Entity,player: Player) {
        this.packets.forEach{NMSUtil.sendPacket(player,it!!)}
    }

    override fun getName(): String {
        return "Rings_${this.color.name}"
    }

    override fun getDisplayName(): String {
        return "${this.color}${ChatColor.BOLD}Rings"
    }

    override fun getDisplayItem(): ItemStack {
        return ItemBuilder.of(Material.WOOL)
            .data(this.dyeColor.woolData)
            .build()
    }

    override fun isSupported(entity: Entity): Boolean {
        return entity is NPC
    }

}