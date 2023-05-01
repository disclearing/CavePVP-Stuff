package org.cavepvp.entity.moshi.adapter

import org.cavepvp.entity.moshi.readDouble
import org.cavepvp.entity.moshi.readFloat
import org.cavepvp.entity.moshi.readString
import org.cavepvp.entity.moshi.write
import com.squareup.moshi.*
import org.bukkit.Bukkit
import org.bukkit.Location

/**
 * @project carnage
 *
 * @date 27/02/2021
 * @author xanderume@gmail.com
 */
object LocationJsonAdapter : JsonAdapter<Location>() {

    override fun toJson(writer: JsonWriter,location: Location?) {

        if (location == null) {
            writer.nullValue()
            return
        }

        writer.beginObject()

        writer.write("world",location.world.name)
        writer.write("x",location.x)
        writer.write("y",location.y)
        writer.write("z",location.z)
        writer.write("yaw",location.yaw)
        writer.write("pitch",location.pitch)

        writer.endObject()
    }

    override fun fromJson(reader: JsonReader): Location? {

        if (!reader.hasNext()) {
            return null
        }

        reader.beginObject()

        return Location(
            Bukkit.getServer().getWorld(reader.readString()),
            reader.readDouble(),
            reader.readDouble(),
            reader.readDouble(),
            reader.readFloat(),
            reader.readFloat(),
        ).also{reader.endObject()}
    }

}