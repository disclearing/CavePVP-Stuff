package org.cavepvp.entity.util

/**
 * @project carnage
 *
 * @date 15/02/2021
 * @author xanderume@gmail.com
 */
object EntityUtil {

    private var count = Integer.MAX_VALUE

    @JvmStatic
    fun getEntityCount():Int {
        //return ENTITY_COUNT_FIELD.getInt(null)
        return count
    }

    @JvmStatic
    fun getNewEntityId():Int {
        return count--
    }

}