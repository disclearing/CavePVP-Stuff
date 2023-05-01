package org.cavepvp.entity.moshi.json

import org.cavepvp.entity.moshi.MoshiUtil
import com.squareup.moshi.Moshi

class JsonObject {

    private var json = "{}"
    private var jsonUpdateRequired = true

    constructor() {
        this.entries = mutableMapOf()
    }

    constructor(entries: Map<*,*>) {

        val newEntries = mutableMapOf<String,Any>()

        for (entry in entries) {

            if (entry.key !is String || entry.value == null) {
                continue
            }

            newEntries[entry.key as String] = entry.value!!
        }

        this.entries = newEntries
    }

    private var entries: MutableMap<String,Any>

    fun get():Map<String,Any> {
        return this.entries
    }

    operator fun set(key: String,value: Any) {
        this.entries[key] = value
        this.jsonUpdateRequired = true
    }

    fun containsKey(key: String):Boolean {
        return this.entries.containsKey(key)
    }

    fun getInt(key: String):Int? {

        val value = this.entries[key]

        if (value is Double) {
            return value.toInt()
        }

        return value as? Int
    }

    fun getLong(key: String):Long? {

        val value = this.entries[key]

        if (value is Double) {
            return value.toLong()
        }

        return value as? Long
    }

    fun getFloat(key: String):Float? {
        return this.entries[key] as? Float
    }

    fun getString(key: String):String? {
        return this.entries[key] as? String
    }

    fun getDouble(key: String):Double? {
        return this.entries[key] as? Double
    }

    fun getBoolean(key: String):Boolean? {
        return this.entries[key] as? Boolean
    }

    fun getJsonObject(key: String): JsonObject {
        val value = this.entries[key]
        return when {
            value is Map<*,*> -> JsonObject(value)
            value is String && value.isNotEmpty() && value[0] == '{' && value[value.lastIndex] == '}' -> {

                val fromJson = fromJson(value)

                // re-update value in cache to a Map so we don't have to deserialize again.
                this.entries[key] = fromJson.get()

                fromJson
            }
            else -> JsonObject()
        }

    }

    fun toJson(moshi: Moshi):String {

        if (this.jsonUpdateRequired) {
            this.json = moshi.adapter<Map<String,Any>>(MoshiUtil.STRING_TO_ANY_MAP_TYPE).toJson(this.entries)!!
        }

        return this.json
    }

    companion object {

        fun fromJson(value: String): JsonObject {
            return JsonObject(MoshiUtil.instance.adapter<Map<String,Any>>(MoshiUtil.STRING_TO_ANY_MAP_TYPE).fromJson(value)!!)
        }

    }
}