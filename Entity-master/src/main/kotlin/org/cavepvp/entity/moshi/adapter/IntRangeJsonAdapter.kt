package org.cavepvp.entity.moshi.adapter

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

object IntRangeJsonAdapter {

    private const val SPLITTER = "\\"

    @ToJson
    fun toJson(range: IntRange):String {
        return "${range.first}$SPLITTER${range.last}"
    }

    @FromJson
    fun fromJson(json: String):IntRange {
        json.split(SPLITTER).also{
            return IntRange(it[0].toInt(),it[1].toInt())
        }
    }
}