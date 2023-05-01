package org.cavepvp.entity.moshi.adapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.util.concurrent.atomic.AtomicInteger

object AtomicIntegerJsonAdapter {

    @ToJson
    fun toJson(value: AtomicInteger):String {
        return value.get().toString()
    }

    @FromJson
    fun fromJson(value: String):AtomicInteger {
        return AtomicInteger(value.toInt())
    }
}