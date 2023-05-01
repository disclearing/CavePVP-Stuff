package org.cavepvp.entity.animation

import org.cavepvp.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

interface EntityAnimation {

    fun onTick(entity: Entity)
    fun onTick(entity: Entity,player: Player) // this is where we send the packets

    fun getName():String
    fun getDisplayName():String
    fun getDisplayItem():ItemStack

    fun isSupported(entity: Entity):Boolean

    fun onDisable(entity: Entity) {

    }

    infix fun ClosedRange<Double>.step(step: Double): Iterable<Double> {

        require(this.start.isFinite()) //
        require(this.endInclusive.isFinite())
        require(step > 0.0) { "Step must be positive,was: $step." }

        val sequence = generateSequence(this.start) { previous ->

            if (previous == Double.POSITIVE_INFINITY) {
                return@generateSequence null
            }

            val next = previous + step

            if (next > this.endInclusive) {
                return@generateSequence null
            }

            return@generateSequence next
        }

        return sequence.asIterable()
    }

}