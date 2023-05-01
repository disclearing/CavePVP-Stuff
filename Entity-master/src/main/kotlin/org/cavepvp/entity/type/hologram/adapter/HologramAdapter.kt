package org.cavepvp.entity.type.hologram.adapter

import org.bukkit.entity.Player

/**
 * @author brew@atheist.com
 *
 * @date 4/24/2021
 * @project carnage
 */
interface HologramAdapter {
    
    fun resolve(player: Player): HashMap<String,Any>
    
}