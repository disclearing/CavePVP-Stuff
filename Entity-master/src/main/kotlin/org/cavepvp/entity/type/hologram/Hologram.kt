package org.cavepvp.entity.type.hologram

import org.cavepvp.entity.Entity
import org.cavepvp.entity.EntityHandler
import org.cavepvp.entity.type.hologram.line.HologramLine
import org.cavepvp.entity.type.hologram.line.type.HologramItemLine
import org.cavepvp.entity.type.hologram.line.type.HologramTextLine
import com.squareup.moshi.JsonClass
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.cavepvp.entity.ai.ShardEntityAI

/**
 * @project carnage
 *
 * @date 15/02/2021
 * @author xanderume@gmail.com
 */
@JsonClass(generateAdapter = true)
open class Hologram(name: String,location: Location,parent: Int? = null) : Entity(name,location,parent) {

    var lines = mutableListOf<HologramLine>()

    override fun init() {}

    override fun getEntityType(): EntityType {
        return EntityType.WITHER_SKULL
    }

    override fun sendCreatePacket(player: Player) {
        ShardEntityAI.sendHologramCreatePacket(player,this)
    }

    override fun sendUpdatePacket(player: Player) {
        ShardEntityAI.sendHologramUpdatePacket(player,this)
    }

    override fun sendRefreshPacket(player: Player) {
        ShardEntityAI.sendHologramRefreshPacket(player,this)
    }

    override fun sendDestroyPacket(player: Player) {
        ShardEntityAI.sendHologramDestroyPacket(player,this)
    }

    operator fun set(index: Int,text: String) {
        this.setText(index,text)
    }

    operator fun set(index: Int,item: ItemStack) {
        this.setItem(index,item)
    }

    fun addText(text: String) {

        for (line in this.lines) {
            line.location.add(0.0,TEXT_IN_BETWEEN_DISTANCE,0.0)
        }

        this.lines.add(HologramTextLine(text,this.location.clone().subtract(0.0,TEXT_IN_BETWEEN_DISTANCE,0.0)))
        this.sendToAll{this.sendRefreshPacket(it)}
    }

    fun addItem(item: ItemStack) {

        for (line in this.lines) {
            line.location.add(0.0, TEXT_IN_BETWEEN_DISTANCE,0.0)
        }

        this.lines.add(HologramItemLine(item,this.location.clone().subtract(0.0, TEXT_IN_BETWEEN_DISTANCE,0.0)))
        this.sendToAll{this.sendRefreshPacket(it)}
    }

    fun setText(index: Int,text: String) {

        if (index > this.lines.lastIndex) {
            this.addText(text)
            return
        }

        val line = this.lines[index]

        if (line is HologramTextLine) {
            line.setText(text)
            this.sendToAll{line.update(it)}
            return
        }

        this.lines[index] = HologramTextLine(text,line.location.clone().add(0.0, TEXT_IN_BETWEEN_DISTANCE,0.0))
        this.sendToAll{line.destroy(it);this.lines[index].render(it)}
    }

    fun setText(vararg lines: String) {

        for (i in lines.indices) {

            if (i <= this.lines.lastIndex) {
                this.setText(i,lines[i])
            } else {
                this.addText(lines[i])
            }

        }

    }

    fun setItem(index: Int,item: ItemStack) {

        if (index > this.lines.lastIndex) {
            this.addItem(item)
            return
        }

        val line = this.lines[index]

        if (line is HologramItemLine) {
            line.item = item
            this.sendToAll{line.update(it)}
            return
        }

        this.lines[index] = HologramItemLine(item,line.location.clone().subtract(0.0, TEXT_IN_BETWEEN_DISTANCE,0.0))
        this.sendToAll{line.destroy(it);this.lines[index].render(it)}
    }

    fun remove(index: Int) {

        if (index > this.lines.lastIndex) {
            return
        }

        val removed = this.lines.removeAt(index)

        this.sendToAll{removed.destroy(it)}

        val toReload = this.lines.withIndex().filter{it.index >= index}.map{it.value}

        this.sendToAll{toReload.forEach{hologram -> hologram.destroy(it);hologram.render(it)}}
    }

    override fun updateLocation(location: Location) {
        this.sendToAll{this.sendDestroyPacket(it)}
        this.location = location

        val clone = this.lines.toList()

        this.lines.clear()

        clone.forEach{

            when (it) {
                is HologramItemLine -> this.addItem(it.item)
                is HologramTextLine -> this.addText(it.text)
            }

        }

        //TODO PacketPlayOutEntityTeleport

        this.sendToAll{this.sendCreatePacket(it)}
    }

    override fun sendToAll(lambda: (player: Player) -> Unit) {

        if (this.parent == null) {
            super.sendToAll(lambda)
            return
        }

       EntityHandler.getEntityById(this.parent!!)?.sendToAll(lambda) ?: super.sendToAll(lambda)
    }

    companion object {

        const val TEXT_IN_BETWEEN_DISTANCE = 0.23
        const val ITEM_IN_BETWEEN_DISTANCE = 0.46

    }
}