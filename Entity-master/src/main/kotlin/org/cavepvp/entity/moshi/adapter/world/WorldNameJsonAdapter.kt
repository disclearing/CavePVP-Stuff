package org.cavepvp.entity.moshi.adapter.world

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import org.bukkit.Bukkit
import org.bukkit.World

object WorldNameJsonAdapter {

    @ToJson
    fun toJson(@WorldNameSerializer world: World):String {
        return world.name
    }

    @FromJson
    @WorldNameSerializer
    fun fromJson(value: String):World {
        return Bukkit.getServer().getWorld(value)
    }

}