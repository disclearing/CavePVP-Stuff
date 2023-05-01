package org.cavepvp.entity.type.hologram.line.type

import org.cavepvp.entity.EntityHandler
import org.cavepvp.entity.type.hologram.line.HologramLine
import com.squareup.moshi.JsonClass
import org.bukkit.ChatColor

import org.bukkit.Location
import org.bukkit.entity.Player
import org.cavepvp.entity.ai.ShardEntityAI

/**
 * @project carnage
 *
 * @date 24/02/2021
 * @author xanderume@gmail.com
 */
@JsonClass(generateAdapter = true)
class HologramTextLine(var text: String,location: Location) : HologramLine(location) {

    @Transient var blank = this.text == " " || this.text.equals("blank",true)
    @Transient var textLowerCase = this.text.toLowerCase()

    override fun render(player: Player) {

        var text = this.text

        for (map in EntityHandler.getAdapters().map{it.resolve(player)}) {

            for (entry in map) {

                if (!this.textLowerCase.contains(entry.key.toLowerCase())) {
                    continue
                }

                text = text.replace(entry.key,entry.value.toString(),true)
            }

        }

        ShardEntityAI.renderHologramTextLine(player,text,this)
    }

    override fun update(player: Player) {

        var text = this.text

        for (map in EntityHandler.getAdapters().map{it.resolve(player)}) {

            for (entry in map) {

                if (!this.textLowerCase.contains(entry.key.toLowerCase())) {
                    continue
                }

                text = text.replace(entry.key,entry.value.toString(),true)
            }

        }

        ShardEntityAI.updateHologramTextLine(player,text,this)
    }

    override fun destroy(player: Player) {
        ShardEntityAI.destroyHologramTextLine(player,this)
    }

    @JvmName("setText1")
    fun setText(value: String) {
        this.text = ChatColor.translateAlternateColorCodes('&',value)
        this.blank = this.text.equals("blank",true)
        this.textLowerCase = value.toLowerCase()
    }

}