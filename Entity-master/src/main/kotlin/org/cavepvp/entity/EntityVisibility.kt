package org.cavepvp.entity

import org.apache.commons.lang.WordUtils
import org.bukkit.Bukkit
import java.util.function.Predicate

/**
 * @project carnage
 *
 * @date 15/02/2021
 * @author xanderume@gmail.com
 */
enum class EntityVisibility(val action: Predicate<Entity>,private var displayName: String? = null) {

    HIDDEN(Predicate<Entity>{
        it.sendToAll{player -> it.sendDestroyPacket(player)}
        it.viewers.clear()
        return@Predicate true
    }),

    VISIBLE(Predicate<Entity>{
        Bukkit.getServer().onlinePlayers.forEach{player ->

            if (it.location.world != player.world) {
                return@forEach
            }

            if (it.getDistanceSquared(player.location) > Entity.DISTANCE) {
                return@forEach
            }

            it.viewers.add(player.uniqueId)
            it.sendCreatePacket(player)
        }
        return@Predicate true
    });

    fun getDisplayName():String {

        if (this.displayName == null) {
            this.displayName = WordUtils.capitalizeFully(this.name.replace("_"," ").toLowerCase())
        }

        return this.displayName!!
    }
}