package org.cavepvp.entity.moshi.adapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.time.LocalDateTime

object LocalDateTimeJsonAdapter {

    @ToJson
    fun toJson(time: LocalDateTime):String {
        return time.toString()
    }

    @FromJson
    fun fromJson(value: String):LocalDateTime {
        return LocalDateTime.parse(value)
    }

}