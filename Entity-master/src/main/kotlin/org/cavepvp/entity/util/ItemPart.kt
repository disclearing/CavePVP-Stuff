package org.cavepvp.entity.util

/**
 * @project carnage
 *
 * @date 04/26/21
 * @author xanderume@gmail.com
 */
enum class ItemPart {

    HAND,
    HELMET,
    CHESTPLATE,
    LEGGINGS,
    BOOTS;

    companion object {

        fun findBySlot(slot: Int): ItemPart? {
            return values().firstOrNull{it.ordinal == slot}
        }

    }

}