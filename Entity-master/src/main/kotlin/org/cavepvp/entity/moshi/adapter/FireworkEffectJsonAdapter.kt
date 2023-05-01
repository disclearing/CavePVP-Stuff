package org.cavepvp.entity.moshi.adapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import com.squareup.moshi.Types
import org.bukkit.FireworkEffect
import java.lang.reflect.ParameterizedType

object FireworkEffectJsonAdapter {

    val LIST_TYPE: ParameterizedType = Types.newParameterizedType(List::class.java,FireworkEffect::class.java)

    @ToJson
    fun toJson(effect: FireworkEffect):Map<@JvmSuppressWildcards String,@JvmSuppressWildcards Any?> {
        return effect.serialize()
    }

    @FromJson
    fun fromJson(json: Map<@JvmSuppressWildcards String, @JvmSuppressWildcards Any>):FireworkEffect {
        return FireworkEffect.deserialize(json) as FireworkEffect
    }

}