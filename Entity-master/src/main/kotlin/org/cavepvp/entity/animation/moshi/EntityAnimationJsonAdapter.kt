package org.cavepvp.entity.animation.moshi

import org.cavepvp.entity.animation.EntityAnimation
import org.cavepvp.entity.animation.EntityAnimationRegistry
import org.cavepvp.entity.animation.moshi.EntityAnimationSerializer
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

object EntityAnimationJsonAdapter {

    @ToJson
    fun toJson(animation: EntityAnimation):String {
        return animation.getName()
    }

    @FromJson
    fun fromJson(value: String): EntityAnimation {
        return EntityAnimationRegistry.getAnimationByName(value)!!
    }

}