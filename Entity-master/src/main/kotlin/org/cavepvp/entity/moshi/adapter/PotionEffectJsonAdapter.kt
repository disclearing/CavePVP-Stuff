package org.cavepvp.entity.moshi.adapter

import com.squareup.moshi.*
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.cavepvp.entity.moshi.readBoolean
import org.cavepvp.entity.moshi.readInt
import org.cavepvp.entity.moshi.write
import java.lang.reflect.ParameterizedType

/**
 * @project carnage
 *
 * @date 28/02/2021
 * @author xanderume@gmail.com
 */
object PotionEffectJsonAdapter : JsonAdapter<PotionEffect>() {

    val LIST_TYPE: ParameterizedType = Types.newParameterizedType(List::class.java,PotionEffect::class.java)

    override fun toJson(writer: JsonWriter,effect: PotionEffect?) {

        if (effect == null) {
            writer.nullValue()
            return
        }

        writer.beginObject()
        writer.write("effect",effect.type.id)
        writer.write("duration",effect.duration)
        writer.write("amplifier",effect.amplifier)
        writer.write("ambient",effect.isAmbient)
        writer.endObject()
    }

    override fun fromJson(reader: JsonReader): PotionEffect? {
        if (!reader.hasNext()) {
            return null
        }

        reader.beginObject()

        return PotionEffect(
            PotionEffectType.getById(reader.readInt()),
            reader.readInt(),
            reader.readInt(),
            reader.readBoolean(),
        ).also{reader.endObject()}
    }

}