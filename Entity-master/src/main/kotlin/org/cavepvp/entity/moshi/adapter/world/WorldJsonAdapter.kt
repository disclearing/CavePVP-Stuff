package org.cavepvp.entity.moshi.adapter.world

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import org.bukkit.Bukkit
import org.bukkit.World
import java.util.*

object WorldJsonAdapter {

    @ToJson
    fun toJson(@WorldSerializer world: World):String {
        return world.uid.toString()
    }

    @FromJson
    @WorldSerializer
    fun fromJson(value: String):World {
        return Bukkit.getServer().getWorld(UUID.fromString(value))
    }

}