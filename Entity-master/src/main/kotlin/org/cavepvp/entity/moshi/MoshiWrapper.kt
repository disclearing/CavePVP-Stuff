package org.cavepvp.entity.moshi

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonReader
import com.squareup.moshi.JsonWriter

fun JsonWriter.write(key: String,value: String) {
    this.name(key)
    this.value(value)
}

fun JsonWriter.write(key: String,value: Int) {
    this.name(key)
    this.value(value)
}

fun JsonWriter.write(key: String,value: Short) {
    this.name(key)
    this.value(value)
}

fun JsonWriter.write(key: String,value: Double) {
    this.name(key)
    this.value(value)
}

fun JsonWriter.write(key: String,value: Float) {
    this.name(key)
    this.value(value)
}

fun JsonWriter.write(key: String,value: Boolean) {
    this.name(key)
    this.value(value)
}

fun JsonWriter.write(key: String,map: Map<String,Any>) {
    this.name(key)

    val array = this.beginArray()

    map.entries.forEach{(name,value) ->
        array.name(name)

        when (value) {
            is Long -> array.value(value)
            is Float -> array.value(value)
            is Double -> array.value(value)
            is Number -> array.value(value)
            is String -> array.value(value)
            is Boolean -> array.value(value)
            else -> array.value(value.toString())
        }

    }
    array.endArray()
}

fun JsonReader.readString():String {
    this.nextName()
    return this.nextString()
}

fun JsonReader.readInt():Int {
    this.nextName()
    return this.nextInt()
}

fun JsonReader.readDouble():Double {
    this.nextName()
    return this.nextDouble()
}

fun JsonReader.readFloat():Float {
    this.nextName()
    return this.nextDouble().toFloat()
}

fun JsonReader.readBoolean():Boolean {
    this.nextName()
    return this.nextBoolean()
}

fun <T> JsonAdapter<T>.setPrettyPrinting():JsonAdapter<T> {
    return this.indent(MoshiUtil.PRETTY_PRINT_INDENT)
}