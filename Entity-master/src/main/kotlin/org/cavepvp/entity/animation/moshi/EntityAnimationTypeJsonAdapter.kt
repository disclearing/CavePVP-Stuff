package org.cavepvp.entity.animation.moshi

import org.cavepvp.entity.animation.EntityAnimation
import org.cavepvp.entity.animation.EntityAnimationRegistry
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

object EntityAnimationTypeJsonAdapter {

    @ToJson
    fun toJson(@EntityAnimationSerializer animation: EntityAnimation):String {
        return animation.getName()
    }

    @FromJson
    @EntityAnimationSerializer
    fun fromJson(value: String): EntityAnimation {
        return EntityAnimationRegistry.getAnimationByName(value)!!
    }

}