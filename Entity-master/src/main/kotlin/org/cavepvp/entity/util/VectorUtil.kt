package org.cavepvp.entity.util

import org.bukkit.util.Vector
import kotlin.math.atan2

/**
 * @project carnage
 *
 * @date 27/02/2021
 * @author xanderume@gmail.com
 */
object VectorUtil {

    @JvmStatic
    fun convertYawFromVectors(a: Vector,b: Vector):Float {
        val dx = a.x - b.x
        val dz = a.z - b.z

        var angle = Math.toDegrees(atan2(dz,dx)).toFloat() - 90.0F

        if (angle < 0.0F) {
            angle += 360.0F
        }

        return angle
    }

}