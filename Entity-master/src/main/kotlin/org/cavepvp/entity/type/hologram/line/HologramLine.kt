package org.cavepvp.entity.type.hologram.line

import com.squareup.moshi.JsonClass
import org.bukkit.Location
import org.bukkit.entity.Player
import org.cavepvp.entity.util.EntityUtil

/**
 * @project carnage
 *
 * @date 24/02/2021
 * @author xanderume@gmail.com
 */
@JsonClass(generateAdapter = false)
abstract class HologramLine(var location: Location) {

    @Transient val skullId = EntityUtil.getNewEntityId()
    @Transient val horseId = EntityUtil.getNewEntityId()

    abstract fun render(player: Player)
    abstract fun update(player: Player)
    abstract fun destroy(player: Player)

    companion object {

        const val ARMOR_STAND_ID = 30

    }

}